package co.edu.uniquindio.campusuq.emails;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import pub.devrel.easypermissions.EasyPermissions;

public class EmailsActivity extends MainActivity implements EmailsAdapter.OnClickEmailListener,
        EasyPermissions.PermissionCallbacks {

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean oldEmails = true;

    private IntentFilter emailsFilter = new IntentFilter(WebService.ACTION_EMAILS);
    private BroadcastReceiver emailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebService.PENDING_ACTION = WebService.ACTION_NONE;
            Intent exceptionIntent = intent.getParcelableExtra("INTENT");

            if (exceptionIntent == null) {
                loadEmails(intent.getIntExtra("INSERTED", 0));
            } else {
                swipeRefreshLayout.setRefreshing(true);
                startActivityForResult(exceptionIntent, EmailsPresenter.REQUEST_AUTHORIZATION);
            }

        }
    };

    /**
     * Constructor que oculta el ícono de navegación para reemplazarlo por la flecha de ir atrás.
     */
    public EmailsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de anuncios en la actividad superior, se
     * crea el adaptador de correos y el manejador de diseño lineal y se asignan al recilador de
     * vista, al cual tambien se le asigna un listener de actualización encargado de actualizar
     * desde el servidor la base de datos local al realizar un desplasamiento vetical en el limite
     * superior, y un listener de desplasamiento encargado cargar mas correos desde la base de datos
     * local al realizar un desplasamiento vetical en el limite inferior, y finalmente llama a la
     * funcion para cargar los correos.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_emails);
        viewStub.inflate();

        FloatingActionButton insert = findViewById(R.id.fab);
        swipeRefreshLayout = findViewById(R.id.emails_swipe_refresh);
        recyclerView = findViewById(R.id.emails_recycler_view);

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmailsActivity.this,
                        EmailsDetailActivity.class);
                intent.putExtra(Utilities.CATEGORY, R.string.institutional_mail);
                startActivityForResult(intent, 0);
            }
        });
        insert.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.haveNetworkConnection(EmailsActivity.this)) {
                    oldEmails = false;
                    WebBroadcastReceiver.startService(EmailsActivity.this,
                            WebService.ACTION_EMAILS, WebService.METHOD_GET,
                            null);
                } else {
                    Toast.makeText(EmailsActivity.this,
                            R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new EmailsAdapter(new ArrayList<Email>(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_SETTLING &&
                        !recyclerView.canScrollVertically(1)) {
                    oldEmails = true;
                    loadEmails(0);
                }
            }
        });

        loadEmails(0);
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

            if (recyclerView != null) {
                ArrayList<Email> emails = ((EmailsAdapter) recyclerView.getAdapter()).getEmails();

                for (Email email : emails) {
                    if(StringUtils.stripAccents(email.getName()).toLowerCase()
                            .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                        recyclerView.smoothScrollToPosition(emails.indexOf(email));
                        return;
                    }
                }
            }

            Toast.makeText(this, getString(R.string.email_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            setIntent(intent);
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(intent.getIntExtra(Utilities.CATEGORY, R.string.app_name));
                loadEmails(0);
            }
        }
    }

    /**
     * Carga los correos desde la base de datos y los almacena en el adaptador, , adicionalmente
     * muestra un mensaje de de carga durante el tiempo que realiza el proceso.
     * @param inserted Indica la cantidad de correos insertados.
     */
    private void loadEmails(int inserted) {
        swipeRefreshLayout.setRefreshing(true);

        EmailsAdapter emailsAdapter = (EmailsAdapter) recyclerView.getAdapter();
        int scrollTo = oldEmails ?
                emailsAdapter.getItemCount() - 1 : (inserted != 0 ? inserted - 1 : 0);

        EmailsSQLiteController dbController = new EmailsSQLiteController(this, 1);
        emailsAdapter.setEmails(dbController
                .select("" + emailsAdapter.getItemCount() + (inserted > 0 ? inserted : 6)));
        dbController.destroy();
        recyclerView.getLayoutManager().scrollToPosition(scrollTo);

        if (emailsAdapter.getItemCount() == 0 &&
                !WebService.PENDING_ACTION.equals(WebService.ACTION_EMAILS)) {
            WebService.PENDING_ACTION = WebService.ACTION_EMAILS;
            WebBroadcastReceiver.startService(this, WebService.ACTION_EMAILS,
                    WebService.METHOD_GET, null);
        }

        if (emailsAdapter.getItemCount() > 0) swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onEmailClick(int index) {
        Email email = ((EmailsAdapter) recyclerView.getAdapter()).getEmails().get(index);
        startActivity(new Intent(this, EmailsContentActivity.class)
                .putExtra(Utilities.CATEGORY, R.string.institutional_mail)
                .putExtra(EmailsSQLiteController.columns[1], email.getName())
                .putExtra(EmailsSQLiteController.columns[2], email.getFrom())
                .putExtra(EmailsSQLiteController.columns[4], email.getDate())
                .putExtra(EmailsSQLiteController.columns[6], email.getContent()));
    }

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra un
     * receptor para estar atento a los intentos relacionados con los correos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(emailsReceiver, emailsFilter);
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del receptor para los correos.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(emailsReceiver);
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case EmailsPresenter.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    swipeRefreshLayout.setRefreshing(true);
                    WebBroadcastReceiver.startService(this, WebService.ACTION_EMAILS,
                            WebService.METHOD_GET, null);
                } else {
                    Toast.makeText(this,
                            R.string.email_authorization,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

}
