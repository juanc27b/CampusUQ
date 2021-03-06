package co.edu.uniquindio.campusuq.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.reflect.Method;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsActivity;
import co.edu.uniquindio.campusuq.dishes.DishesActivity;
import co.edu.uniquindio.campusuq.emails.EmailsActivity;
import co.edu.uniquindio.campusuq.items.ItemsActivity;
import co.edu.uniquindio.campusuq.items.ItemsPresenter;
import co.edu.uniquindio.campusuq.maps.MapsActivity;
import co.edu.uniquindio.campusuq.news.NewsActivity;
import co.edu.uniquindio.campusuq.notifications.NotificationsActivity;
import co.edu.uniquindio.campusuq.notifications.NotificationsDetailActivity;
import co.edu.uniquindio.campusuq.objects.ObjectsActivity;
import co.edu.uniquindio.campusuq.quotas.QuotasActivity;
import co.edu.uniquindio.campusuq.radio.RadioActivity;
import co.edu.uniquindio.campusuq.users.LoginActivity;
import co.edu.uniquindio.campusuq.users.User;
import co.edu.uniquindio.campusuq.users.UsersActivity;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.AnalyticsApplication;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebActivity;
import co.edu.uniquindio.campusuq.web.WebContentActivity;
import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Actividad principal de la que heredan la mayoría de actividades de la aplicación, la cual provee
 * funciones básicas como el panel lateral de navegación, la flecha para retroceder, la opción para
 * realizar búsquedas de ítems, el menú overflow, y la interacción mediante BroadcastReceiver para
 * garantizar un correcto comportamiento de la navegación.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private LinearLayout accountLayout;
    private TextView account;

    private boolean hasSearch;
    private boolean hasNavigationDrawerIcon;

    public ProgressDialog progressDialog;

    public Tracker mTracker;

    private String language;

    /**
     * Se define un filtro de intentos para el BroadcastReceiver principal.
     */
    public IntentFilter mainFilter = new IntentFilter();

    /**
     * Se define un BroadcastReceiver para que el servicio que se encarga de las operaciones web le
     * pueda comunicar a la actividad que ha terminado de cargar/descargar los datos, y se define
     * el callback para saber qué hacer cuando esto ocurre. El BroadcastReceiver de la actividad
     * principal se encarga de abrir la actividad correspondiente a una funcionalidad que el usuario
     * seleccionó en una situación previa en la que la aplicación no había descargado los datos
     * necesarios, y lo hace cuando el servicio web le informa que estos datos se han recibido.
     */
    public BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebService.PENDING_ACTION = WebService.ACTION_NONE;
            String action = intent.getAction();

            if (progressDialog.isShowing() && action != null) switch (action) {
                case WebService.ACTION_WELFARE:
                    loadInformations(R.string.institutional_welfare, context);
                    break;
                case WebService.ACTION_CONTACTS:
                    startActivity(new Intent(context, ItemsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.directory));
                    break;
                case WebService.ACTION_PROGRAMS:
                    startActivity(new Intent(context, ItemsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.academic_offer));
                    break;
                case WebService.ACTION_CALENDAR:
                    startActivity(new Intent(context, ItemsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.academic_calendar));
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Constructor de la actividad principal en el que se agregan las acciones a filtrar con el
     * filtro de intentos del BroadcastReceiver principal.
     */
    public MainActivity() {
        setHasSearch(true);
        setHasNavigationDrawerIcon(true);

        mainFilter.addAction(WebService.ACTION_WELFARE);
        mainFilter.addAction(WebService.ACTION_CONTACTS);
        mainFilter.addAction(WebService.ACTION_PROGRAMS);
        mainFilter.addAction(WebService.ACTION_CALENDAR);
    }

    /**
     * Método que adjunta el contexto necesario para el correcto funcionamiento de la opción de
     * cambio de idioma.
     * @param newBase Contexto a adjuntar.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.getLanguage(newBase));
    }

    /**
     * Método llamado durante el ciclo de vida para crear la actividad, se encarga de crear la barra
     * de acción para la actividad, el panel lateral de navegación, el diálogo de progreso a mostrar
     * cuando se está cargando contenido, inicializa las herramientas necesarias para utilizar
     * Google Analytics, y por último llama al método que se encarga de añadir el contenido
     * específico de la actividad.
     * @param savedInstanceState Parámetro usado para recuperar estados anteriores de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        // Se manejan acciones contenidas en el intento, como búsquedas de ítems.
        handleIntent(intent);

        int category = intent.getIntExtra(Utilities.CATEGORY, R.string.app_name);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(category != R.string.app_name ?
                getString(category) : intent.getStringExtra(Utilities.CATEGORY));
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(hasNavigationDrawerIcon);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        accountLayout = view.findViewById(R.id.account_layout);
        account = view.findViewById(R.id.account);

        progressDialog = Utilities.getProgressDialog(this, true);

        // Se obtiene la instancia del rastreador compartido.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        addContent(savedInstanceState);
    }

    /**
     * Método útilizado para cambiar la configuración de la actividad en un intento de abrirla
     * nuevamente, ya que esto último no es posible para actividades lanzadas en modo SingleTop
     * @param intent Intento usado para abrir nuevamente la actividad.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Método llamado cuando se navega hacia atrás, se encarga de cerrar primero el panel lateral de
     * navegación en caso de que el mismo esté abierto.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    /**
     * Método llamado para crear los ítems del menú overflow, también crea la opción para buscar
     * ítems y la muestra u oculta dependiendo del tipo de actividad que se esté creando.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú, lo que añade ítems a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.main, menu);

        // Se obtiene el SearchView y la configuración de búsqueda.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Se asume que esta actividad será la que maneje las búsquedas.
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Se muestra u oculta la opción para buscar.
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(hasSearch);

        return true;
    }

    /**
     * Método llamado antes de que se muestre el menú overflow, se encarga de hacer visibles los
     * íconos de los ítems del menú.
     * @param menu Menú a mostrar.
     * @return Verdadero o falso según se vaya a mostrar el menú.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null && "MenuBuilder".equals(menu.getClass().getSimpleName())) try {
            Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",
                    Boolean.TYPE);
            m.setAccessible(true);
            m.invoke(menu, true);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(),
                    "onMenuOpened...unable to set icons for overflow menu", e);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Método útilizado para definir el comportamiento de los ítems del menú cuando los mismos son
     * seleccionados por el usuario.
     * @param item Ítem del menú que ha sido seleccionado.
     * @return Verdadero o falso según se quiera procesar la acción.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Se manejan los clicks de los ítems de la barra de acción.
        switch (item.getItemId()) {
            // Se responde al botón Home/Up de la barra de acción.
            case android.R.id.home: {
                if (hasNavigationDrawerIcon) drawer.openDrawer(GravityCompat.START);
                else onBackPressed();
                return true;
            }
            case R.id.action_change_language:
                Utilities.changeLanguage(this);
                recreate();
                return true;
            case R.id.action_adjust_notifications: {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_notifications_category))
                        .setAction(getString(R.string.analytics_view_action))
                        .setLabel(getString(R.string.analytics_adjust_notifications_label))
                        .setValue(1)
                        .build());
                startActivity(new Intent(this, NotificationsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.action_adjust_notifications));
                return true;
            }
            case R.id.action_notification_tray:
                startActivity(new Intent(this, NotificationsDetailActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.action_notification_tray));
                return true;
            case R.id.action_delete_history:
                startActivity(new Intent(this, HistoryActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.action_delete_history));
                return true;
            case R.id.action_login: {
                User user = UsersPresenter.loadUser(this);

                if (user != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_users_category))
                            .setAction(getString(R.string.analytics_view_action))
                            .setLabel(getString(R.string.analytics_login_label))
                            .setValue(1)
                            .build());
                    Intent intent;

                    if ("campusuq@uniquindio.edu.co".equals(user.getEmail())) {
                        intent = new Intent(this, LoginActivity.class)
                                .putExtra(Utilities.CATEGORY, R.string.log_in);
                    } else {
                        intent = new Intent(this, UsersActivity.class)
                                .putExtra(Utilities.CATEGORY, R.string.edit_account)
                                .putExtra("USER", user);
                    }

                    startActivity(intent);
                }
                return true;
            }
            case R.id.action_credits: {
                startActivity(new Intent(this, CreditsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.action_credits));
                return true;
            }
            default:
                // La acción no fue reconocida.
                // Se invoca a la superclase para que la maneje.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Método que maneja los clicks en los ítems del panel lateral de navegación (Home).
     * Se define un comportamiento para cada ítem del panel.
     * @param item Ítem del panel de navegación en el que se ha hecho click.
     * @return Verdadero o falso según se quiera procesar la acción.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = null;
        String category = null, label = null, action = getString(R.string.analytics_view_action);

        switch (item.getItemId()) {
            case R.id.nav_events:
                category = getString(R.string.analytics_news_category);
                label = getString(R.string.analytics_events_label);
                intent = new Intent(this, NewsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.events);
                break;
            case R.id.nav_news:
                category = getString(R.string.analytics_news_category);
                label = getString(R.string.analytics_news_label);
                intent = new Intent(this, NewsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.news);
                break;
            case R.id.nav_institution:
                intent = new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.institution);
                break;
            case R.id.nav_directory:
                category = getString(R.string.analytics_contatcs_category);
                label = getString(R.string.analytics_directory_label);
                WebService.PENDING_ACTION = WebService.ACTION_CONTACTS;
                intent = new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.directory);
                break;
            case R.id.nav_academic_offer:
                category = getString(R.string.analytics_programs_category);
                label = getString(R.string.analytics_academic_offer_label);
                WebService.PENDING_ACTION = WebService.ACTION_PROGRAMS;
                intent = new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.academic_offer);
                break;
            case R.id.nav_academic_calendar:
                category = getString(R.string.analytics_events_category);
                label = getString(R.string.analytics_academic_calendar_label);
                WebService.PENDING_ACTION = WebService.ACTION_CALENDAR;
                intent = new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.academic_calendar);
                break;
            case R.id.nav_employment_exchange:
                category = getString(R.string.analytics_web_category);
                label = getString(R.string.analytics_employment_exchange_label);
                intent = new Intent(this, WebActivity.class)
                        .putExtra(Utilities.URL, getString(R.string.employment_exchange_url));
                break;
            case R.id.nav_institutional_welfare:
                category = getString(R.string.analytics_informations_category);
                label = getString(R.string.analytics_institutional_welfare);
                WebService.PENDING_ACTION = WebService.ACTION_WELFARE;
                loadInformations(R.string.institutional_welfare, this);
                break;
            case R.id.nav_university_map:
                category = getString(R.string.analytics_maps_category);
                label = getString(R.string.analytics_university_map_label);
                intent = new Intent(this, MapsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.university_map);
                break;
            case R.id.nav_library_services:
                intent = new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.library_services);
                break;
            case R.id.nav_radio:
                category = getString(R.string.analytics_radio_category);
                label = getString(R.string.analytics_radio_label);
                intent = new Intent(this, RadioActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.radio);
                break;
            case R.id.nav_pqrsd_system:
                category = getString(R.string.analytics_web_category);
                label = getString(R.string.analytics_pqrsd_system_label);
                intent = new Intent(this, WebActivity.class)
                        .putExtra(Utilities.URL,
                                getString(R.string.pqrsd_system_url));
                break;
            case R.id.nav_lost_objects:
                category = getString(R.string.analytics_objects_category);
                label = getString(R.string.analytics_lost_objects_label);
                intent = new Intent(this, ObjectsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.lost_objects);
                break;
            case R.id.nav_security_system:
                category = getString(R.string.analytics_announcements_category);
                label = getString(R.string.analytics_security_system_label);
                intent = new Intent(this, AnnouncementsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.security_system);
                break;
            case R.id.nav_restaurant:
                category = getString(R.string.analytics_dishes_category);
                label = getString(R.string.analytics_restaurant_label);
                intent = new Intent(this, DishesActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.restaurant);
                break;
            case R.id.nav_billboard_information:
                category = getString(R.string.analytics_announcements_category);
                label = getString(R.string.analytics_billboard_information_label);
                intent = new Intent(this, AnnouncementsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.billboard_information);
                break;
            case R.id.nav_computer_rooms:
                category = getString(R.string.analytics_quotas_category);
                label = getString(R.string.analytics_computer_rooms_label);
                intent = new Intent(this, QuotasActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.computer_rooms);
                break;
            case R.id.nav_parking_lots:
                category = getString(R.string.analytics_quotas_category);
                label = getString(R.string.analytics_parking_lots_label);
                intent = new Intent(this, QuotasActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.parking_lots);
                break;
            case R.id.nav_laboratories:
                category = getString(R.string.analytics_quotas_category);
                label = getString(R.string.analytics_laboratories_label);
                intent = new Intent(this, QuotasActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.laboratories);
                break;
            case R.id.nav_studio_zones:
                category = getString(R.string.analytics_quotas_category);
                label = getString(R.string.analytics_study_areas_label);
                intent = new Intent(this, QuotasActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.study_areas);
                break;
            case R.id.nav_cultural_and_sport:
                category = getString(R.string.analytics_quotas_category);
                label = getString(R.string.analytics_cultural_and_sport_label);
                intent = new Intent(this, QuotasActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.cultural_and_sport);
                break;
            case R.id.nav_auditoriums:
                category = getString(R.string.analytics_quotas_category);
                label = getString(R.string.analytics_auditoriums_label);
                intent = new Intent(this, QuotasActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.auditoriums);
                break;
            case R.id.nav_institutional_mail: {
                User user = UsersPresenter.loadUser(this);

                if (user != null) {
                    if (user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                        category = getString(R.string.analytics_users_category);
                        label = getString(R.string.analytics_login_label);
                        intent = new Intent(this, LoginActivity.class)
                                .putExtra(Utilities.CATEGORY, R.string.log_in);
                    } else {
                        category = getString(R.string.analytics_emails_category);
                        label = getString(R.string.analytics_institutional_mail_label);
                        intent = new Intent(this, EmailsActivity.class)
                                .putExtra(Utilities.CATEGORY, R.string.institutional_mail);
                    }
                }
                break;
            }
            case R.id.nav_web_page:
                category = getString(R.string.analytics_web_category);
                label = getString(R.string.analytics_web_page_label);
                intent = new Intent(this, WebActivity.class)
                        .putExtra(Utilities.URL, getString(R.string.web_page_url));
                break;
            case R.id.nav_ecotic:
                category = getString(R.string.analytics_web_category);
                label = getString(R.string.analytics_ecotic_label);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.ecotic_url)));
                break;
            default:
                break;
        }

        if (category != null) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(1)
                    .build());
        }

        if (intent != null) startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Método utlizado para manejar acciones contenidas en el intento, como las búsquedas de ítems.
     * @param intent Intento usado para abrir la actividad.
     */
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, getString(R.string.query) + query,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método usado para definir si la actividad tendrá la opción de realizar búsquedas o no.
     * @param hasSearch Verdadero o falso según se quiera que la actividad maneje búsquedas.
     */
    public void setHasSearch(boolean hasSearch) {
        this.hasSearch = hasSearch;
    }

    /**
     * Método usado para definir si la actividad tendrá un botón de Home (panel de navegación
     * lateral) o un botón  de Up (flecha para retroceder), conservando en ambos casos la
     * posibilidad de abrir el panel de navegación con un deslizamiento horizontal.
     * @param hasNavigationDrawerIcon Verdadero para tener el botón Home y falso para el botón Up.
     */
    public void setHasNavigationDrawerIcon(boolean hasNavigationDrawerIcon) {
        this.hasNavigationDrawerIcon = hasNavigationDrawerIcon;
    }

    /**
     * Método usado para establecer una imágen de fondo para la actividad según la orientación.
     * @param portraitBackground Imágen de fondo para orientación vertical.
     * @param landscapeBackground Imágen de fondo para orientación horizontal.
     */
    public void setBackground(int portraitBackground, int landscapeBackground) {
        ImageView background = findViewById(R.id.background_image);
        background.setImageResource(getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT ? portraitBackground : landscapeBackground);
    }

    /**
     * Método usado para añadir el contenido específico de la actividad.
     * @param savedInstanceState Parámetro usado para recuperar estados anteriores de la actividad.
     */
    public void addContent(Bundle savedInstanceState) {}

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra el
     * BroadcastReceiver principal para abrir actividades previamente seleccionadas cuando se hayan
     * descargado los datos necesarios.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mainReceiver, mainFilter);

        User user = UsersPresenter.loadUser(this);

        if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail())) {
            accountLayout.setVisibility(View.VISIBLE);
            account.setText(user.getName());
        } else {
            accountLayout.setVisibility(View.GONE);
        }

        String language = getSharedPreferences(Utilities.PREFERENCES, Context.MODE_PRIVATE)
                .getString(Utilities.PREFERENCE_LANGUAGE, Utilities.LANGUAGE_ES);

        if (this.language == null) {
            this.language = language;
        } else if (!this.language.equals(language)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            }, 10);
        }
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del BroadcastReceiver principal.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mainReceiver);
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawer = null;
        accountLayout = null;
        account = null;
        progressDialog = null;
        mTracker = null;
        language = null;
    }

    /**
     * Método encargado de obtener la información necesaria desde la base de datos local para las
     * funcionalidades de Institución (Símbolos) y Bienestar institucional, y después abrir las
     * actividades correspondientes o mostrar mensajes al usuario si no están disponibles los datos.
     * @param type Tipo de información a cargar (Símbolos o Bienestar institucional).
     * @param context Contexto necesario para cargar los datos.
     */
    public static void loadInformations(int type, Context context) {
        ProgressDialog progressDialog = ((MainActivity) context).progressDialog;
        if (!progressDialog.isShowing()) progressDialog.show();
        String[] content = new String[2];

        if (type == R.string.symbols) {
            content = ItemsPresenter.getInformation("Simbolos", context);
        } else if (type == R.string.institutional_welfare) {
            content = ItemsPresenter
                    .getInformation("Cursos Culturales y Deportivos", context);
        }

        if (progressDialog.isShowing() && content[0] != null) {
            progressDialog.dismiss();
            context.startActivity(new Intent(context, WebContentActivity.class)
                    .putExtra(Utilities.CATEGORY, type)
                    .putExtra(Utilities.LINK, content[0])
                    .putExtra("CONTENT", content[1]));
        } else if (content[0] == null && !Utilities.haveNetworkConnection(context)) {
            Toast.makeText(context, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
        }
    }

}
