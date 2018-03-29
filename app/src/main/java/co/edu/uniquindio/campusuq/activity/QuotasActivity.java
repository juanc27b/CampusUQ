package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class QuotasActivity extends MainActivity implements QuotasAdapter.OnClickQuotaListener, View.OnClickListener {

    private String[] type;
    private ArrayList<Quota> quotas = new ArrayList<>();
    private boolean newActivity = true;
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
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (Quota quota : quotas) if (quota.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(quotas.indexOf(quota));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el cupo: "+query, Toast.LENGTH_SHORT).show();
        } else {
            String category = intent.getStringExtra("CATEGORY");
            if (category.equals(getString(R.string.computer_rooms))) type = new String[]{"S"};
            else if (category.equals(getString(R.string.parking_lots))) type = new String[]{"P"};
            else if (category.equals(getString(R.string.laboratories))) type = new String[]{"L"};
            else if (category.equals(getString(R.string.study_areas))) type = new String[]{"E"};
            else if (category.equals(getString(R.string.cultural_and_sport))) type = new String[]{"C"};
            else if (category.equals(getString(R.string.auditoriums))) type = new String[]{"A"};
            else type = new String[]{""};
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(category);
                loadQuotas();
            }
        }
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_quotas);
        viewStub.inflate();
        FloatingActionButton insert = findViewById(R.id.fab);

        insert.setOnClickListener(this);
        insert.setVisibility(View.VISIBLE);

        loadQuotas();
    }

    private void loadQuotas() {

        if (!progressDialog.isShowing()) progressDialog.show();

        QuotasSQLiteController dbController = new QuotasSQLiteController(getApplicationContext(), 1);
        quotas = dbController.select('`'+QuotasSQLiteController.columns[1]+"` = ?", type);
        dbController.destroy();

        if (newActivity) {
            newActivity = false;
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
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1) || !recyclerView.canScrollVertically(1)) {
                            if (Utilities.haveNetworkConnection(QuotasActivity.this)) {
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                        WebService.ACTION_QUOTAS, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(QuotasActivity.this,
                                        getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        } else {
            adapter.setQuotas(quotas);
            layoutManager.scrollToPosition(0);
        }

        if (progressDialog.isShowing() && quotas.size() > 0) progressDialog.dismiss();

    }

    @Override
    public void onQuotaClick(boolean fragment_quotas, int index) {
        QuotasFragment.newInstance(fragment_quotas, index).show(getSupportFragmentManager(), null);
    }

    public Quota getQuota(int index) {
        return quotas.get(index);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(this, QuotasDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.quota_detail));
                intent.putExtra(QuotasSQLiteController.columns[1], type[0]);
                intent.putExtra(QuotasSQLiteController.columns[2], "");
                intent.putExtra(QuotasSQLiteController.columns[3], "");
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
        registerReceiver(quotasReceiver, quotasFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(quotasReceiver);
    }
}
