package co.edu.uniquindio.campusuq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.DishesAdapter;
import co.edu.uniquindio.campusuq.util.DishesSQLiteController;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Dish;

public class DishesActivity extends MainActivity implements DishesAdapter.OnClickDishListener {
    public ArrayList<Dish> dishes;
    public boolean newActivity = true;
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DishesAdapter mAdapter;

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
        loadDishes(0);
    }

    private void loadDishes(int inserted) {
        if(!progressDialog.isShowing()) progressDialog.show();
        int scrollTo;
        DishesSQLiteController dbController = new DishesSQLiteController(getApplicationContext(), 1);
        dishes = dbController.select(null, null, null);
        dbController.destroy();
        scrollTo = newActivity? 0 : dishes.size()-1;
        if(newActivity) {
            mRecyclerView = findViewById(R.id.dishes_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new DishesAdapter(dishes, DishesActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(DishesActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            newActivity = false;
        } else {
            mAdapter.setDishes(dishes);
            mLayoutManager.scrollToPosition(scrollTo);
        }
        if(progressDialog.isShowing() && dishes.size() > 0) progressDialog.dismiss();
    }

    @Override
    public void onDishClick(int pos) {
        DishesFragment.newInstance(pos).show(getSupportFragmentManager(), null);
    }

    private IntentFilter dishesFilter = new IntentFilter(WebService.ACTION_DISHES);

    private BroadcastReceiver dishesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadDishes(intent.getIntExtra("INSERTED", 0));
        }
    };

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