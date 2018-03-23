package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;

public class QuotasDetailActivity extends MainActivity implements View.OnClickListener {
    private Intent intent;
    private Integer _ID;
    private EditText name, quota;

    public QuotasDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void handleIntent(Intent intent) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("CATEGORY"));
            this.intent = intent;
            setQuota();
        }
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_quotas_detail);
        viewStub.inflate();

        intent = getIntent();
        name = findViewById(R.id.quota_detail_name);
        quota = findViewById(R.id.quota_detail_quota);
        setQuota();

        findViewById(R.id.quota_detail_ok).setOnClickListener(this);
    }

    private void setQuota() {
        _ID = (Integer) intent.getSerializableExtra(QuotasSQLiteController.columns[0]);
        name.setText(intent.getStringExtra(QuotasSQLiteController.columns[2]));
        quota.setText(intent.getStringExtra(QuotasSQLiteController.columns[3]));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quota_detail_ok:
                if (Utilities.haveNetworkConnection(QuotasDetailActivity.this)) {
                    if (name.getText().length() != 0 && quota.getText().length() != 0) {
                        JSONObject json = new JSONObject();
                        try {
                            if (_ID != null) json.put("UPDATE_ID", _ID);
                            json.put(QuotasSQLiteController.columns[1], intent.getStringExtra(QuotasSQLiteController.columns[1]));
                            json.put(QuotasSQLiteController.columns[2], name.getText());
                            json.put(QuotasSQLiteController.columns[3], quota.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_QUOTAS, WebService.METHOD_POST, json.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(QuotasDetailActivity.this, getString(R.string.empty_string), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QuotasDetailActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
