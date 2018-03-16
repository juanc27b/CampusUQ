package co.edu.uniquindio.campusuq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.UsersSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.User;

public class LoginActivity extends MainActivity {

    private TextView title, signUp;
    private EditText email, password;
    private Button logIn;

    private String category;

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

        logIn = findViewById(R.id.user_log_in);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.haveNetworkConnection(LoginActivity.this)) {
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
        setTitle();

    }

    @Override
    public void handleIntent(Intent intent) {
        if (category != null) {
            category = intent.getStringExtra("CATEGORY");
            setTitle();
        }
    }

    public void setTitle() {
        if (getString(R.string.log_in).equals(category)) {
            title.setText(R.string.login_campusuq);
        } else {
            title.setText(R.string.login_institutional_mail);
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

}
