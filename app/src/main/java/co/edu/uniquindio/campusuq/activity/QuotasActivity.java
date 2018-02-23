package co.edu.uniquindio.campusuq.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasAdapter;
import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasActivity extends MainActivity implements QuotasAdapter.OnClickQuotaListener {
    public ArrayList<Quota> quotas;
    public boolean oldActivity = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private QuotasAdapter mAdapter;

    public QuotasActivity() {
        this.quotas = new ArrayList<Quota>();
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent() {
        super.addContent();
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);
        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_quotas);
    }

    @Override
    public void onQuotaClick(int pos, String action) {
    }

    private void loadQuotas(int inserted) {
        mRecyclerView = findViewById(R.id.news_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(QuotasActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new QuotasAdapter(quotas, QuotasActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}