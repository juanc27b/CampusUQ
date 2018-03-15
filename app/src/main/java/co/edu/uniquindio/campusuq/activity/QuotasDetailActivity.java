package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;

public class QuotasDetailActivity extends MainActivity {
    private Intent intent;
    private EditText name, quota;

    public QuotasDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
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
        name.setText(intent.getStringExtra(QuotasSQLiteController.columns[2]));
        quota = findViewById(R.id.quota_detail_quota);
        quota.setText(intent.getStringExtra(QuotasSQLiteController.columns[3]));
        findViewById(R.id.quota_detail_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = intent.getStringExtra(QuotasSQLiteController.columns[0]);
                JSONObject json = new JSONObject();
                try {
                    if(id != null) json.put("UPDATE_ID", id);
                    json.put(QuotasSQLiteController.columns[1], intent.getStringExtra(QuotasSQLiteController.columns[1]));
                    json.put(QuotasSQLiteController.columns[2], name.getText());
                    json.put(QuotasSQLiteController.columns[3], quota.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_QUOTAS, WebService.METHOD_POST, json.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
