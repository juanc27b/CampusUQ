package co.edu.uniquindio.campusuq.announcements;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.LoginActivity;
import co.edu.uniquindio.campusuq.users.User;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Actividad para visualizar los anuncios de incidentes y comunicados.
 */
public class AnnouncementsActivity extends MainActivity implements
        AnnouncementsAdapter.OnClickAnnouncementListener, View.OnClickListener {

    public static final int REQUEST_ANNOUNCEMENT_DETAIL = 1005;

    private String action;
    private Button report;
    private FloatingActionButton fab;
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private ArrayList<AnnouncementLink> announcementsLinks;
    private boolean newActivity = true;
    private AnnouncementsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean oldAnnouncements = true;

    private String socialNetwork = AnnouncementsAdapter.UNDEFINED;

    private CallbackManager facebookCallbackManager;
    public boolean facebookLoggedIn;
    public ShareDialog shareDialog;

    public TwitterAuthClient mTwitterAuthClient;
    public Callback<TwitterSession> twitterCallback;
    public boolean twitterLoggedIn;

    private IntentFilter announcementsFilter = new IntentFilter(WebService.ACTION_INCIDENTS);
    private BroadcastReceiver announcementsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAnnouncements(intent.getIntExtra("INSERTED", 0));
            String response = intent.getStringExtra("RESPONSE");

            if (response != null) {
                Log.i(AnnouncementsActivity.class.getSimpleName(), response);
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Constructor que oculta el ícono de navegación para reemplazarlo por la flecha de ir atrás.
     */
    public AnnouncementsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de anuncios en la actividad superior, y
     * llama a la funcion para cargar los anuncios.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_announcements);
        viewStub.inflate();
        report = findViewById(R.id.report_incident);
        fab = findViewById(R.id.fab);

        report.setOnClickListener(this);
        fab.setOnClickListener(this);
        changeConfiguration();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookLoggedIn = true;
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });
        facebookLoggedIn = AccessToken.getCurrentAccessToken() == null;
        shareDialog = new ShareDialog(this);

        Twitter.initialize(this);
        mTwitterAuthClient = new TwitterAuthClient();
        twitterCallback = new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                twitterLoggedIn = true;
            }
            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                exception.printStackTrace();
            }
        };
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterAuthToken authToken = session != null ? session.getAuthToken() : null;
        twitterLoggedIn = authToken != null;

        loadAnnouncements(0);
    }

    /**
     * Método para manejar nuevas llamadas a la actividad, dependiendo de la accion del intento,
     * puede buscar un ítem, o cambiar el titulo de la actividad y volver a cargar los ítems.
     * @param intent Intento que contiene la accion a realizar.
     */
    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            for (Announcement announcement : announcements) {
                if (announcement.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                    layoutManager.scrollToPosition(announcements.indexOf(announcement));
                    return;
                }
            }

            Toast.makeText(this, getString(R.string.announcement_no_found) + ": " +
                    query, Toast.LENGTH_SHORT).show();
        } else {
            String category = intent.getStringExtra("CATEGORY");
            action = getString(R.string.security_system).equals(category) ?
                    WebService.ACTION_INCIDENTS : WebService.ACTION_COMMUNIQUES;
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(category);
                changeConfiguration();
                loadAnnouncements(0);
            }
        }
    }

    public void changeConfiguration() {
        if (WebService.ACTION_INCIDENTS.equals(action)) {
            fab.setVisibility(View.GONE);
            report.setVisibility(View.VISIBLE);
        } else {
            report.setVisibility(View.GONE);
            User user = UsersPresenter.loadUser(this);
            fab.setVisibility(user != null && user.getAdministrator().equals("S") ?
                    View.VISIBLE : View.GONE);
        }
    }

    /**
     * Carga los anuncios desde la base de datos y los almacena en el arreglo de anuncios para
     * enviarselos al adaptador, si la actividad es nueva el arreglo se envia por medio de su
     * constructor, se crea tambien el manejador de diseño y se asignan al recilador de vista, al
     * cual tambien se le asigna un listener de desplasamiento encargado de actualizar desde el
     * servidor la base de datos local al realizar un desplasamiento vetical en el limite
     * superior o cargar mas anuncios desde la base de datos local al realizar un desplasamiento
     * vetical en el limite inferior, adicionalmente muestra un mensaje de de carga durante el
     * tiempo que realiza el proceso.
     * @param inserted Indica la cantidad de objetos perdidos insertados.
     */
    private void loadAnnouncements(int inserted) {
        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldAnnouncements ?
                (newActivity ? 0 : announcements.size() - 1) :
                (inserted > 0 ? inserted - 1 : 0);

        announcements = AnnouncementsPresenter.loadAnnouncements(action, this,
                announcements.size() + (inserted > 0 ? inserted : 3));

        String[] announcement_IDs = new String[announcements.size()];
        for (int i = 0; i < announcement_IDs.length; i++) {
            announcement_IDs[i] = announcements.get(i).get_ID();
        }
        announcementsLinks =
                AnnouncementsPresenter.getAnnouncementsLinks(this, announcement_IDs);

        if (newActivity) {
            newActivity = false;
            adapter = new AnnouncementsAdapter(announcements, announcementsLinks,
                    this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.announcements_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (Utilities
                                    .haveNetworkConnection(AnnouncementsActivity.this)) {
                                oldAnnouncements = false;
                                progressDialog.show();
                                WebBroadcastReceiver.startService(AnnouncementsActivity.this,
                                        action, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(AnnouncementsActivity.this,
                                        R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        } else if (!recyclerView.canScrollVertically(1)) {
                            oldAnnouncements = true;
                            loadAnnouncements(0);
                        }
                    }
                }
            });
        } else {
            adapter.setAnnouncements(announcements, announcementsLinks);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && announcements.size() > 0) progressDialog.dismiss();
    }

    /**
     * Dependiendo de la accion, en caso haber iniciado sesión como administrador o ser propietario
     * del anuncio, puede mostrar un cuadro de dialogo que permite modificar o eliminar el anuncio
     * (o un mensaje de advertencia en caso contrario), puede llamar a otra aplicacion que permita
     * visualizar con mas detalle uno de los enlaces del anuncio, puede marcar el anuncio como leido
     * o compartirlo en redes sociales.
     * @param index Indice del anuncio que determina a cuál de los ítems del arreglo de anuncios se
     *              le aplicará la accion.
     * @param action Determina si se le ha dado clic al anuncio, a uno de sus enlaces (imagen o
     *               video), al boton de leido, facebook, twitter o whatsapp.
     */
    @Override
    public void onAnnouncementClick(int index, String action) {
        switch (action) {
            case AnnouncementsAdapter.ANNOUNCEMENT: {
                User user = UsersPresenter.loadUser(this);

                if (user != null && "S".equals(user.getAdministrator())) {
                    AnnouncementsFragment.newInstance(announcements.get(index), this.action)
                            .show(getSupportFragmentManager(), null);
                } else if (WebService.ACTION_INCIDENTS.equals(this.action)) {
                    if (user != null &&
                            user.get_ID().equals(announcements.get(index).getUser_ID())) {
                        AnnouncementsFragment.newInstance(announcements.get(index), this.action)
                                .show(getSupportFragmentManager(), null);
                    } else {
                        Toast.makeText(this,
                                R.string.no_propietary,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this,
                            R.string.no_administrator,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case AnnouncementsAdapter.IMAGE_0:
            case AnnouncementsAdapter.IMAGE_1:
            case AnnouncementsAdapter.IMAGE_2:
            case AnnouncementsAdapter.IMAGE_3:
            case AnnouncementsAdapter.IMAGE_4:
            case AnnouncementsAdapter.IMAGE_5:
            case AnnouncementsAdapter.IMAGE_6:
            case AnnouncementsAdapter.IMAGE_7:
            case AnnouncementsAdapter.IMAGE_8:
            case AnnouncementsAdapter.IMAGE_9:
                try {
                    String _ID = announcements.get(index).get_ID();
                    int link = Integer.parseInt(action.substring(action.length() - 1));

                    for (AnnouncementLink announcementLink : announcementsLinks) {
                        if (announcementLink.getAnnouncement_ID().equals(_ID) && link-- == 0) {
                            if (announcementLink.getLink() != null) {
                                startActivity(new Intent(Intent.ACTION_VIEW)
                                        .setDataAndType(FileProvider.getUriForFile(this,
                                                "co.edu.uniquindio.campusuq.provider",
                                                new File(announcementLink.getLink())),
                                                "I".equals(announcementLink.getType()) ?
                                                        "image/*" : "video/*")
                                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
                            }
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case AnnouncementsAdapter.READ: {
                AnnouncementsPresenter.readed(this, announcements.get(index).get_ID());
                loadAnnouncements(0);
                break;
            }
            case AnnouncementsAdapter.FACEBOOK:
                if (Utilities.haveNetworkConnection(this)) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_announcements_category))
                            .setAction(getString(R.string.analytics_share_action))
                            .setLabel(getString(WebService.ACTION_INCIDENTS.equals(this.action) ?
                                    R.string.analytics_security_system_label :
                                    R.string.analytics_billboard_information_label))
                            .setValue(1)
                            .build());
                    socialNetwork = AnnouncementsAdapter.FACEBOOK;
                    if (!facebookLoggedIn) {
                        LoginManager.getInstance().logInWithReadPermissions(this,
                                Arrays.asList("public_profile", "user_friends"));
                    } else if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentTitle(announcements.get(index).getName())
                                .setContentDescription(announcements.get(index).getDescription())
                                .build();
                        shareDialog.show(content);
                    }
                } else {
                    Toast.makeText(this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case AnnouncementsAdapter.TWITTER:
                if (Utilities.haveNetworkConnection(this)) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_announcements_category))
                            .setAction(getString(R.string.analytics_share_action))
                            .setLabel(getString(WebService.ACTION_INCIDENTS.equals(this.action) ?
                                    R.string.analytics_security_system_label :
                                    R.string.analytics_billboard_information_label))
                            .setValue(1)
                            .build());
                    if (!twitterLoggedIn) {
                        mTwitterAuthClient.authorize(this, twitterCallback);
                    } else {
                        startActivity(new ComposerActivity.Builder(this)
                                .session(TwitterCore
                                        .getInstance().getSessionManager().getActiveSession())
                                .text(announcements.get(index).getName())
                                .hashtags("#Uniquindio")
                                .createIntent());
                    }
                } else {
                    Toast.makeText(this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case AnnouncementsAdapter.WHATSAPP:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_announcements_category))
                        .setAction(getString(R.string.analytics_share_action))
                        .setLabel(getString(WebService.ACTION_INCIDENTS.equals(this.action) ?
                                R.string.analytics_security_system_label :
                                R.string.analytics_billboard_information_label))
                        .setValue(1)
                        .build());

                try {
                    startActivity(new Intent(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_TEXT, announcements.get(index).getName())
                            .setType("text/plain")
                            .setPackage("com.whatsapp"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.no_whatsapp,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, getString(R.string.undefined) + ": " + index,
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Define la tarea a realizar cuando se da click en una de las vistas controladas por esta
     * actividad, en caso de dar click en el boton de añadir se abrirá la actividad que permite
     * insertar un nuevo anuncio.
     * @param view Vista a la cual el usuario ha dado click.
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.report_incident:
            case R.id.fab: {
                User user = UsersPresenter.loadUser(this);

                if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail())) {
                    startActivityForResult(
                            new Intent(this, AnnouncementsDetailActivity.class)
                                    .putExtra("CATEGORY",
                                            getString(id == R.id.report_incident ?
                                                    R.string.report_incident : R.string.billboard_detail))
                                    .putExtra(AnnouncementsFragment.ANNOUNCEMENT,
                                            new Announcement(null, user.get_ID(), null,
                                                    null, null, null,
                                                    null)),
                            REQUEST_ANNOUNCEMENT_DETAIL);
                } else {
                    startActivity(new Intent(this, LoginActivity.class)
                            .putExtra("CATEGORY", getString(R.string.log_in)));
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * Muestra el cuadro de dialogo de progreso si la actividad que permite modificar o insertar un
     * anuncio resulta en un codigo correcto.
     * @param requestCode Código de solicitud para el cual se espera un resultado.
     * @param resultCode Código de resultado que indica exito o fracaso.
     * @param data Datos retornados por la actividad (no utilizado).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ANNOUNCEMENT_DETAIL:
                if (resultCode  == RESULT_OK && !progressDialog.isShowing()) progressDialog.show();
                break;
            default:
                switch (socialNetwork) {
                    case AnnouncementsAdapter.FACEBOOK:
                        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                        break;
                    case AnnouncementsAdapter.TWITTER:
                        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
                        break;
                    default:
                        break;
                }
                if (resultCode == RESULT_OK){
                    Toast.makeText(this, R.string.social_ok, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.social_error,
                            Toast.LENGTH_SHORT).show();
                }
                socialNetwork = AnnouncementsAdapter.UNDEFINED;
                break;
        }
    }

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra un
     * receptor para estar atento a los intentos relacionados con los anuncios.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(announcementsReceiver, announcementsFilter);
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del receptor para los anuncios.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(announcementsReceiver);
    }

}
