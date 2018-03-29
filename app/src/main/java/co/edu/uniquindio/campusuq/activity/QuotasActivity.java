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
import co.edu.uniquindio.campusuq.util.QuotasPresenter;
import co.edu.uniquindio.campusuq.util.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.util.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Quota;
import co.edu.uniquindio.campusuq.vo.User;

public class QuotasActivity extends MainActivity implements QuotasAdapter.OnClickQuotaListener,
        View.OnClickListener {

    private String type;
    private ArrayList<Quota> quotas = new ArrayList<>();
    private boolean newActivity = true;
    private QuotasAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private IntentFilter quotasFilter = new IntentFilter(WebService.ACTION_QUOTAS);
    private BroadcastReceiver quotasReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadQuotas();
            String response = intent.getStringExtra("RESPONSE");
            if (response != null) Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
        }
    };

    public QuotasActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_quotas);
        viewStub.inflate();
        FloatingActionButton insert = findViewById(R.id.fab);

        insert.setOnClickListener(this);
        User user = UsersPresenter.loadUser(this);
        if (user != null && user.getAdministrator().equals("S")) insert.setVisibility(View.VISIBLE);

        loadQuotas();
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (Quota quota : quotas)
                if (quota.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(quotas.indexOf(quota));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el cupo: "+query,
                    Toast.LENGTH_SHORT).show();
        } else {
            String category = intent.getStringExtra("CATEGORY");
            if (category.equals(getString(R.string.computer_rooms))) type = "S";
            else if (category.equals(getString(R.string.parking_lots))) type = "P";
            else if (category.equals(getString(R.string.laboratories))) type = "L";
            else if (category.equals(getString(R.string.studio_zones))) type = "E";
            else if (category.equals(getString(R.string.cultural_and_sport))) type = "C";
            else if (category.equals(getString(R.string.auditoriums))) type = "A";
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(category);
                loadQuotas();
            }
        }
    }

    private void loadQuotas() {

        if (!progressDialog.isShowing()) progressDialog.show();

        quotas = QuotasPresenter.loadQuotas(getApplicationContext(), type);

        if (newActivity) {
            newActivity = false;
            adapter = new QuotasAdapter(quotas, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.quotas_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1) ||
                                !recyclerView.canScrollVertically(1)) {
                            if (Utilities.haveNetworkConnection(QuotasActivity.this)) {
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                        WebService.ACTION_QUOTAS, WebService.METHOD_GET,
                                        null);
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
        User user = UsersPresenter.loadUser(this);
        if (user != null && user.getAdministrator().equals("S")) QuotasFragment
                .newInstance(fragment_quotas, index).show(getSupportFragmentManager(), null);
        else Toast.makeText(this, getString(R.string.no_administrator), Toast.LENGTH_SHORT)
                .show();
    }

    public Quota getQuota(int index) {
        return quotas.get(index);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(this, QuotasDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.quota_detail_insert));
                intent.putExtra(QuotasSQLiteController.columns[1], type);
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
