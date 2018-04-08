package co.edu.uniquindio.campusuq.emails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import pub.devrel.easypermissions.EasyPermissions;

public class EmailsDetailActivity extends MainActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {

    private TextView from;
    private EditText to, name, content;

    private IntentFilter emailsFilter = new IntentFilter(WebService.ACTION_EMAILS);
    private BroadcastReceiver emailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(progressDialog.isShowing()) progressDialog.dismiss();
            Intent exceptionIntent = intent.getParcelableExtra("INTENT");
            if (exceptionIntent == null) {
                int inserted = intent.getIntExtra("INSERTED", 0);
                if (inserted == 0) {
                    Toast.makeText(context, R.string.email_failed_sending,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.email_successful_sending,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                startActivityForResult(exceptionIntent, EmailsPresenter.REQUEST_AUTHORIZATION);
            }
        }
    };

    public EmailsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_emails_detail);
        viewStub.inflate();

        from = findViewById(R.id.email_detail_from);
        to = findViewById(R.id.email_detail_to);
        name = findViewById(R.id.email_detail_name);
        content = findViewById(R.id.email_detail_content);

        from.setText(UsersPresenter.loadUser(this).getEmail());

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

    public void sendEmail() {
        if (from.getText().equals("campusuq@uniquindio.edu.co")) {
            Toast.makeText(this, R.string.email_from_invalid,
                    Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(to.getText()) ||
                !Patterns.EMAIL_ADDRESS.matcher(to.getText()).matches()) {
            Toast.makeText(this, R.string.email_to_invalid,
                    Toast.LENGTH_SHORT).show();
        } else if (name.getText().length() < 1) {
            Toast.makeText(this, R.string.email_name_invalid,
                    Toast.LENGTH_SHORT).show();
        } else if (content.getText().length() < 1) {
            Toast.makeText(this, R.string.email_content_invalid,
                    Toast.LENGTH_SHORT).show();
        } else if (Utilities.haveNetworkConnection(this)) {
            JSONObject json = new JSONObject();
            try {
                json.put(EmailsSQLiteController.columns[1], name.getText());
                json.put(EmailsSQLiteController.columns[2], from.getText());
                json.put(EmailsSQLiteController.columns[3], to.getText());
                json.put(EmailsSQLiteController.columns[4], new SimpleDateFormat(
                        "EEE, d MMM yyyy HH:mm:ss Z",
                        new Locale("en", "CO"))
                        .format(Calendar.getInstance().getTime()));
                json.put(EmailsSQLiteController.columns[6], content.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.show();
            WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                    WebService.ACTION_EMAILS, WebService.METHOD_POST, json.toString());
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.email_detail_ok:
                sendEmail();
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

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case EmailsPresenter.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    sendEmail();
                } else {
                    Toast.makeText(this,
                            R.string.email_authorization,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

}
