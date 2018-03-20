package co.edu.uniquindio.campusuq.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.ItemsPresenter;
import co.edu.uniquindio.campusuq.util.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Item;
import co.edu.uniquindio.campusuq.vo.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean hasSearch;
    private boolean hasNavigationDrawerIcon;

    public ProgressDialog progressDialog;

    public IntentFilter mainFilter = new IntentFilter();

    public MainActivity() {
        setHasSearch(true);
        setHasNavigationDrawerIcon(true);

        mainFilter.addAction(WebService.ACTION_WELFARE);
        mainFilter.addAction(WebService.ACTION_CONTACTS);
        mainFilter.addAction(WebService.ACTION_PROGRAMS);
        mainFilter.addAction(WebService.ACTION_CALENDAR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the intent, verify the action and get the query
        handleIntent(getIntent());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("CATEGORY"));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(hasNavigationDrawerIcon);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressDialog = Utilities.getProgressDialog(MainActivity.this);

        addContent(savedInstanceState);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(hasSearch);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (hasNavigationDrawerIcon) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
                return true;
            case R.id.action_change_language:
                return true;
            case R.id.action_adjust_notifications:
                return true;
            case R.id.action_delete_history:
                return true;
            case R.id.action_login:
                User user = UsersPresenter.loadUser(MainActivity.this);
                if (user != null) {
                    if (user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("CATEGORY", getString(R.string.log_in));
                    } else {
                        intent = new Intent(MainActivity.this, UsersActivity.class);
                        intent.putExtra("CATEGORY", getString(R.string.edit_account));
                        intent.putExtra("USER", user);
                    }
                    MainActivity.this.startActivity(intent);
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = null;
        int id = item.getItemId();
        switch(id) {
            case R.id.nav_events:
                intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.events));
                break;
            case R.id.nav_news:
                intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news));
                break;
            case R.id.nav_institution:
                intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.institution));
                break;
            case R.id.nav_directory:
                WebService.PENDING_ACTION = WebService.ACTION_CONTACTS;
                loadContactCategories(MainActivity.this);
                break;
            case R.id.nav_academic_offer:
                WebService.PENDING_ACTION = WebService.ACTION_PROGRAMS;
                loadPrograms(MainActivity.this);
                break;
            case R.id.nav_academic_calendar:
                WebService.PENDING_ACTION = WebService.ACTION_CALENDAR;
                loadEventCategories(MainActivity.this);
                break;
            case R.id.nav_employment_exchange:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.employment_exchange_url));
                break;
            case R.id.nav_institutional_welfare:
                WebService.PENDING_ACTION = WebService.ACTION_WELFARE;
                loadInformations(getString(R.string.institutional_welfare), MainActivity.this);
                break;
            case R.id.nav_university_map:
                intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.university_map));
                break;
            case R.id.nav_library_services:
                intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.library_services));
                break;
            case R.id.nav_radio:
                intent = new Intent(MainActivity.this, RadioActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.radio));
                break;
            case R.id.nav_pqrsd_system:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.pqrsd_system_url));
                break;
            case R.id.nav_lost_objects:
                intent = new Intent(MainActivity.this, ObjectsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.lost_objects));
                break;
            case R.id.nav_security_system:
                intent = new Intent(MainActivity.this, AnnouncementsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.security_system));
                break;
            case R.id.nav_restaurant:
                intent = new Intent(MainActivity.this, DishesActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.restaurant));
                break;
            case R.id.nav_billboard_information:
                intent = new Intent(MainActivity.this, AnnouncementsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.billboard_information));
                break;
            case R.id.nav_computer_rooms:
                intent = new Intent(MainActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.computer_rooms));
                break;
            case R.id.nav_parking_lots:
                intent = new Intent(MainActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.parking_lots));
                break;
            case R.id.nav_laboratories:
                intent = new Intent(MainActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.laboratories));
                break;
            case R.id.nav_studio_zones:
                intent = new Intent(MainActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.studio_zones));
                break;
            case R.id.nav_cultural_and_sport:
                intent = new Intent(MainActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.cultural_and_sport));
                break;
            case R.id.nav_auditoriums:
                intent = new Intent(MainActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.auditoriums));
                break;
            case R.id.nav_institutional_mail:
                User user = UsersPresenter.loadUser(MainActivity.this);
                if (user != null) {
                    if (user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("CATEGORY", getString(R.string.log_in));
                    } else {
                        intent = new Intent(MainActivity.this, EmailsActivity.class);
                        intent.putExtra("CATEGORY", getString(R.string.institutional_mail));
                    }
                }
                break;
            case R.id.nav_web_page:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.web_page_url));
                break;
            case R.id.nav_ecotic:
                intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.ecotic_url));
                break;
            default:
                break;
        }
        if (intent != null) {
            MainActivity.this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Consulta: "+query, Toast.LENGTH_SHORT).show();
        }
    }

    public void setHasSearch(boolean hasSearch) {
        this.hasSearch = hasSearch;
    }

    public void setHasNavigationDrawerIcon(boolean hasNavigationDrawerIcon) {
        this.hasNavigationDrawerIcon = hasNavigationDrawerIcon;
    }

    public void setBackground(int portraitBackground, int landscapeBackground) {
        ImageView background = (ImageView) findViewById(R.id.background_image);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            background.setImageResource(portraitBackground);
        } else {
            background.setImageResource(landscapeBackground);
        }
    }

    public void addContent(Bundle savedInstanceState) {

    }

    // Define the callback for what to do when data is received
    public BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebService.PENDING_ACTION = WebService.ACTION_NONE;
            if (progressDialog.isShowing()) {
                switch (intent.getAction()) {
                    case WebService.ACTION_WELFARE:
                        loadInformations(getString(R.string.institutional_welfare), MainActivity.this);
                        break;
                    case WebService.ACTION_CONTACTS:
                        loadContactCategories(MainActivity.this);
                        break;
                    case WebService.ACTION_PROGRAMS:
                        loadPrograms(MainActivity.this);
                        break;
                    case WebService.ACTION_CALENDAR:
                        loadEventCategories(MainActivity.this);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        registerReceiver(mainReceiver, mainFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(mainReceiver);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void loadInformations(String type, Context context) {

        ProgressDialog progressDialog = ((MainActivity) context).progressDialog;

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        String[] content = new String[2];
        if (context.getString(R.string.symbols).equals(type)) {
            content = ItemsPresenter.getInformation("Simbolos", context);
        } else if (context.getString(R.string.institutional_welfare).equals(type)) {
            content = ItemsPresenter.getInformation("Cursos Culturales y Deportivos", context);
        }

        if (progressDialog.isShowing() && content[0] != null) {
            progressDialog.dismiss();
            Intent intent = new Intent(context, WebContentActivity.class);
            intent.putExtra("CATEGORY", type);
            intent.putExtra("LINK", content[0]);
            intent.putExtra("CONTENT", content[1]);
            context.startActivity(intent);
        } else if (content[0] == null && !Utilities.haveNetworkConnection(context)) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void loadContactCategories(Context context) {

        ProgressDialog progressDialog = ((MainActivity) context).progressDialog;

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        ArrayList<Item> categories = ItemsPresenter.getContactCategories(context);

        if (progressDialog.isShowing() && categories.size() > 0) {
            progressDialog.dismiss();
            Intent intent = new Intent(context, ItemsActivity.class);
            intent.putExtra("CATEGORY", context.getString(R.string.directory));
            intent.putParcelableArrayListExtra("ITEMS", categories);
            context.startActivity(intent);
        } else if (categories.size() == 0 && !Utilities.haveNetworkConnection(context)) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void loadPrograms(Context context) {

        ProgressDialog progressDialog = ((MainActivity) context).progressDialog;

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        ArrayList<Item> programs = ItemsPresenter.getPrograms(context);

        if (progressDialog.isShowing() && programs.size() > 0) {
            progressDialog.dismiss();
            Intent intent = new Intent(context, ItemsActivity.class);
            intent.putExtra("CATEGORY", context.getString(R.string.academic_offer));
            intent.putParcelableArrayListExtra("ITEMS", programs);
            context.startActivity(intent);
        } else if (programs.size() == 0 && !Utilities.haveNetworkConnection(context)) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void loadEventCategories(Context context) {

        ProgressDialog progressDialog = ((MainActivity) context).progressDialog;

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        ArrayList<Item> categories = ItemsPresenter.getEventCategories(context);

        if (progressDialog.isShowing() && categories.size() > 0) {
            progressDialog.dismiss();
            Intent intent = new Intent(context, ItemsActivity.class);
            intent.putExtra("CATEGORY", context.getString(R.string.academic_calendar));
            intent.putParcelableArrayListExtra("ITEMS", categories);
            context.startActivity(intent);
        } else if (categories.size() == 0 && !Utilities.haveNetworkConnection(context)) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

}
