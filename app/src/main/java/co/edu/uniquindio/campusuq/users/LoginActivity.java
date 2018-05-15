package co.edu.uniquindio.campusuq.users;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.emails.EmailsPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends MainActivity implements EasyPermissions.PermissionCallbacks {

    private TextView title;
    private TextView email;
    private EditText password;

    private int category;
    private EmailsPresenter emailsPresenter;

    private IntentFilter usersFilter = new IntentFilter(WebService.ACTION_USERS);
    private BroadcastReceiver usersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            User user = intent.getParcelableExtra("USER");

            if (user == null) {
                Toast.makeText(context, R.string.login_wrong,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.login_successful, Toast.LENGTH_SHORT).show();
                finish();
            }

            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
    };

    public LoginActivity() {
        super.setHasSearch(false);

        emailsPresenter = new EmailsPresenter(this);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_login);
        viewStub.inflate();

        title = findViewById(R.id.login_title);

        email = findViewById(R.id.user_email);
        password = findViewById(R.id.user_password);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utilities.haveNetworkConnection(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                } else if (!emailsPresenter.isGooglePlayServicesAvailable()) {
                    emailsPresenter.acquireGooglePlayServices(LoginActivity.this);
                } else {
                    emailsPresenter.chooseAccount(LoginActivity.this);
                }
            }
        });

        findViewById(R.id.user_log_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().length() < 7 || email.getText().length() > 70) {
                    Toast.makeText(LoginActivity.this,
                            R.string.user_email_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (password.getText().length() < 8 || password.getText().length() > 16) {
                    Toast.makeText(LoginActivity.this,
                            R.string.user_password_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (Utilities.haveNetworkConnection(LoginActivity.this)) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put(UsersSQLiteController.columns[2], email.getText());
                        json.put(UsersSQLiteController.columns[6], password.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.show();
                    WebBroadcastReceiver.startService(getApplicationContext(),
                            WebService.ACTION_USERS, WebService.METHOD_GET, json.toString());
                } else {
                    Toast.makeText(LoginActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.user_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UsersActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.sign_up));
                finish();
            }
        });

        category = getIntent().getIntExtra(Utilities.CATEGORY, R.string.app_name);
        if (category == R.string.log_in) title.setText(R.string.login_campusuq);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (getSupportActionBar() != null) {
            setIntent(intent);
            category = intent.getIntExtra(Utilities.CATEGORY, R.string.app_name);
            if (category == R.string.log_in) title.setText(R.string.login_campusuq);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(usersReceiver, usersFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(usersReceiver);
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
            case EmailsPresenter.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this,
                            R.string.google_play_error,
                            Toast.LENGTH_SHORT).show();
                } else {
                    emailsPresenter.chooseAccount(this);
                }
                break;
            case EmailsPresenter.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        if (accountName.endsWith("@uqvirtual.edu.co") ||
                                accountName.endsWith("@uniquindio.edu.co")) {
                            email.setText(accountName);
                        } else {
                            Toast.makeText(this, R.string.user_account_invalid,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
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
