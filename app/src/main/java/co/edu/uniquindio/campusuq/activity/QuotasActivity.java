package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasAdapter;
import co.edu.uniquindio.campusuq.util.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasActivity extends MainActivity implements QuotasAdapter.OnClickQuotaListener {

    private String category;
    private ArrayList<Quota> quotas = new ArrayList<>();
    private boolean newActivity = true, oldQuotas = true;
    private QuotasAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private IntentFilter quotasFilter = new IntentFilter(WebService.ACTION_QUOTAS);
    private BroadcastReceiver quotasReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadQuotas();
        }
    };

    public QuotasActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_quotas);
        viewStub.inflate();

        loadQuotas();
    }

    @Override
    public void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for(Quota quota : quotas) if(quota.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(quotas.indexOf(quota));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el cupo: "+query, Toast.LENGTH_SHORT).show();
        } else {
            category = intent.getStringExtra("CATEGORY");
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setTitle(category);
                loadQuotas();
            }
        }
    }

    private void loadQuotas() {

        if (!progressDialog.isShowing()) progressDialog.show();

        QuotasSQLiteController dbController = new QuotasSQLiteController(getApplicationContext(), 1);
        String validRows = null;

        if (category.equals(getString(R.string.computer_rooms))) validRows = '`'+QuotasSQLiteController.columns[1]+"` = 'S'";
        else if (category.equals(getString(R.string.parking_lots))) validRows = '`'+QuotasSQLiteController.columns[1]+"` = 'P'";
        quotas = dbController.select(validRows, null);
        dbController.destroy();

        if(newActivity) {
            adapter = new QuotasAdapter(quotas, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = findViewById(R.id.quotas_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if(!recyclerView.canScrollVertically(-1) || !recyclerView.canScrollVertically(1)) {
                            if(Utilities.haveNetworkConnection(QuotasActivity.this)) {
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_QUOTAS, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(QuotasActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
            newActivity = false;
        } else {
            adapter.setQuotas(quotas);
            layoutManager.scrollToPosition(0);
        }

        if(progressDialog.isShowing() && quotas.size() > 0) progressDialog.dismiss();

    }

    @Override
    public void onQuotaClick(int index) {
        QuotasFragment.newInstance(index).show(getSupportFragmentManager(), null);
    }

    public Quota getQuota(int index) {
        return quotas.get(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(quotasReceiver, quotasFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(quotasReceiver);
    }
}