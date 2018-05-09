package co.edu.uniquindio.campusuq.events;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;

public class CalendarActivity extends MainActivity implements
        CalendarItemsAdapter.OnClickItemListener {

    private CalendarItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<CalendarItem> items = new ArrayList<>();

    private TextView categoryText;
    private String category;

    public CalendarActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_calendar);
        stub.inflate();

        RecyclerView mRecyclerView = findViewById(R.id.events_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CalendarItemsAdapter(items, this);
        mRecyclerView.setAdapter(mAdapter);

        categoryText = findViewById(R.id.category_text);
        category = getIntent().getStringExtra("CATEGORY");
        categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
        setItems();
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            for (CalendarItem item : items) {
                if (StringUtils.stripAccents(item.getEvent()).toLowerCase()
                        .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                    mLayoutManager.scrollToPosition(items.indexOf(item));
                    return;
                }
            }

            Toast.makeText(this, getString(R.string.event_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else if (mAdapter != null) {
            category = intent.getStringExtra("CATEGORY");
            categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
            setItems();
        }
    }

    private void setItems() {
        this.items = CalendarPresenter.getCalendarItems(category, this);
        mAdapter.setItems(this.items);
    }

    @Override
    public void onCalendarItemClick(int pos) {
        Intent intent = new Intent(this, CalendarDetailActivity.class);
        intent.putExtra("EVENT", this.items.get(pos).getEvent());
        intent.putExtra("CATEGORY", category);
        startActivity(intent);
    }

}
