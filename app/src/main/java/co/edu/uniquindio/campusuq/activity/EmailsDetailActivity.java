package co.edu.uniquindio.campusuq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.EmailsSQLiteController;
import co.edu.uniquindio.campusuq.util.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;

public class EmailsDetailActivity extends MainActivity implements View.OnClickListener {

    private TextView from;
    private EditText to, name, content;

    private IntentFilter emailsFilter = new IntentFilter(WebService.ACTION_EMAILS);
    private BroadcastReceiver emailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int inserted = intent.getIntExtra("INSERTED", 0);
            if (inserted == 0) {
                Toast.makeText(context, context.getString(R.string.email_failed_sending), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.email_successful_sending), Toast.LENGTH_SHORT).show();
                finish();
            }
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    };

    public EmailsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_emails_detail);
        viewStub.inflate();

        from = findViewById(R.id.email_detail_from);
        to = findViewById(R.id.email_detail_to);
        name = findViewById(R.id.email_detail_name);
        content = findViewById(R.id.email_detail_content);

        from.setText(UsersPresenter.loadUser(EmailsDetailActivity.this).getEmail());

        findViewById(R.id.email_detail_ok).setOnClickListener(this);
    }

    @Override
    public void handleIntent(Intent intent) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            to.setText("");
            name.setText("");
            content.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
        case R.id.email_detail_ok:
            if (from.getText().equals("campusuq@uniquindio.edu.co")) {
                Toast.makeText(EmailsDetailActivity.this, getString(R.string.email_from_invalid), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(to.getText()) || !Patterns.EMAIL_ADDRESS.matcher(to.getText()).matches()) {
                Toast.makeText(EmailsDetailActivity.this, getString(R.string.email_to_invalid), Toast.LENGTH_SHORT).show();
            } else if (name.getText().length() < 1) {
                Toast.makeText(EmailsDetailActivity.this, getString(R.string.email_name_invalid), Toast.LENGTH_SHORT).show();
            } else if (content.getText().length() < 1) {
                Toast.makeText(EmailsDetailActivity.this, getString(R.string.email_content_invalid), Toast.LENGTH_SHORT).show();
            } else if (Utilities.haveNetworkConnection(EmailsDetailActivity.this)) {
                JSONObject json = new JSONObject();
                try {
                    json.put(EmailsSQLiteController.columns[1], name.getText());
                    json.put(EmailsSQLiteController.columns[2], from.getText());
                    json.put(EmailsSQLiteController.columns[3], to.getText());
                    json.put(EmailsSQLiteController.columns[4], "date");
                    json.put(EmailsSQLiteController.columns[5], content.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.show();
                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                        WebService.ACTION_EMAILS, WebService.METHOD_POST, json.toString());
            } else {
                Toast.makeText(EmailsDetailActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(emailsReceiver, emailsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(emailsReceiver);
    }

}
