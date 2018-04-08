package co.edu.uniquindio.campusuq.dishes;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.users.User;

public class DishesActivity extends MainActivity implements DishesAdapter.OnClickDishListener,
        View.OnClickListener {

    private ArrayList<Dish> dishes = new ArrayList<>();
    private boolean newActivity = true;
    private DishesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean oldDishes = true;

    private IntentFilter dishesFilter = new IntentFilter(WebService.ACTION_DISHES);
    private BroadcastReceiver dishesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadDishes(intent.getIntExtra("INSERTED", 0));
            String response = intent.getStringExtra("RESPONSE");
            if (response != null) Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
        }
    };

    public DishesActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_dishes);
        viewStub.inflate();
        FloatingActionButton insert = findViewById(R.id.fab);

        insert.setOnClickListener(this);
        User user = UsersPresenter.loadUser(this);
        if (user != null && user.getAdministrator().equals("S")) insert.setVisibility(View.VISIBLE);

        loadDishes(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (Dish dish : dishes)
                if (dish.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(dishes.indexOf(dish));
                return;
            }
            Toast.makeText(this, getString(R.string.dish_no_found)+query,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDishes(int inserted) {

        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldDishes ?
                (newActivity ? 0 : dishes.size()-1) : (inserted > 0 ? inserted-1 : 0);

        dishes = DishesPresenter.loadDishes(this,
                dishes.size()+(inserted > 0 ? inserted : 12));

        if (newActivity) {
            newActivity = false;
            adapter = new DishesAdapter(dishes, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.dishes_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (Utilities.haveNetworkConnection(DishesActivity.this)) {
                                oldDishes = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                        WebService.ACTION_DISHES, WebService.METHOD_GET,
                                        null);
                            } else {
                                Toast.makeText(DishesActivity.this,
                                        R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        } else if (!recyclerView.canScrollVertically(1)) {
                            oldDishes = true;
                            loadDishes(0);
                        }
                    }
                }
            });
        } else {
            adapter.setDishes(dishes);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && dishes.size() > 0) progressDialog.dismiss();

    }

    @Override
    public void onDishClick(int index) {
        User user = UsersPresenter.loadUser(this);
        if (user != null && user.getAdministrator().equals("S")) DishesFragment.newInstance(index)
                .show(getSupportFragmentManager(), null);
        else Toast.makeText(this, R.string.no_administrator,
                Toast.LENGTH_SHORT).show();
    }

    public Dish getDish(int index) {
        return dishes.get(index);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(this, DishesDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.restaurant_detail));
                startActivityForResult(intent, 0);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode  == RESULT_OK && !progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        registerReceiver(dishesReceiver, dishesFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(dishesReceiver);
    }

}