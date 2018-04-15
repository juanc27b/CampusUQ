package co.edu.uniquindio.campusuq.items;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsActivity;
import co.edu.uniquindio.campusuq.dishes.DishesActivity;
import co.edu.uniquindio.campusuq.emails.EmailsActivity;
import co.edu.uniquindio.campusuq.events.CalendarActivity;
import co.edu.uniquindio.campusuq.maps.MapsActivity;
import co.edu.uniquindio.campusuq.news.NewsActivity;
import co.edu.uniquindio.campusuq.objects.ObjectsActivity;
import co.edu.uniquindio.campusuq.quotas.QuotasActivity;
import co.edu.uniquindio.campusuq.radio.RadioActivity;
import co.edu.uniquindio.campusuq.users.LoginActivity;
import co.edu.uniquindio.campusuq.users.User;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.web.WebActivity;
import co.edu.uniquindio.campusuq.web.WebContentActivity;

public class ItemsActivity extends MainActivity implements ItemsAdapter.OnClickItemListener {

    private ItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<Item> items;
    public ItemsPresenter itemsPresenter;

    private String category;
    private String subcategory;

    public ItemsActivity() {
        super.setHasNavigationDrawerIcon(false);

        items = new ArrayList<>();
        itemsPresenter = new ItemsPresenter();
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_background, R.drawable.landscape_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_items);
        stub.inflate();

        RecyclerView mRecyclerView = findViewById(R.id.items_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ItemsAdapter(items, this);
        mRecyclerView.setAdapter(mAdapter);

        ArrayList<Item> newItems;
        if (savedInstanceState != null) {
            category = savedInstanceState.getString("CATEGORY");
            subcategory = savedInstanceState.getString("SUBCATEGORY");
            newItems = savedInstanceState.getParcelableArrayList("ITEMS");
        } else {
            Intent intent = getIntent();
            category = intent.getStringExtra("CATEGORY");
            subcategory = intent.getStringExtra("SUBCATEGORY");
            newItems = intent.getParcelableArrayListExtra("ITEMS");
        }
        setItems(newItems);

    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            for (Item item : items) {
                if (item.getTitle().toLowerCase().contains(query.trim().toLowerCase())) {
                    //items.add(0, items.remove(items.indexOf(item)));
                    //mAdapter.setItems(items);
                    mLayoutManager.scrollToPosition(items.indexOf(item));
                    return;
                }
            }

            Toast.makeText(this, getString(R.string.item_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            category = intent.getStringExtra("CATEGORY");
            subcategory = intent.getStringExtra("SUBCATEGORY");
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(category);
                ArrayList<Item> newItems = intent.getParcelableArrayListExtra("ITEMS");
                setItems(newItems);
            }
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent intent = null;
        String title = items.get(pos).getTitle();
        String category = null, label = null, action = getString(R.string.analytics_view_action);

        if (getString(R.string.events).equals(title)) {
            category = getString(R.string.analytics_news_category);
            label = getString(R.string.analytics_events_label);
            intent = new Intent(this, NewsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.events));
        } else if (getString(R.string.news).equals(title)) {
            category = getString(R.string.analytics_news_category);
            label = getString(R.string.analytics_news_label);
            intent = new Intent(this, NewsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.news));
        } else if (getString(R.string.institution).equals(title)) {
            intent = new Intent(this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.institution));
        } else if (getString(R.string.directory).equals(title)) {
            category = getString(R.string.analytics_contatcs_category);
            label = getString(R.string.analytics_directory_label);
            WebService.PENDING_ACTION = WebService.ACTION_CONTACTS;
            loadContactCategories(this);
        } else if (getString(R.string.academic_offer).equals(title)) {
            category = getString(R.string.analytics_programs_category);
            label = getString(R.string.analytics_academic_offer_label);
            WebService.PENDING_ACTION = WebService.ACTION_PROGRAMS;
            loadPrograms(this);
        } else if (getString(R.string.academic_calendar).equals(title)) {
            category = getString(R.string.analytics_events_category);
            label = getString(R.string.analytics_academic_calendar_label);
            WebService.PENDING_ACTION = WebService.ACTION_CALENDAR;
            loadEventCategories(this);
        } else if (getString(R.string.employment_exchange).equals(title)) {
            category = getString(R.string.analytics_web_category);
            label = getString(R.string.analytics_employment_exchange_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.employment_exchange_url));
        } else if (getString(R.string.institutional_welfare).equals(title)) {
            category = getString(R.string.analytics_informations_category);
            label = getString(R.string.analytics_institutional_welfare);
            WebService.PENDING_ACTION = WebService.ACTION_WELFARE;
            loadInformations(getString(R.string.institutional_welfare), this);
        } else if (getString(R.string.university_map).equals(title)) {
            category = getString(R.string.analytics_maps_category);
            label = getString(R.string.analytics_university_map_label);
            intent = new Intent(this, MapsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.university_map));
        } else if (getString(R.string.library_services).equals(title)) {
            intent = new Intent(this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.library_services));
        } else if (getString(R.string.radio).equals(title)) {
            category = getString(R.string.analytics_radio_category);
            label = getString(R.string.analytics_radio_label);
            intent = new Intent(this, RadioActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.radio));
        } else if (getString(R.string.pqrsd_system).equals(title)) {
            category = getString(R.string.analytics_web_category);
            label = getString(R.string.analytics_pqrsd_system_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL",
                    getString(R.string.pqrsd_system_url));
        } else if (getString(R.string.lost_objects).equals(title)) {
            category = getString(R.string.analytics_objects_category);
            label = getString(R.string.analytics_lost_objects_label);
            intent = new Intent(this, ObjectsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.lost_objects));
        } else if (getString(R.string.security_system).equals(title)) {
            category = getString(R.string.analytics_announcements_category);
            label = getString(R.string.analytics_security_system_label);
            intent = new Intent(this, AnnouncementsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.security_system));
        } else if (getString(R.string.restaurant).equals(title)) {
            category = getString(R.string.analytics_dishes_category);
            label = getString(R.string.analytics_restaurant_label);
            intent = new Intent(this, DishesActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.restaurant));
        } else if (getString(R.string.billboard_information).equals(title)) {
            category = getString(R.string.analytics_announcements_category);
            label = getString(R.string.analytics_billboard_information_label);
            intent = new Intent(this, AnnouncementsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.billboard_information));
        } else if (getString(R.string.computer_rooms).equals(title)) {
            category = getString(R.string.analytics_quotas_category);
            label = getString(R.string.analytics_computer_rooms_label);
            intent = new Intent(this, QuotasActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.computer_rooms));
        } else if (getString(R.string.parking_lots).equals(title)) {
            category = getString(R.string.analytics_quotas_category);
            label = getString(R.string.analytics_parking_lots_label);
            intent = new Intent(this, QuotasActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.parking_lots));
        } else if (getString(R.string.laboratories).equals(title)) {
            category = getString(R.string.analytics_quotas_category);
            label = getString(R.string.analytics_laboratories_label);
            intent = new Intent(this, QuotasActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.laboratories));
        } else if (getString(R.string.study_areas).equals(title)) {
            category = getString(R.string.analytics_quotas_category);
            label = getString(R.string.analytics_study_areas_label);
            intent = new Intent(this, QuotasActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.study_areas));
        } else if (getString(R.string.cultural_and_sport).equals(title)) {
            category = getString(R.string.analytics_quotas_category);
            label = getString(R.string.analytics_cultural_and_sport_label);
            intent = new Intent(this, QuotasActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.cultural_and_sport));
        } else if (getString(R.string.auditoriums).equals(title)) {
            category = getString(R.string.analytics_quotas_category);
            label = getString(R.string.analytics_auditoriums_label);
            intent = new Intent(this, QuotasActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.auditoriums));
        } else if (getString(R.string.institutional_mail).equals(title)) {
            User user = UsersPresenter.loadUser(this);
            if (user != null) {
                if (user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                    category = getString(R.string.analytics_users_category);
                    label = getString(R.string.analytics_login_label);
                    intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("CATEGORY", getString(R.string.log_in));
                } else {
                    category = getString(R.string.analytics_emails_category);
                    label = getString(R.string.analytics_institutional_mail_label);
                    intent = new Intent(this, EmailsActivity.class);
                    intent.putExtra("CATEGORY", getString(R.string.institutional_mail));
                }
            }
        } else if (getString(R.string.web_page).equals(title)) {
            category = getString(R.string.analytics_web_category);
            label = getString(R.string.analytics_web_page_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.web_page_url));
        } else if (getString(R.string.ecotic).equals(title)) {
            category = getString(R.string.analytics_web_category);
            label = getString(R.string.analytics_ecotic_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.ecotic_url));
        } else if (getString(R.string.mission_vision).equals(title)) {
            category = getString(R.string.analytics_informations_category);
            label = getString(R.string.analytics_mission_vision_label);
            intent = new Intent(this, WebContentActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.mission_vision));
            intent.putExtra("LINK",
                    getString(R.string.pdf_viewer)
                            .replaceAll("URL",
                                    getString(R.string.mission_vision_url)));
        } else if (getString(R.string.quality_policy).equals(title)) {
            category = getString(R.string.analytics_informations_category);
            label = getString(R.string.analytics_quality_policy_label);
            intent = new Intent(this, WebContentActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.quality_policy));
            intent.putExtra("LINK",
                    getString(R.string.pdf_viewer)
                            .replaceAll("URL",
                                    getString(R.string.quality_policy_url)));
        } else if (getString(R.string.axes_pillars_objectives).equals(title)) {
            category = getString(R.string.analytics_informations_category);
            label = getString(R.string.analytics_axes_pillars_objectives_label);
            intent = new Intent(this, WebContentActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.axes_pillars_objectives));
            intent.putExtra("LINK",
                    getString(R.string.pdf_viewer)
                            .replaceAll("URL",
                                    getString(R.string.axes_pillars_objectives_url)));
        } else if (getString(R.string.organization_chart).equals(title)) {
            category = getString(R.string.analytics_informations_category);
            label = getString(R.string.analytics_organization_chart_label);
            intent = new Intent(this, WebContentActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.organization_chart));
            intent.putExtra("LINK",
                    getString(R.string.pdf_viewer)
                            .replaceAll("URL",
                                    getString(R.string.organization_chart_url)));
        } else if (getString(R.string.normativity).equals(title)) {
            category = getString(R.string.analytics_informations_category);
            label = getString(R.string.analytics_normativity_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.normativity_url));
        } else if (getString(R.string.symbols).equals(title)) {
            category = getString(R.string.analytics_informations_category);
            label = getString(R.string.analytics_symbols_label);
            WebService.PENDING_ACTION = WebService.ACTION_SYMBOLS;
            loadInformations(getString(R.string.symbols), this);
        } else if (getString(R.string.digital_repository).equals(title)) {
            category = getString(R.string.analytics_web_category);
            label = getString(R.string.analytics_digital_repository_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.digital_repository_url));
        } else if (getString(R.string.public_catalog).equals(title)) {
            category = getString(R.string.analytics_web_category);
            label = getString(R.string.analytics_public_catalog_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.public_catalog_url));
        } else if (getString(R.string.databases).equals(title)) {
            category = getString(R.string.analytics_web_category);
            label = getString(R.string.analytics_databases_label);
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("URL",
                    getString(R.string.databases_url));
        } else if (getString(R.string.directory).equals(this.category)) {
            loadContacts(title);
        } else if (getString(R.string.academic_offer).equals(this.category)) {
            intent = new Intent(this, ItemsActivity.class);
            intent.putExtra("CATEGORY", title);
            intent.putExtra("SUBCATEGORY", getString(R.string.academic_offer));
        } else if (getString(R.string.history).equals(title) ||
                getString(R.string.program_mission_vision).equals(title) ||
                getString(R.string.curriculum).equals(title) ||
                getString(R.string.profiles).equals(title) ||
                getString(R.string.program_contact).equals(title)) {
            loadProgramContent(this.category, title);
        } else if (getString(R.string.academic_calendar).equals(this.category)) {
            intent = new Intent(this, CalendarActivity.class);
            intent.putExtra("CATEGORY", title);
        }

        if (category != null) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(1)
                    .build());
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    public void setItems(ArrayList<Item> items) {
        if (category.equals(getString(R.string.information_module))) {
            this.items = itemsPresenter.getInformationItems(this);
        } else if (category.equals(getString(R.string.services_module))) {
            this.items = itemsPresenter.getServicesItems(this);
        } else if (category.equals(getString(R.string.state_module))) {
            this.items = itemsPresenter.getStateItems(this);
        } else if (category.equals(getString(R.string.communication_module))) {
            this.items = itemsPresenter.getCommunicationItems(this);
        } else if (category.equals(getString(R.string.institution))) {
            this.items = itemsPresenter.getInstitutionItems(this);
        } else if (category.equals(getString(R.string.library_services))) {
            this.items = itemsPresenter.getLibraryItems(this);
        } else if (subcategory != null && subcategory.equals(getString(R.string.academic_offer))) {
            this.items = itemsPresenter.getProgramItems(this);
        } else {
            this.items = items;
        }
        mAdapter.setItems(this.items);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getString(R.string.institution).equals(category) ||
                getString(R.string.directory).equals(category) ||
                getString(R.string.academic_offer).equals(category) ||
                getString(R.string.academic_calendar).equals(category)) {
            Intent intent = new Intent(this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.information_module));
            startActivity(intent);
        } else if (getString(R.string.library_services).equals(category)) {
            Intent intent = new Intent(this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.services_module));
            startActivity(intent);
        } else if (subcategory != null) {
            if (getString(R.string.directory).equals(subcategory)) {
                loadContactCategories(this);
            } else if (getString(R.string.academic_offer).equals(subcategory)) {
                loadPrograms(this);
            }
        } else {
            super.onBackPressed();
        }
        // directorio, dependencias y contactos
        // calendario
        // ¿menu restaurante?
        // ¿correo institucional?
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CATEGORY", category);
        outState.putString("SUBCATEGORY", subcategory);
        outState.putParcelableArrayList("ITEMS", this.items);

        super.onSaveInstanceState(outState);
    }

    private IntentFilter symbolsFilter = new IntentFilter(WebService.ACTION_SYMBOLS);
    // Define the callback for what to do when data is received
    private BroadcastReceiver symbolsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebService.PENDING_ACTION = WebService.ACTION_NONE;
            if (progressDialog.isShowing()) {
                loadInformations(getString(R.string.symbols), ItemsActivity.this);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        registerReceiver(symbolsReceiver, symbolsFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(symbolsReceiver);
    }

    public void loadContacts(String categoryName) {

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        ArrayList<Item> contacts = ItemsPresenter.getContacts(categoryName, this);

        if (progressDialog.isShowing() && contacts.size() > 0) {
            progressDialog.dismiss();
            Intent intent = new Intent(this, ItemsActivity.class);
            intent.putExtra("CATEGORY", categoryName);
            intent.putExtra("SUBCATEGORY", getString(R.string.directory));
            intent.putParcelableArrayListExtra("ITEMS", contacts);
            startActivity(intent);
        } else if (contacts.size() == 0 && !Utilities.haveNetworkConnection(this)) {
            Toast.makeText(this, getString(R.string.no_internet),
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void loadProgramContent(String name, String type) {

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        String[] content = ItemsPresenter.getProgramContent(name, type, this);

        if (progressDialog.isShowing() && content[0] != null) {
            progressDialog.dismiss();
            Intent intent = new Intent(this, WebContentActivity.class);
            intent.putExtra("CATEGORY", type);
            intent.putExtra("LINK", content[0]);
            intent.putExtra("CONTENT", content[1]);
            startActivity(intent);
        } else if (content[0] == null && !Utilities.haveNetworkConnection(this)) {
            Toast.makeText(this, getString(R.string.no_internet),
                    Toast.LENGTH_SHORT).show();
        }

    }

}
