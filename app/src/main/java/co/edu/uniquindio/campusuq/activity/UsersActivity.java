package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class UsersActivity extends MainActivity {

    private EditText name, email, phone, address, document, password;
    private TextView logOut;
    private LinearLayout passwordLayout;
    private Button send;
    private User user;

    private String category;

    public UsersActivity() {
        super.setHasSearch(false);
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
                        if (password.getText().length() > 0) {
                            json.put(UsersSQLiteController.columns[6], password.getText());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                            WebService.ACTION_USERS, WebService.METHOD_POST, json.toString());
                    finish();
                } else {
                    Toast.makeText(UsersActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        logOut = findViewById(R.id.user_log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            logOut.setVisibility(View.VISIBLE);
        } else {
            logOut.setVisibility(View.GONE);
        }

        if (getString(R.string.sign_up).equals(category) || getString(R.string.edit_account).equals(category)) {
            passwordLayout.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
            changeConfiguration(true);
        } else {
            passwordLayout.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            changeConfiguration(false);
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

}
