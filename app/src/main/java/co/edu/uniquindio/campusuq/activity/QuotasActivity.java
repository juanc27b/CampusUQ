package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
    public ArrayList<Quota> quotas;
    public boolean oldQuotas = true, newActivity = true;
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public QuotasAdapter mAdapter;

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
        loadQuotas(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for(Quota mQuota : quotas) if(
                query.trim().toLowerCase().equals(mQuota.getName().toLowerCase()) ||
                mQuota.getName().toLowerCase().contains(query.trim().toLowerCase())
            ) {
                mLayoutManager.scrollToPosition(quotas.indexOf(mQuota));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el cupo: "+query, Toast.LENGTH_SHORT).show();
        } else if(mAdapter != null) {
            getSupportActionBar().setTitle(intent.getStringExtra("CATEGORY"));
            loadQuotas(0);
        }
    }

    @Override
    public void onQuotaClick(int index) {
        QuotasFragment.newInstance(index).show(getSupportFragmentManager(), null);
    }

    private void loadQuotas(int inserted) {
        if(!progressDialog.isShowing()) progressDialog.show();
        int scrollTo;
        QuotasSQLiteController dbController = new QuotasSQLiteController(getApplicationContext(), 1);
        String category = getIntent().getStringExtra("CATEGORY");
        String validRows = null;
        if(category.equals(getString(R.string.computer_rooms))) validRows = '`'+QuotasSQLiteController.CAMPOS_TABLA[1]+"` = 'S'";
        else if(category.equals(getString(R.string.parking_lots))) validRows = '`'+QuotasSQLiteController.CAMPOS_TABLA[1]+"` = 'P'";
        quotas = dbController.select(null, validRows, null);
        dbController.destroy();
        if(oldQuotas) scrollTo = newActivity? 0 : quotas.size()-1;
        else scrollTo = inserted != 0? inserted-1 : 0;
        if(newActivity) {
            mRecyclerView = findViewById(R.id.quotas_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new QuotasAdapter(quotas, this);
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(QuotasActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if(!mRecyclerView.canScrollVertically(-1)) {
                            if(Utilities.haveNetworkConnection(QuotasActivity.this)) {
                                oldQuotas = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_QUOTAS, WebService.METHOD_GET);
                            } else {
                                Toast.makeText(QuotasActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if(!mRecyclerView.canScrollVertically(1)) {
                            oldQuotas = true;
                            loadQuotas(0);
                        }
                    }
                }
            });
            newActivity = false;
        } else {
            mAdapter.setQuotas(quotas);
            mLayoutManager.scrollToPosition(scrollTo);
        }
        if(progressDialog.isShowing() && quotas.size() > 0) progressDialog.dismiss();
    }

    private IntentFilter quotasFilter = new IntentFilter(WebService.ACTION_QUOTAS);

    private BroadcastReceiver quotasReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadQuotas(intent.getIntExtra("INSERTED", 0));
        }
    };

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