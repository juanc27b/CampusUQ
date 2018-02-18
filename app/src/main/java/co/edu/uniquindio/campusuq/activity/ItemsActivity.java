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
import co.edu.uniquindio.campusuq.vo.Item;

public class ItemsActivity extends MainActivity implements ItemsAdapter.OnClickItemListener {

    private RecyclerView mRecyclerView;
    private ItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<Item> informationItems;
    public ArrayList<Item> servicesItems;
    public ArrayList<Item> stateItems;
    public ArrayList<Item> communicationItems;

    public ArrayList<Item> items;
    public int[] circleColors;
    public ArrayList<Integer> colors;

    public ItemsActivity() {
        circleColors = new int[7];
        circleColors[0] = R.drawable.circle_blue;
        circleColors[1] = R.drawable.circle_orange;
        circleColors[2] = R.drawable.circle_red;
        circleColors[3] = R.drawable.circle_yellow;
        circleColors[4] = R.drawable.circle_purple;
        circleColors[5] = R.drawable.circle_dark_blue;
        circleColors[6] = R.drawable.circle_green;

        colors = new ArrayList<Integer>();
        fillColors();

        super.setHasNavigationDrawerIcon(false);
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
            setItems(category, true);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent intent;
        switch(items.get(pos).getTitle()) {
            case R.string.events:
                intent = new Intent(ItemsActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.events));
                ItemsActivity.this.startActivity(intent);
                break;
            case R.string.news:
                intent = new Intent(ItemsActivity.this, NewsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news));
                ItemsActivity.this.startActivity(intent);
                break;
            case R.string.institution:

                break;
            case R.string.directory:

                break;
            case R.string.academic_offer:

                break;
            case R.string.academic_calendar:

                break;
            case R.string.employment_exchange:

                break;
            case R.string.institutional_welfare:

                break;
            case R.string.university_map:
                intent = new Intent(ItemsActivity.this, MapsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.university_map));
                ItemsActivity.this.startActivity(intent);
                break;
            case R.string.library_services:

                break;
            case R.string.radio:
                intent = new Intent(ItemsActivity.this, RadioActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.radio));
                ItemsActivity.this.startActivity(intent);
                break;
            case R.string.pqrsd_system:

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

                break;
            case R.string.parking_lots:

                break;
            case R.string.institutional_mail:

                break;
            case R.string.web_page:

                break;
            case R.string.ecotic:

                break;
            default:
                break;
        }
        Toast.makeText(this, "Item clicked: "+getString(items.get(pos).getTitle()), Toast.LENGTH_SHORT).show();
    }

    public void setItems(String category, boolean oldActivity) {
        if (category.equals(getString(R.string.information_module))) {
            this.items = informationItems != null ? informationItems : getInformationItems();
        } else if (category.equals(getString(R.string.services_module))) {
            this.items = servicesItems != null ? servicesItems : getServicesItems();
        } else if (category.equals(getString(R.string.state_module))) {
            this.items = stateItems != null ? stateItems : getStateItems();
        } else if (category.equals(getString(R.string.communication_module))) {
            this.items = communicationItems != null ? communicationItems : getCommunicationItems();
        }
        if (oldActivity) {
            mAdapter.setItems(this.items);
        }
    }

    public ArrayList<Item> getInformationItems() {
        informationItems = new ArrayList<Item>();
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_events, R.string.events, R.string.events_description));
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_news, R.string.news, R.string.news_description));
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_institution, R.string.institution, R.string.institution_description));
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_directory, R.string.directory, R.string.directory_description));
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_academic_offer, R.string.academic_offer, R.string.academic_offer_description));
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_academic_calendar, R.string.academic_calendar, R.string.academic_calendar_description));
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_employment_exchange, R.string.employment_exchange, R.string.employment_exchange_description));
        informationItems.add(
                new Item(getColor(), R.drawable.ic_menu_institutional_welfare, R.string.institutional_welfare, R.string.institutional_welfare_description));
        return informationItems;
    }

    public ArrayList<Item> getServicesItems() {
        servicesItems = new ArrayList<Item>();
        servicesItems.add(
                new Item(getColor(), R.drawable.ic_menu_university_map, R.string.university_map, R.string.university_map_description));
        servicesItems.add(
                new Item(getColor(), R.drawable.ic_menu_library_services, R.string.library_services, R.string.library_services_description));
        servicesItems.add(
                new Item(getColor(), R.drawable.ic_menu_radio, R.string.radio, R.string.radio_description));
        servicesItems.add(
                new Item(getColor(), R.drawable.ic_menu_pqrsd_system, R.string.pqrsd_system, R.string.pqrsd_system_description));
        servicesItems.add(
                new Item(getColor(), R.drawable.ic_menu_lost_objects, R.string.lost_objects, R.string.lost_objects_description));
        servicesItems.add(
                new Item(getColor(), R.drawable.ic_menu_security_system, R.string.security_system, R.string.security_system_description));
        return servicesItems;
    }

    public ArrayList<Item> getStateItems() {
        stateItems = new ArrayList<Item>();
        stateItems.add(
                new Item(getColor(), R.drawable.ic_menu_restaurant, R.string.restaurant, R.string.restaurant_description));
        stateItems.add(
                new Item(getColor(), R.drawable.ic_menu_billboard_information, R.string.billboard_information, R.string.billboard_information_description));
        stateItems.add(
                new Item(getColor(), R.drawable.ic_menu_computer_rooms, R.string.computer_rooms, R.string.computer_rooms_description));
        stateItems.add(
                new Item(getColor(), R.drawable.ic_menu_parking_lots, R.string.parking_lots, R.string.parking_lots_description));
        return stateItems;
    }

    public ArrayList<Item> getCommunicationItems() {
        communicationItems = new ArrayList<Item>();
        communicationItems.add(
                new Item(getColor(), R.drawable.ic_menu_institutional_mail, R.string.institutional_mail, R.string.institutional_mail_description));
        communicationItems.add(
                new Item(getColor(), R.drawable.ic_menu_web_page, R.string.web_page, R.string.web_page_description));
        communicationItems.add(
                new Item(getColor(), R.drawable.ic_menu_ecotic, R.string.ecotic, R.string.ecotic_description));
        return communicationItems;
    }

    public int getColor() {
        if (colors.size() == 0) {
            fillColors();
        }
        int random = (int) (Math.random()*colors.size());
        int pos = colors.remove(random);
        return circleColors[pos];
    }

    public void fillColors() {
        colors.add(0,0);
        colors.add(1,1);
        colors.add(2,2);
        colors.add(3,3);
        colors.add(4,4);
        colors.add(5,5);
        colors.add(6,6);
    }

}
