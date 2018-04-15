package co.edu.uniquindio.campusuq.events;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;

public class CalendarDetailActivity extends MainActivity {

    private CalendarDetailItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<CalendarDetailItem> items = new ArrayList<>();

    private TextView eventText;
    private TextView categoryText;
    private String event;
    private String category;

    public CalendarDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_calendar_detail);
        stub.inflate();

        RecyclerView mRecyclerView = findViewById(R.id.event_detail_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CalendarDetailItemsAdapter(items);
        mRecyclerView.setAdapter(mAdapter);

        eventText = findViewById(R.id.event_detail_text);
        categoryText = findViewById(R.id.category_detail_text);
        event = getIntent().getStringExtra("EVENT");
        category = getIntent().getStringExtra("CATEGORY");
        eventText.setText(event);
        categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
        setItems();

    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            boolean found = false;

            for (CalendarDetailItem item : items) {
                if (query.trim().toLowerCase().equals(item.getPeriod().toLowerCase()) ||
                        item.getPeriod().toLowerCase().contains(query.trim().toLowerCase()) ||
                        query.trim().toLowerCase().equals(item.getStart().toLowerCase()) ||
                        item.getStart().toLowerCase().contains(query.trim().toLowerCase()) ||
                        query.trim().toLowerCase().equals(item.getEnd().toLowerCase()) ||
                        item.getEnd().toLowerCase().contains(query.trim().toLowerCase())) {
                    mLayoutManager.scrollToPosition(items.indexOf(item));
                    found = true;
                    break;
                }
            }

            if (!found) {
                Toast.makeText(this, getString(R.string.date_no_found) + ": " + query,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (mAdapter != null) {
            event = intent.getStringExtra("EVENT");
            category = intent.getStringExtra("CATEGORY");
            eventText.setText(event);
            categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
            setItems();
        }
    }

    private void setItems() {
        items = CalendarPresenter.getCalendarDetailItems(event, category, this);
        mAdapter.setItems(items);
    }

}
