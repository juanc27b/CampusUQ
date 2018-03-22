package co.edu.uniquindio.campusuq.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.EmailsPresenter;
import co.edu.uniquindio.campusuq.util.UsersSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.User;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends MainActivity implements EasyPermissions.PermissionCallbacks {

    private TextView title, email, signUp;
    private EditText password;
    private Button logIn;

    private String category;
    private EmailsPresenter emailsPresenter;

    private IntentFilter usersFilter = new IntentFilter(WebService.ACTION_USERS);
    private BroadcastReceiver usersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            User user = intent.getParcelableExtra("USER");
            if (user == null) {
                Toast.makeText(context, context.getString(R.string.login_wrong), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                finish();
            }
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    };

    public LoginActivity() {
        super.setHasSearch(false);

        emailsPresenter = new EmailsPresenter(this);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

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
                    Toast.makeText(LoginActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if (!emailsPresenter.isGooglePlayServicesAvailable()) {
                    emailsPresenter.acquireGooglePlayServices(LoginActivity.this);
                } else {
                    emailsPresenter.chooseAccount(LoginActivity.this);
                }
            }
        });

        logIn = findViewById(R.id.user_log_in);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().length() < 7 || email.getText().length() > 70) {
                    Toast.makeText(LoginActivity.this, getString(R.string.user_email_invalid), Toast.LENGTH_SHORT).show();
                } else if (password.getText().length() < 8 || password.getText().length() > 16) {
                    Toast.makeText(LoginActivity.this, getString(R.string.user_password_invalid), Toast.LENGTH_SHORT).show();
                } else if (Utilities.haveNetworkConnection(LoginActivity.this)) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put(UsersSQLiteController.columns[2], email.getText());
                        json.put(UsersSQLiteController.columns[6], password.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.show();
                    WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                            WebService.ACTION_USERS, WebService.METHOD_GET, json.toString());
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUp = findViewById(R.id.user_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.sign_up));
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        category = intent.getStringExtra("CATEGORY");
        if (getString(R.string.log_in).equals(category)) {
            title.setText(R.string.login_campusuq);
        }

    }

    @Override
    public void handleIntent(Intent intent) {
        if (category != null) {
            category = intent.getStringExtra("CATEGORY");
            if (getString(R.string.log_in).equals(category)) {
                title.setText(R.string.login_campusuq);
            }
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
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                } else {
                    emailsPresenter.chooseAccount(LoginActivity.this);
                }
                break;
            case EmailsPresenter.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        if (accountName.endsWith("@uqvirtual.edu.co") || accountName.endsWith("@uniquindio.edu.co")) {
                            email.setText(accountName);
                            new MakeRequestTask(emailsPresenter.getCredential().setSelectedAccountName(accountName)).execute();
                        } else {
                            Toast.makeText(this, getString(R.string.user_account_invalid), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case EmailsPresenter.REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    email.setText("");
                    Toast.makeText(this, "Authorization is required for get emails", Toast.LENGTH_SHORT).show();
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

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {

        private GoogleAccountCredential credential;
        private Gmail mService = null;
        private Exception mLastError = null;
        private ProgressDialog pDialog;

        MakeRequestTask(GoogleAccountCredential credential) {
            pDialog = Utilities.getProgressDialog(LoginActivity.this, false);
            this.credential = credential;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build();
        }

        /**
         * Background task to call Gmail API.
         * @param voids no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        /**
         * Fetch a list of Gmail labels attached to the specified account.
         * @return List of Strings labels.
         * @throws IOException
         */
        private void getDataFromApi() throws IOException {
            // Get the labels in the user's account.
            String user = credential.getSelectedAccountName();
            ListMessagesResponse response = mService.users().messages().list(user).execute();
        }


        @Override
        protected void onPreExecute() {
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    email.setText("");
                    emailsPresenter.showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode(), LoginActivity.this);
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            EmailsPresenter.REQUEST_AUTHORIZATION);
                } else {
                    email.setText("");
                    Toast.makeText(LoginActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                email.setText("");
                Toast.makeText(LoginActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
