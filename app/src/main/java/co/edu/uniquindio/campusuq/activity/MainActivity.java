package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import co.edu.uniquindio.campusuq.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean hasSearch;
    private boolean hasNavigationDrawerIcon;

    public MainActivity() {
        setHasSearch(true);
        setHasNavigationDrawerIcon(true);
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

        addContent();

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
            case R.id.action_login_as_administrator:
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
        Intent intent;
        int id = item.getItemId();
        switch(id) {
            case R.id.nav_events:
                intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.events));
                MainActivity.this.startActivity(intent);
                break;
            case R.id.nav_news:
                intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news));
                MainActivity.this.startActivity(intent);
                break;
            case R.id.nav_institution:

                break;
            case R.id.nav_directory:

                break;
            case R.id.nav_academic_offer:

                break;
            case R.id.nav_academic_calendar:

                break;
            case R.id.nav_employment_exchange:

                break;
            case R.id.nav_institutional_welfare:

                break;
            case R.id.nav_university_map:
                intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.university_map));
                MainActivity.this.startActivity(intent);
                break;
            case R.id.nav_library_services:

                break;
            case R.id.nav_radio:
                intent = new Intent(MainActivity.this, RadioActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.radio));
                MainActivity.this.startActivity(intent);
                break;
            case R.id.nav_lost_objects:

                break;
            case R.id.nav_security_system:

                break;
            case R.id.nav_restaurant:

                break;
            case R.id.nav_billboard_information:

                break;
            case R.id.nav_computer_rooms:

                break;
            case R.id.nav_parking_lots:

                break;
            case R.id.nav_institutional_mail:

                break;
            case R.id.nav_web_page:

                break;
            case R.id.nav_ecotic:

                break;
            default:
                break;
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

    public void addContent() {

    }

    public boolean haveNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }

}
