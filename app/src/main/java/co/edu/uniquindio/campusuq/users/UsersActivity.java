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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class UsersActivity extends MainActivity implements EasyPermissions.PermissionCallbacks {

    private EditText name, phone, address, document, password;
    private TextView email, logOut;
    private LinearLayout passwordLayout;
    private Button send;
    private User user;

    private String category;
    private EmailsPresenter emailsPresenter;

    private IntentFilter usersFilter = new IntentFilter(WebService.ACTION_USERS);
    private BroadcastReceiver usersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            User user = intent.getParcelableExtra("USER");
            if (user == null) {
                Toast.makeText(context, context.getString(R.string.registration_wrong), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                finish();
            }
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    };

    public UsersActivity() {
        super.setHasSearch(false);

        emailsPresenter = new EmailsPresenter(this);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_users);
        viewStub.inflate();

        name = findViewById(R.id.user_detail_name);
        email = findViewById(R.id.user_detail_email);
        phone = findViewById(R.id.user_detail_phone);
        address = findViewById(R.id.user_detail_address);
        document = findViewById(R.id.user_detail_document);
        password = findViewById(R.id.user_detail_password);
        passwordLayout = findViewById(R.id.user_detail_password_layout);

        send = findViewById(R.id.user_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() < 3 || name.getText().length() > 50) {
                    Toast.makeText(UsersActivity.this, getString(R.string.user_name_invalid), Toast.LENGTH_SHORT).show();
                } else if (email.getText().length() < 7 || email.getText().length() > 70) {
                    Toast.makeText(UsersActivity.this, getString(R.string.user_email_invalid), Toast.LENGTH_SHORT).show();
                } else if (phone.getText().length() > 50) {
                    Toast.makeText(UsersActivity.this, getString(R.string.user_phone_invalid), Toast.LENGTH_SHORT).show();
                } else if (address.getText().length() > 100) {
                    Toast.makeText(UsersActivity.this, getString(R.string.user_address_invalid), Toast.LENGTH_SHORT).show();
                } else if (document.getText().length() > 50) {
                    Toast.makeText(UsersActivity.this, getString(R.string.user_document_invalid), Toast.LENGTH_SHORT).show();
                } else if (password.getText().length() > 0 && (password.getText().length() < 8 || password.getText().length() > 16)) {
                    Toast.makeText(UsersActivity.this, getString(R.string.user_password_invalid), Toast.LENGTH_SHORT).show();
                } else if (Utilities.haveNetworkConnection(UsersActivity.this)) {
                    JSONObject json = new JSONObject();
                    try {
                        if(user != null) json.put("UPDATE_ID", user.get_ID());
                        json.put(UsersSQLiteController.columns[1], name.getText());
                        json.put(UsersSQLiteController.columns[2], email.getText());
                        json.put(UsersSQLiteController.columns[3], phone.getText());
                        json.put(UsersSQLiteController.columns[4], address.getText());
                        json.put(UsersSQLiteController.columns[5], document.getText());
                        if (password.getText().length() > 0) json.put(UsersSQLiteController.columns[6], password.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.show();
                    WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                            WebService.ACTION_USERS, WebService.METHOD_POST, json.toString());
                } else {
                    Toast.makeText(UsersActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        logOut = findViewById(R.id.user_log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailsPresenter.deleteEmails();
                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                        WebService.ACTION_USERS, WebService.METHOD_DELETE, null);
                finish();
            }
        });

        Intent intent = getIntent();
        category = intent.getStringExtra("CATEGORY");
        setUser(intent);

    }

    @Override
    public void handleIntent(Intent intent) {
        if (category != null) {
            category = intent.getStringExtra("CATEGORY");
            setUser(intent);
        }
    }

    public void setUser(Intent intent) {
        user = intent.getParcelableExtra("USER");

        if (user != null) {
            name.setText(user.getName());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
            address.setText(user.getAddress());
            document.setText(user.getDocument());
        }

        if (getString(R.string.sign_up).equals(category) || getString(R.string.edit_account).equals(category)) {
            passwordLayout.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
            changeConfiguration(true);
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utilities.haveNetworkConnection(UsersActivity.this)) {
                        Toast.makeText(UsersActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    } else if (!emailsPresenter.isGooglePlayServicesAvailable()) {
                        emailsPresenter.acquireGooglePlayServices(UsersActivity.this);
                    } else {
                        emailsPresenter.chooseAccount(UsersActivity.this);
                    }
                }
            });
        } else {
            passwordLayout.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            changeConfiguration(false);
            email.setOnClickListener(null);
        }

        if (getString(R.string.edit_account).equals(category)) {
            logOut.setVisibility(View.VISIBLE);
        } else {
            logOut.setVisibility(View.GONE);
        }
    }

    public void changeConfiguration(boolean edit) {
        name.setClickable(edit);
        name.setCursorVisible(edit);
        name.setFocusable(edit);
        name.setFocusableInTouchMode(edit);
        email.setClickable(edit);
        email.setCursorVisible(edit);
        email.setFocusable(edit);
        email.setFocusableInTouchMode(edit);
        phone.setClickable(edit);
        phone.setCursorVisible(edit);
        phone.setFocusable(edit);
        phone.setFocusableInTouchMode(edit);
        address.setClickable(edit);
        address.setCursorVisible(edit);
        address.setFocusable(edit);
        address.setFocusableInTouchMode(edit);
        document.setClickable(edit);
        document.setCursorVisible(edit);
        document.setFocusable(edit);
        document.setFocusableInTouchMode(edit);
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
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                } else {
                    emailsPresenter.chooseAccount(UsersActivity.this);
                }
                break;
            case EmailsPresenter.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        if (accountName.endsWith("@uqvirtual.edu.co") || accountName.endsWith("@uniquindio.edu.co")) {
                            email.setText(accountName);
                        } else {
                            Toast.makeText(this, getString(R.string.user_account_invalid), Toast.LENGTH_SHORT).show();
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
