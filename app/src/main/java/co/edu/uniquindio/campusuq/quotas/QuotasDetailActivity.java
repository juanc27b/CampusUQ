package co.edu.uniquindio.campusuq.quotas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class QuotasDetailActivity extends MainActivity implements View.OnClickListener {

    private Intent intent;
    private String _ID;
    private String type;
    private EditText name;
    private EditText quota;

    public QuotasDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_quotas_detail);
        viewStub.inflate();

        intent = getIntent();
        name = findViewById(R.id.quota_detail_name);
        quota = findViewById(R.id.quota_detail_quota);
        setQuota();

        findViewById(R.id.quota_detail_ok).setOnClickListener(this);
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

    private void setQuota() {
        _ID = intent.getStringExtra(QuotasSQLiteController.columns[0]);
        type = intent.getStringExtra(QuotasSQLiteController.columns[1]);
        name.setText(intent.getStringExtra(QuotasSQLiteController.columns[2]));
        quota.setText(intent.getStringExtra(QuotasSQLiteController.columns[3]));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quota_detail_ok:
                if (Utilities.haveNetworkConnection(QuotasDetailActivity.this)) {
                    if (name.getText().length() != 0 && quota.getText().length() != 0) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_quotas_category))
                                .setAction(getString(_ID == null ?
                                        R.string.analytics_create_action : R.string.analytics_modify_action))
                                .setLabel(getString(
                                        type.equals("S") ? R.string.analytics_computer_rooms_label :
                                        type.equals("P") ? R.string.analytics_parking_lots_label :
                                        type.equals("L") ? R.string.analytics_laboratories_label :
                                        type.equals("E") ? R.string.analytics_study_areas_label :
                                        type.equals("C") ? R.string.analytics_cultural_and_sport_label :
                                        R.string.analytics_auditoriums_label))
                                .setValue(1)
                                .build());
                        JSONObject json = new JSONObject();
                        try {
                            if (_ID != null) json.put("UPDATE_ID", _ID);
                            json.put(QuotasSQLiteController.columns[1],
                                    intent.getStringExtra(QuotasSQLiteController.columns[1]));
                            json.put(QuotasSQLiteController.columns[2], name.getText());
                            json.put(QuotasSQLiteController.columns[3], quota.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                WebService.ACTION_QUOTAS, WebService.METHOD_POST, json.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.empty_string),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}
