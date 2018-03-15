package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.ItemsAdapter;
import co.edu.uniquindio.campusuq.util.ItemsPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Item;

public class ItemsActivity extends MainActivity implements ItemsAdapter.OnClickItemListener {

    private RecyclerView mRecyclerView;
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

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_items);
        View inflated = stub.inflate();

        mRecyclerView = (RecyclerView) findViewById(R.id.items_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ItemsAdapter(items, ItemsActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        ArrayList<Item> newItems = new ArrayList<>();
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
            boolean found = false;
            for (Item item : items) {
                if (query.trim().toLowerCase().equals(item.getTitle().toLowerCase()) ||
                        item.getTitle().toLowerCase().contains(query.trim().toLowerCase())) {
                    //items.add(0, items.remove(items.indexOf(item)));
                    //mAdapter.setItems(items);
                    mLayoutManager.scrollToPosition(items.indexOf(item));
                    found = true;
                    break;
                }
            }
            if (!found) {
                Toast.makeText(this, "No se ha encontrado el ítem: "+query, Toast.LENGTH_SHORT).show();
            }
        } else if (mAdapter != null) {
            category = intent.getStringExtra("CATEGORY");
            subcategory = intent.getStringExtra("SUBCATEGORY");
            getSupportActionBar().setTitle(category);
            ArrayList<Item> newItems = intent.getParcelableArrayListExtra("ITEMS");
            setItems(newItems);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent intent = null;
        String title = items.get(pos).getTitle();
        if (getString(R.string.events).equals(title)) {
            intent = new Intent(ItemsActivity.this, NewsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.events));
        } else if (getString(R.string.news).equals(title)) {
            intent = new Intent(ItemsActivity.this, NewsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.news));
        } else if (getString(R.string.institution).equals(title)) {
            intent = new Intent(ItemsActivity.this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.institution));
        } else if (getString(R.string.directory).equals(title)) {
            WebService.PENDING_ACTION = WebService.ACTION_CONTACTS;
            loadContactCategories(ItemsActivity.this);
        } else if (getString(R.string.academic_offer).equals(title)) {
            WebService.PENDING_ACTION = WebService.ACTION_PROGRAMS;
            loadPrograms(ItemsActivity.this);
        } else if (getString(R.string.academic_calendar).equals(title)) {
            WebService.PENDING_ACTION = WebService.ACTION_CALENDAR;
            loadEventCategories(ItemsActivity.this);
        } else if (getString(R.string.employment_exchange).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.employment_exchange_url));
        } else if (getString(R.string.institutional_welfare).equals(title)) {
            WebService.PENDING_ACTION = WebService.ACTION_WELFARE;
            loadInformations(getString(R.string.institutional_welfare), ItemsActivity.this);
        } else if (getString(R.string.university_map).equals(title)) {
            intent = new Intent(ItemsActivity.this, MapsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.university_map));
        } else if (getString(R.string.library_services).equals(title)) {
            intent = new Intent(ItemsActivity.this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.library_services));
        } else if (getString(R.string.radio).equals(title)) {
            intent = new Intent(ItemsActivity.this, RadioActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.radio));
        } else if (getString(R.string.pqrsd_system).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.pqrsd_system_url));
        } else if (getString(R.string.lost_objects).equals(title)) {

        } else if (getString(R.string.security_system).equals(title)) {
            intent = new Intent(ItemsActivity.this, AnnouncementsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.security_system));
        } else if (getString(R.string.restaurant).equals(title)) {

        } else if (getString(R.string.billboard_information).equals(title)) {
            intent = new Intent(ItemsActivity.this, AnnouncementsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.billboard_information));
        } else if (getString(R.string.computer_rooms).equals(title)) {

        } else if (getString(R.string.parking_lots).equals(title)) {

        } else if (getString(R.string.institutional_mail).equals(title)) {

        } else if (getString(R.string.web_page).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.web_page_url));
        } else if (getString(R.string.ecotic).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.ecotic_url));
        } else if (getString(R.string.mission_vision).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebContentActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.mission_vision));
            intent.putExtra("LINK",
                    getString(R.string.pdf_viewer).replaceAll("URL", getString(R.string.mission_vision_url)));
        } else if (getString(R.string.quality_policy).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebContentActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.quality_policy));
            intent.putExtra("LINK",
                    getString(R.string.pdf_viewer).replaceAll("URL", getString(R.string.quality_policy_url)));
        } else if (getString(R.string.axes_pillars_objectives).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebContentActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.axes_pillars_objectives));
            intent.putExtra("LINK",
                    getString(R.string.pdf_viewer).replaceAll("URL", getString(R.string.axes_pillars_objectives_url)));
        } else if (getString(R.string.symbols).equals(title)) {
            WebService.PENDING_ACTION = WebService.ACTION_SYMBOLS;
            loadInformations(getString(R.string.symbols), ItemsActivity.this);
        } else if (getString(R.string.digital_repository).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.digital_repository_url));
        } else if (getString(R.string.public_catalog).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.public_catalog_url));
        } else if (getString(R.string.databases).equals(title)) {
            intent = new Intent(ItemsActivity.this, WebActivity.class);
            intent.putExtra("URL", getString(R.string.databases_url));
        } else if (getString(R.string.directory).equals(category)) {
            loadContacts(title);
        } else if (getString(R.string.academic_offer).equals(category)) {
            intent = new Intent(ItemsActivity.this, ItemsActivity.class);
            intent.putExtra("CATEGORY", title);
            intent.putExtra("SUBCATEGORY", getString(R.string.academic_offer));
        } else if (getString(R.string.history).equals(title) ||
                getString(R.string.program_mission_vision).equals(title) ||
                getString(R.string.curriculum).equals(title) ||
                getString(R.string.profiles).equals(title) ||
                getString(R.string.program_contact).equals(title)) {
            loadProgramContent(category, title);
        } else if (getString(R.string.academic_calendar).equals(category)) {
            intent = new Intent(ItemsActivity.this, CalendarActivity.class);
            intent.putExtra("CATEGORY", title);
        }

        if (intent != null) {
            ItemsActivity.this.startActivity(intent);
        }
    }

    public void setItems(ArrayList<Item> items) {
        if (category.equals(getString(R.string.information_module))) {
            this.items = itemsPresenter.getInformationItems(ItemsActivity.this);
        } else if (category.equals(getString(R.string.services_module))) {
            this.items = itemsPresenter.getServicesItems(ItemsActivity.this);
        } else if (category.equals(getString(R.string.state_module))) {
            this.items = itemsPresenter.getStateItems(ItemsActivity.this);
        } else if (category.equals(getString(R.string.communication_module))) {
            this.items = itemsPresenter.getCommunicationItems(ItemsActivity.this);
        } else if (category.equals(getString(R.string.institution))) {
            this.items = itemsPresenter.getInstitutionItems(ItemsActivity.this);
        } else if (category.equals(getString(R.string.library_services))) {
            this.items = itemsPresenter.getLibraryItems(ItemsActivity.this);
        } else if (subcategory != null && subcategory.equals(getString(R.string.academic_offer))) {
            this.items = itemsPresenter.getProgramItems(ItemsActivity.this);
        } else {
            this.items = items;
        }
        mAdapter.setItems(this.items);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getString(R.string.institution).equals(category) ||
                getString(R.string.directory).equals(category) ||
                getString(R.string.academic_offer).equals(category) ||
                getString(R.string.academic_calendar).equals(category)) {
            Intent intent = new Intent(ItemsActivity.this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.information_module));
            ItemsActivity.this.startActivity(intent);
        } else if (getString(R.string.library_services).equals(category)) {
            Intent intent = new Intent(ItemsActivity.this, ItemsActivity.class);
            intent.putExtra("CATEGORY", getString(R.string.services_module));
            ItemsActivity.this.startActivity(intent);
        } else if (subcategory != null) {
            if (getString(R.string.directory).equals(subcategory)) {
                loadContactCategories(ItemsActivity.this);
            } else if (getString(R.string.academic_offer).equals(subcategory)) {
                loadPrograms(ItemsActivity.this);
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

        ArrayList<Item> contacts = ItemsPresenter.getContacts(categoryName, ItemsActivity.this);

        if (progressDialog.isShowing() && contacts.size() > 0) {
            progressDialog.dismiss();
            Intent intent = new Intent(ItemsActivity.this, ItemsActivity.class);
            intent.putExtra("CATEGORY", categoryName);
            intent.putExtra("SUBCATEGORY", getString(R.string.directory));
            intent.putParcelableArrayListExtra("ITEMS", contacts);
            startActivity(intent);
        } else if (contacts.size() == 0 && !Utilities.haveNetworkConnection(ItemsActivity.this)) {
            Toast.makeText(ItemsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public void loadProgramContent(String name, String type) {

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        String[] content = ItemsPresenter.getProgramContent(name, type, ItemsActivity.this);

        if (progressDialog.isShowing() && content[0] != null) {
            progressDialog.dismiss();
            Intent intent = new Intent(ItemsActivity.this, WebContentActivity.class);
            intent.putExtra("CATEGORY", type);
            intent.putExtra("LINK", content[0]);
            intent.putExtra("CONTENT", content[1]);
            startActivity(intent);
        } else if (content[0] == null && !Utilities.haveNetworkConnection(ItemsActivity.this)) {
            Toast.makeText(ItemsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

}
