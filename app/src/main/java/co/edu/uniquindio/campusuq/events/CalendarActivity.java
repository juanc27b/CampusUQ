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
import co.edu.uniquindio.campusuq.util.Utilities;

public class CalendarActivity extends MainActivity
        implements CalendarItemsAdapter.OnClickItemListener {

    private TextView categoryText;
    private RecyclerView recyclerView;

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

        categoryText = findViewById(R.id.category_text);
        recyclerView = findViewById(R.id.events_recycler_view);

        category = getIntent().getStringExtra(Utilities.CATEGORY);
        categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CalendarItemsAdapter(new ArrayList<CalendarItem>(),
                this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        setItems();
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (recyclerView != null) {
                ArrayList<CalendarItem> items =
                        ((CalendarItemsAdapter) recyclerView.getAdapter()).getItems();

                for (CalendarItem item : items) {
                    if (StringUtils.stripAccents(item.getEvent()).toLowerCase()
                            .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                        recyclerView.getLayoutManager().scrollToPosition(items.indexOf(item));
                        return;
                    }
                }
            }

            Toast.makeText(this, getString(R.string.event_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else if (recyclerView != null) {
            setIntent(intent);
            category = intent.getStringExtra(Utilities.CATEGORY);
            categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
            setItems();
        }
    }

    private void setItems() {
        ((CalendarItemsAdapter) recyclerView.getAdapter())
                .setItems(CalendarPresenter.getCalendarItems(category, this));
    }

    @Override
    public void onCalendarItemClick(int index) {
        startActivity(new Intent(this, CalendarDetailActivity.class)
                .putExtra("EVENT", ((CalendarItemsAdapter) recyclerView.getAdapter())
                        .getItems().get(index).getEvent())
                .putExtra(Utilities.CATEGORY, category));
    }

}
