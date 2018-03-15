package co.edu.uniquindio.campusuq.activity;

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
import co.edu.uniquindio.campusuq.util.DishesAdapter;
import co.edu.uniquindio.campusuq.util.DishesSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Dish;

public class DishesActivity extends MainActivity implements DishesAdapter.OnClickDishListener {

    private ArrayList<Dish> dishes = new ArrayList<>();
    private boolean newActivity = true, oldDishes = true;
    private DishesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private IntentFilter dishesFilter = new IntentFilter(WebService.ACTION_DISHES);
    private BroadcastReceiver dishesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadDishes(intent.getIntExtra("INSERTED", 0));
        }
    };

    public DishesActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_dishes);
        viewStub.inflate();

        FloatingActionButton insert = findViewById(R.id.fab);
        insert.setVisibility(View.VISIBLE);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishesActivity.this, DishesDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.restaurant_detail));
                intent.putExtra(DishesSQLiteController.columns[1], "");
                intent.putExtra(DishesSQLiteController.columns[2], "");
                intent.putExtra(DishesSQLiteController.columns[3], "");
                intent.putExtra(DishesSQLiteController.columns[4], "");
                startActivityForResult(intent, 0);
            }
        });

        loadDishes(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for(Dish dish : dishes) if(dish.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(dishes.indexOf(dish));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el plato: "+query, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDishes(int inserted) {

        if(!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldDishes ? (newActivity ? 0 : dishes.size()-1) : (inserted != 0? inserted-1 : 0);

        DishesSQLiteController dbController = new DishesSQLiteController(getApplicationContext(), 1);
        dishes = dbController.select(String.valueOf(inserted > 0 ? dishes.size()+inserted : dishes.size()+6), null, null);
        dbController.destroy();

        if(newActivity) {
            adapter = new DishesAdapter(dishes, DishesActivity.this);
            layoutManager = new LinearLayoutManager(DishesActivity.this, LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = findViewById(R.id.dishes_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if(!recyclerView.canScrollVertically(-1)) {
                            if(Utilities.haveNetworkConnection(DishesActivity.this)) {
                                oldDishes = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_DISHES, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(DishesActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if(!recyclerView.canScrollVertically(1)) {
                            oldDishes = true;
                            loadDishes(0);
                        }
                    }
                }
            });
            newActivity = false;
        } else {
            adapter.setDishes(dishes);
            layoutManager.scrollToPosition(scrollTo);
        }

        if(progressDialog.isShowing() && dishes.size() > 0) progressDialog.dismiss();

    }

    @Override
    public void onDishClick(int index) {
        DishesFragment.newInstance(index).show(getSupportFragmentManager(), null);
    }

    public Dish getDish(int index) {
        return dishes.get(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode  == RESULT_OK && !progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dishesReceiver, dishesFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dishesReceiver);
    }
}