package co.edu.uniquindio.campusuq.items;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import org.apache.commons.lang3.StringUtils;

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

    private static final String DIRECTORY_FRAGMENT = "DIRECTORY_FRAGMENT";

    private RecyclerView recyclerView;

    private int category;
    private int subcategory;

    private IntentFilter symbolsFilter = new IntentFilter(WebService.ACTION_SYMBOLS);
    private BroadcastReceiver symbolsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebService.PENDING_ACTION = WebService.ACTION_NONE;

            if (progressDialog.isShowing()) {
                loadInformations(R.string.symbols, ItemsActivity.this);
            }
        }
    };

    public ItemsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_background, R.drawable.landscape_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_items);
        stub.inflate();

        recyclerView = findViewById(R.id.items_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ItemsAdapter(new ArrayList<Item>(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        ArrayList<Item> items;

        if (savedInstanceState != null) {
            category = savedInstanceState.getInt(Utilities.CATEGORY);
            subcategory = savedInstanceState.getInt(Utilities.SUBCATEGORY);
            items = savedInstanceState.getParcelableArrayList(Utilities.ITEMS);
        } else {
            Intent intent = getIntent();
            category = intent.getIntExtra(Utilities.CATEGORY, R.string.app_name);
            subcategory = intent.getIntExtra(Utilities.SUBCATEGORY, R.string.app_name);
            items = intent.getParcelableArrayListExtra(Utilities.ITEMS);
        }

        setItems(items);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (recyclerView != null) {
                ArrayList<Item> items = ((ItemsAdapter) recyclerView.getAdapter()).getItems();

                for (Item item : items) {
                    if (StringUtils.stripAccents(item.getTitle()).toLowerCase()
                            .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                        //items.add(0, items.remove(items.indexOf(item)));
                        //mAdapter.setItems(items);
                        recyclerView.getLayoutManager().scrollToPosition(items.indexOf(item));
                        return;
                    }
                }
            }

            Toast.makeText(this, getString(R.string.item_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            setIntent(intent);
            category = intent.getIntExtra(Utilities.CATEGORY, R.string.app_name);
            subcategory = intent.getIntExtra(Utilities.SUBCATEGORY, R.string.app_name);
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(category != R.string.app_name ?
                        getString(category) : intent.getStringExtra(Utilities.CATEGORY));
                ArrayList<Item> items = intent.getParcelableArrayListExtra(Utilities.ITEMS);
                setItems(items);
            }
        }
    }

    @Override
    public void onItemClick(int index) {
        Item item = ((ItemsAdapter) recyclerView.getAdapter()).getItems().get(index);
        String title = item.getTitle();
        String category = null;
        String label = null;
        Intent intent = null;

        switch (this.category) {
            case R.string.information_module:
                if (getString(R.string.events).equals(title)) {
                    category = getString(R.string.analytics_news_category);
                    label = getString(R.string.analytics_events_label);
                    intent = new Intent(this, NewsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.events);
                } else if (getString(R.string.news).equals(title)) {
                    category = getString(R.string.analytics_news_category);
                    label = getString(R.string.analytics_news_label);
                    intent = new Intent(this, NewsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.news);
                } else if (getString(R.string.institution).equals(title)) {
                    intent = new Intent(this, ItemsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.institution);
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
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL, getString(R.string.employment_exchange_url));
                } else if (getString(R.string.institutional_welfare).equals(title)) {
                    category = getString(R.string.analytics_informations_category);
                    label = getString(R.string.analytics_institutional_welfare);
                    WebService.PENDING_ACTION = WebService.ACTION_WELFARE;
                    loadInformations(R.string.institutional_welfare, this);
                }
                break;
            case R.string.services_module:
                if (getString(R.string.university_map).equals(title)) {
                    category = getString(R.string.analytics_maps_category);
                    label = getString(R.string.analytics_university_map_label);
                    intent = new Intent(this, MapsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.university_map);
                } else if (getString(R.string.library_services).equals(title)) {
                    intent = new Intent(this, ItemsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.library_services);
                } else if (getString(R.string.radio).equals(title)) {
                    category = getString(R.string.analytics_radio_category);
                    label = getString(R.string.analytics_radio_label);
                    intent = new Intent(this, RadioActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.radio);
                } else if (getString(R.string.pqrsd_system).equals(title)) {
                    category = getString(R.string.analytics_web_category);
                    label = getString(R.string.analytics_pqrsd_system_label);
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL,
                                    getString(R.string.pqrsd_system_url));
                } else if (getString(R.string.lost_objects).equals(title)) {
                    category = getString(R.string.analytics_objects_category);
                    label = getString(R.string.analytics_lost_objects_label);
                    intent = new Intent(this, ObjectsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.lost_objects);
                } else if (getString(R.string.security_system).equals(title)) {
                    category = getString(R.string.analytics_announcements_category);
                    label = getString(R.string.analytics_security_system_label);
                    intent = new Intent(this, AnnouncementsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.security_system);
                }
                break;
            case R.string.state_module:
                if (getString(R.string.restaurant).equals(title)) {
                    category = getString(R.string.analytics_dishes_category);
                    label = getString(R.string.analytics_restaurant_label);
                    intent = new Intent(this, DishesActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.restaurant);
                } else if (getString(R.string.billboard_information).equals(title)) {
                    category = getString(R.string.analytics_announcements_category);
                    label = getString(R.string.analytics_billboard_information_label);
                    intent = new Intent(this, AnnouncementsActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.billboard_information);
                } else if (getString(R.string.computer_rooms).equals(title)) {
                    category = getString(R.string.analytics_quotas_category);
                    label = getString(R.string.analytics_computer_rooms_label);
                    intent = new Intent(this, QuotasActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.computer_rooms);
                } else if (getString(R.string.parking_lots).equals(title)) {
                    category = getString(R.string.analytics_quotas_category);
                    label = getString(R.string.analytics_parking_lots_label);
                    intent = new Intent(this, QuotasActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.parking_lots);
                } else if (getString(R.string.laboratories).equals(title)) {
                    category = getString(R.string.analytics_quotas_category);
                    label = getString(R.string.analytics_laboratories_label);
                    intent = new Intent(this, QuotasActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.laboratories);
                } else if (getString(R.string.study_areas).equals(title)) {
                    category = getString(R.string.analytics_quotas_category);
                    label = getString(R.string.analytics_study_areas_label);
                    intent = new Intent(this, QuotasActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.study_areas);
                } else if (getString(R.string.cultural_and_sport).equals(title)) {
                    category = getString(R.string.analytics_quotas_category);
                    label = getString(R.string.analytics_cultural_and_sport_label);
                    intent = new Intent(this, QuotasActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.cultural_and_sport);
                } else if (getString(R.string.auditoriums).equals(title)) {
                    category = getString(R.string.analytics_quotas_category);
                    label = getString(R.string.analytics_auditoriums_label);
                    intent = new Intent(this, QuotasActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.auditoriums);
                }
                break;
            case R.string.communication_module:
                if (getString(R.string.institutional_mail).equals(title)) {
                    User user = UsersPresenter.loadUser(this);

                    if (user != null) {
                        if ("campusuq@uniquindio.edu.co".equals(user.getEmail())) {
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
                } else if (getString(R.string.web_page).equals(title)) {
                    category = getString(R.string.analytics_web_category);
                    label = getString(R.string.analytics_web_page_label);
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL, getString(R.string.web_page_url));
                } else if (getString(R.string.ecotic).equals(title)) {
                    category = getString(R.string.analytics_web_category);
                    label = getString(R.string.analytics_ecotic_label);
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL,
                                    getString(R.string.ecotic_url));
                }
                break;
            case R.string.institution:
                if (getString(R.string.mission_vision).equals(title)) {
                    category = getString(R.string.analytics_informations_category);
                    label = getString(R.string.analytics_mission_vision_label);
                    intent = new Intent(this, WebContentActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.mission_vision)
                            .putExtra(Utilities.LINK,
                                    getString(R.string.pdf_viewer)
                                            .replaceAll("URL",
                                                    getString(R.string.mission_vision_url)));
                } else if (getString(R.string.quality_policy).equals(title)) {
                    category = getString(R.string.analytics_informations_category);
                    label = getString(R.string.analytics_quality_policy_label);
                    intent = new Intent(this, WebContentActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.quality_policy)
                            .putExtra(Utilities.LINK,
                                    getString(R.string.pdf_viewer)
                                            .replaceAll("URL",
                                                    getString(R.string.quality_policy_url)));
                } else if (getString(R.string.axes_pillars_objectives).equals(title)) {
                    category = getString(R.string.analytics_informations_category);
                    label = getString(R.string.analytics_axes_pillars_objectives_label);
                    intent = new Intent(this, WebContentActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.axes_pillars_objectives)
                            .putExtra(Utilities.LINK,
                                    getString(R.string.pdf_viewer)
                                            .replaceAll("URL",
                                                    getString(R.string.axes_pillars_objectives_url)));
                } else if (getString(R.string.organization_chart).equals(title)) {
                    category = getString(R.string.analytics_informations_category);
                    label = getString(R.string.analytics_organization_chart_label);
                    intent = new Intent(this, WebContentActivity.class)
                            .putExtra(Utilities.CATEGORY, R.string.organization_chart)
                            .putExtra(Utilities.LINK,
                                    getString(R.string.pdf_viewer)
                                            .replaceAll("URL",
                                                    getString(R.string.organization_chart_url)));
                } else if (getString(R.string.normativity).equals(title)) {
                    category = getString(R.string.analytics_informations_category);
                    label = getString(R.string.analytics_normativity_label);
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL, getString(R.string.normativity_url));
                } else if (getString(R.string.symbols).equals(title)) {
                    category = getString(R.string.analytics_informations_category);
                    label = getString(R.string.analytics_symbols_label);
                    WebService.PENDING_ACTION = WebService.ACTION_SYMBOLS;
                    loadInformations(R.string.symbols, this);
                }
                break;
            case R.string.directory:
                intent = new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, title)
                        .putExtra(Utilities.SUBCATEGORY, R.string.directory);
                break;
            case R.string.academic_offer:
                intent = new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, title)
                        .putExtra(Utilities.SUBCATEGORY, R.string.academic_offer);
                break;
            case R.string.academic_calendar:
                intent = new Intent(this, CalendarActivity.class)
                        .putExtra(Utilities.CATEGORY, title);
                break;
            case R.string.library_services:
                if (getString(R.string.digital_repository).equals(title)) {
                    category = getString(R.string.analytics_web_category);
                    label = getString(R.string.analytics_digital_repository_label);
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL, getString(R.string.digital_repository_url));
                } else if (getString(R.string.public_catalog).equals(title)) {
                    category = getString(R.string.analytics_web_category);
                    label = getString(R.string.analytics_public_catalog_label);
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL, getString(R.string.public_catalog_url));
                } else if (getString(R.string.databases).equals(title)) {
                    category = getString(R.string.analytics_web_category);
                    label = getString(R.string.analytics_databases_label);
                    intent = new Intent(this, WebActivity.class)
                            .putExtra(Utilities.URL,
                                    getString(R.string.databases_url));
                }
                break;
            default:
                switch (subcategory) {
                    case R.string.directory:
                        ItemsFragment.newInstance(item)
                                .show(getSupportFragmentManager(), DIRECTORY_FRAGMENT);
                        break;
                    case R.string.academic_offer: {
                        ActionBar actionBar = getSupportActionBar();

                        if (actionBar != null) {
                            CharSequence actionBarTitle = actionBar.getTitle();

                            if (actionBarTitle != null) {
                                if (getString(R.string.history).equals(title)) {
                                    loadProgramContent(actionBarTitle.toString(), R.string.history);
                                } else if (getString(R.string.program_mission_vision)
                                        .equals(title)) {
                                    loadProgramContent(actionBarTitle.toString(),
                                            R.string.program_mission_vision);
                                } else if (getString(R.string.curriculum).equals(title)) {
                                    loadProgramContent(actionBarTitle.toString(),
                                            R.string.curriculum);
                                } else if (getString(R.string.profiles).equals(title)) {
                                    loadProgramContent(actionBarTitle.toString(),
                                            R.string.profiles);
                                } else if (getString(R.string.program_contact).equals(title)) {
                                    loadProgramContent(actionBarTitle.toString(),
                                            R.string.program_contact);
                                }
                            }
                        }
                        break;
                    }
                }
                break;
        }

        if (category != null) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(getString(R.string.analytics_view_action))
                    .setLabel(label)
                    .setValue(1)
                    .build());
        }

        if (intent != null) startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ItemsFragment itemsFragment =
                (ItemsFragment) getSupportFragmentManager().findFragmentByTag(DIRECTORY_FRAGMENT);
        itemsFragment.addContact.setClickable(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        itemsFragment.call.setClickable(grantResults[1] == PackageManager.PERMISSION_GRANTED);
    }

    public void setItems(ArrayList<Item> items) {
        ItemsAdapter itemsAdapter = (ItemsAdapter) recyclerView.getAdapter();

        switch (category) {
            case R.string.information_module:
                itemsAdapter.setItems(ItemsPresenter.getInformationItems(this));
                break;
            case R.string.services_module:
                itemsAdapter.setItems(ItemsPresenter.getServicesItems(this));
                break;
            case R.string.state_module:
                itemsAdapter.setItems(ItemsPresenter.getStateItems(this));
                break;
            case R.string.communication_module:
                itemsAdapter.setItems(ItemsPresenter.getCommunicationItems(this));
                break;
            case R.string.institution:
                itemsAdapter.setItems(ItemsPresenter.getInstitutionItems(this));
                break;
            /*case R.string.directory:
                loadContactCategories(this);
                break;
            case R.string.academic_offer:
                loadPrograms(this);
                break;
            case R.string.academic_calendar:
                loadEventCategories(this);
                break;*/
            case R.string.library_services:
                itemsAdapter.setItems(ItemsPresenter.getLibraryItems(this));
                break;
            default:
                switch (subcategory) {
                    case R.string.directory:
                        ArrayList<Item> contacts = ItemsPresenter.getContacts(getIntent()
                                .getStringExtra(Utilities.CATEGORY), this);

                        if (contacts.isEmpty()) {
                            Toast.makeText(this, R.string.no_records,
                                    Toast.LENGTH_SHORT).show();
                        }

                        itemsAdapter.setItems(contacts);
                        break;
                    case R.string.academic_offer:
                        itemsAdapter.setItems(ItemsPresenter.getProgramItems(this));
                        break;
                    default:
                        itemsAdapter.setItems(items != null ? items : new ArrayList<Item>());
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (category == R.string.institution ||
                category == R.string.directory ||
                category == R.string.academic_offer ||
                category == R.string.academic_calendar) {
            startActivity(new Intent(this, ItemsActivity.class)
                    .putExtra(Utilities.CATEGORY, R.string.information_module));
        } else if (category == R.string.library_services) {
            startActivity(new Intent(this, ItemsActivity.class)
                    .putExtra(Utilities.CATEGORY, R.string.services_module));
        } else if (subcategory == R.string.directory) {
            loadContactCategories(this);
        } else if (subcategory == R.string.academic_offer) {
            loadPrograms(this);
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
        outState.putInt(Utilities.CATEGORY, category);
        outState.putInt(Utilities.SUBCATEGORY, subcategory);
        outState.putParcelableArrayList(Utilities.ITEMS,
                ((ItemsAdapter) recyclerView.getAdapter()).getItems());

        super.onSaveInstanceState(outState);
    }

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

    public void loadProgramContent(String name, int type) {
        if (!progressDialog.isShowing()) progressDialog.show();

        String[] content = ItemsPresenter.getProgramContent(name, type, this);

        if (progressDialog.isShowing() && content != null) {
            progressDialog.dismiss();
            startActivity(new Intent(this, WebContentActivity.class)
                    .putExtra(Utilities.CATEGORY, type)
                    .putExtra(Utilities.LINK, content[0])
                    .putExtra("CONTENT", content[1]));
        } else if (content == null && !Utilities.haveNetworkConnection(this)) {
            Toast.makeText(this, R.string.no_internet,
                    Toast.LENGTH_SHORT).show();
        }
    }

}
