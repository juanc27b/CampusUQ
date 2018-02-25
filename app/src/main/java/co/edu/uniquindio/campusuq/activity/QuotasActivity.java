package co.edu.uniquindio.campusuq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasAdapter;
import co.edu.uniquindio.campusuq.util.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasActivity extends MainActivity implements QuotasAdapter.OnClickQuotaListener {
    public ArrayList<Quota> quotas;
    public boolean newActivity = true;
    private RecyclerView.LayoutManager mLayoutManager;
    private QuotasAdapter mAdapter;

    public QuotasActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent() {
        super.addContent();
        super.setBackground(
            R.drawable.portrait_normal_background,
            R.drawable.landscape_normal_background
        );
        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_quotas);
        viewStub.inflate();
        loadQuotas(0);
    }

    @Override
    public void onQuotaClick(int pos, String action) {
    }

    private void loadQuotas(int inserted) {
        if(!progressDialog.isShowing()) progressDialog.show();
        int scrollTo = 0;
        QuotasSQLiteController dbController = new QuotasSQLiteController(
            getApplicationContext(), 1
        );
        String category = getIntent().getStringExtra("CATEGORY");
        String validRows = null;
        if(category.equals(getString(R.string.computer_rooms))) validRows =
            '`'+QuotasSQLiteController.CAMPOS_TABLA[1]+"` = 'S'";
        else if(category.equals(getString(R.string.parking_lots))) validRows =
            '`'+QuotasSQLiteController.CAMPOS_TABLA[1]+"` = 'P'";
        quotas = dbController.select(null, validRows, null);
        dbController.destroy();
        if(newActivity) {
            RecyclerView mRecyclerView = findViewById(R.id.quotas_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter = new QuotasAdapter(
                quotas, QuotasActivity.this
            ));
            mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(
                QuotasActivity.this, LinearLayoutManager.VERTICAL, false
            ));
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