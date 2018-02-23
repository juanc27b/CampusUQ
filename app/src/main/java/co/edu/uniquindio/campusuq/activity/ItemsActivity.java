package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.ItemsAdapter;
import co.edu.uniquindio.campusuq.util.ItemsPresenter;
import co.edu.uniquindio.campusuq.util.LoadWebContentAsync;
import co.edu.uniquindio.campusuq.vo.Item;

public class ItemsActivity extends MainActivity implements ItemsAdapter.OnClickItemListener {

    private RecyclerView mRecyclerView;
    private ItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<Item> items;
    public ItemsPresenter itemsPresenter;

    public ItemsActivity() {
        super.setHasNavigationDrawerIcon(false);
        itemsPresenter = new ItemsPresenter();
    }

    @Override
    public void addContent() {
        super.addContent();

        super.setBackground(R.drawable.portrait_background, R.drawable.landscape_background);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_items);
        View inflated = stub.inflate();

        setItems(getIntent().getStringExtra("CATEGORY"), false);

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

    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            boolean found = false;
            for (Item item : items) {
                if (query.trim().toLowerCase().equals(getString(item.getTitle()).toLowerCase()) ||
                        getString(item.getTitle()).toLowerCase().contains(query.trim().toLowerCase())) {
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
            String category = intent.getStringExtra("CATEGORY");
            getSupportActionBar().setTitle(category);
            setItems(category, true);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent intent = null;
        String url = null;
        LoadWebContentAsync loadWebContentAsync = new LoadWebContentAsync(ItemsActivity.this);
        switch(items.get(pos).getTitle()) {
            case R.string.events:
                intent = new Intent(ItemsActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.events));
                break;
            case R.string.news:
                intent = new Intent(ItemsActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news));
                break;
            case R.string.institution:
                intent = new Intent(ItemsActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.institution));
                break;
            case R.string.directory:

                break;
            case R.string.academic_offer:

                break;
            case R.string.academic_calendar:

                break;
            case R.string.employment_exchange:
                intent = new Intent(ItemsActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.employment_exchange_url));
                break;
            case R.string.institutional_welfare:
                loadWebContentAsync.execute(getString(R.string.institutional_welfare));
                break;
            case R.string.university_map:
                intent = new Intent(ItemsActivity.this, MapsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.university_map));
                break;
            case R.string.library_services:
                intent = new Intent(ItemsActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.library_services));
                break;
            case R.string.radio:
                intent = new Intent(ItemsActivity.this, RadioActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.radio));
                break;
            case R.string.pqrsd_system:
                intent = new Intent(ItemsActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.pqrsd_system_url));
                break;
            case R.string.lost_objects:

                break;
            case R.string.security_system:

                break;
            case R.string.restaurant:

                break;
            case R.string.billboard_information:

                break;
            case R.string.computer_rooms:
                intent = new Intent(ItemsActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.computer_rooms));
                break;
            case R.string.parking_lots:
                intent = new Intent(ItemsActivity.this, QuotasActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.parking_lots));
                break;
            case R.string.institutional_mail:

                break;
            case R.string.web_page:
                intent = new Intent(ItemsActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.web_page_url));
                break;
            case R.string.ecotic:
                intent = new Intent(ItemsActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.ecotic_url));
                break;
            case R.string.mission_vision:
                intent = new Intent(ItemsActivity.this, WebContentActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.mission_vision));
                intent.putExtra("LINK",
                        getString(R.string.pdf_viewer).replaceAll("URL", getString(R.string.mission_vision_url)));
                break;
            case R.string.quality_policy:
                intent = new Intent(ItemsActivity.this, WebContentActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.quality_policy));
                intent.putExtra("LINK",
                        getString(R.string.pdf_viewer).replaceAll("URL", getString(R.string.quality_policy_url)));
                break;
            case R.string.axes_pillars_objectives:
                intent = new Intent(ItemsActivity.this, WebContentActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.axes_pillars_objectives));
                intent.putExtra("LINK",
                        getString(R.string.pdf_viewer).replaceAll("URL", getString(R.string.axes_pillars_objectives_url)));
                break;
            case R.string.symbols:
                loadWebContentAsync.execute(getString(R.string.symbols));
                break;
            case R.string.digital_repository:
                intent = new Intent(ItemsActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.digital_repository_url));
                break;
            case R.string.public_catalog:
                intent = new Intent(ItemsActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.public_catalog_url));
                break;
            case R.string.databases:
                intent = new Intent(ItemsActivity.this, WebActivity.class);
                intent.putExtra("URL", getString(R.string.databases_url));
                break;
            default:
                break;
        }
        if (intent != null) {
            ItemsActivity.this.startActivity(intent);
        }
    }

    public void setItems(String category, boolean oldActivity) {
        if (category.equals(getString(R.string.information_module))) {
            this.items = itemsPresenter.getInformationItems();
        } else if (category.equals(getString(R.string.services_module))) {
            this.items = itemsPresenter.getServicesItems();
        } else if (category.equals(getString(R.string.state_module))) {
            this.items = itemsPresenter.getStateItems();
        } else if (category.equals(getString(R.string.communication_module))) {
            this.items = itemsPresenter.getCommunicationItems();
        } else if (category.equals(getString(R.string.institution))) {
            this.items = itemsPresenter.getInstitutionItems();
        } else if (category.equals(getString(R.string.library_services))) {
            this.items = itemsPresenter.getLibraryItems();
        }
        if (oldActivity) {
            mAdapter.setItems(this.items);
        }
    }

}
