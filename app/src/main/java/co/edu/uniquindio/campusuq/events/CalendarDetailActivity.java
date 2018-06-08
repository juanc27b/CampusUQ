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
import co.edu.uniquindio.campusuq.util.Utilities;

public class CalendarDetailActivity extends MainActivity {

    private TextView eventText;
    private RecyclerView recyclerView;
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

        eventText = findViewById(R.id.event_detail_text);
        recyclerView = findViewById(R.id.event_detail_recycler_view);
        categoryText = findViewById(R.id.category_detail_text);

        recyclerView.setHasFixedSize(true);
        recyclerView
                .setAdapter(new CalendarDetailItemsAdapter(new ArrayList<CalendarDetailItem>()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        event = getIntent().getStringExtra("EVENT");
        eventText.setText(event);
        category = getIntent().getStringExtra(Utilities.CATEGORY);
        categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
        setItems();
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            boolean found = false;

            if (recyclerView != null) {
                ArrayList<CalendarDetailItem> items =
                        ((CalendarDetailItemsAdapter) recyclerView.getAdapter()).getItems();

                for (CalendarDetailItem item : items) {
                    if (query.trim().toLowerCase().equals(item.getPeriod().toLowerCase()) ||
                            item.getPeriod().toLowerCase().contains(query.trim().toLowerCase()) ||
                            query.trim().toLowerCase().equals(item.getStart().toLowerCase()) ||
                            item.getStart().toLowerCase().contains(query.trim().toLowerCase()) ||
                            query.trim().toLowerCase().equals(item.getEnd().toLowerCase()) ||
                            item.getEnd().toLowerCase().contains(query.trim().toLowerCase())) {
                        recyclerView.smoothScrollToPosition(items.indexOf(item));
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                Toast.makeText(this, getString(R.string.date_no_found) + ": " + query,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (recyclerView != null) {
            setIntent(intent);
            event = intent.getStringExtra("EVENT");
            eventText.setText(event);
            category = intent.getStringExtra(Utilities.CATEGORY);
            categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
            setItems();
        }
    }

    private void setItems() {
        ((CalendarDetailItemsAdapter) recyclerView.getAdapter())
                .setItems(CalendarPresenter.getCalendarDetailItems(event, category, this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventText = null;
        recyclerView = null;
        categoryText = null;
        event = null;
        category = null;
    }

}
