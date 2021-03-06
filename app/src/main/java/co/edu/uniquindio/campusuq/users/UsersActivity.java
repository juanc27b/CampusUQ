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

import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.HistoryActivity;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.emails.EmailsPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Actividad que permite visualizar o editar la informacion de una cuenta de usuario.
 */
public class UsersActivity extends MainActivity implements EasyPermissions.PermissionCallbacks {

    private EditText name;
    private TextView email;
    private EditText phone;
    private EditText address;
    private EditText document;
    private LinearLayout passwordLayout;
    private EditText password;
    private EditText password_verify;
    private Button send;
    private TextView logOut;

    private User user;

    private int category;
    private EmailsPresenter emailsPresenter = new EmailsPresenter(this);

    private IntentFilter usersFilter = new IntentFilter(WebService.ACTION_USERS);
    private BroadcastReceiver usersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            User user = intent.getParcelableExtra("USER");

            if (user == null) {
                Toast.makeText(context, R.string.registration_wrong, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.registration_successful,
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
    };

    /**
     * Constructor que oculta el botón de busqueda.
     */
    public UsersActivity() {
        super.setHasSearch(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de usuario en la actividad superior y
     * establece los valores de las variables.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_users);
        viewStub.inflate();

        name = findViewById(R.id.user_detail_name);
        email = findViewById(R.id.user_detail_email);
        phone = findViewById(R.id.user_detail_phone);
        address = findViewById(R.id.user_detail_address);
        document = findViewById(R.id.user_detail_document);
        passwordLayout = findViewById(R.id.user_detail_password_layout);
        password = findViewById(R.id.user_detail_password);
        password_verify = findViewById(R.id.user_detail_password_verify);

        send = findViewById(R.id.user_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() < 3 || name.getText().length() > 50) {
                    Toast.makeText(UsersActivity.this,
                            R.string.user_name_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (email.getText().length() < 7 || email.getText().length() > 70) {
                    Toast.makeText(UsersActivity.this,
                            R.string.user_email_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (phone.getText().length() > 50) {
                    Toast.makeText(UsersActivity.this,
                            R.string.user_phone_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (address.getText().length() > 100) {
                    Toast.makeText(UsersActivity.this,
                            R.string.user_address_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (document.getText().length() > 50) {
                    Toast.makeText(UsersActivity.this,
                            R.string.user_document_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (password.getText().length() > 0 && (password.getText().length() < 8 ||
                        password.getText().length() > 16)) {
                    Toast.makeText(UsersActivity.this,
                            R.string.user_password_invalid,
                            Toast.LENGTH_SHORT).show();
                } else if (!password_verify.getText().toString()
                        .equals(password.getText().toString())) {
                    Toast.makeText(UsersActivity.this,
                            R.string.user_password_verify_invalid,
                            Toast.LENGTH_SHORT).show();
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
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_users_category))
                            .setAction(getString(user == null ?
                                    R.string.analytics_create_action : R.string.analytics_modify_action))
                            .setLabel(getString(R.string.analytics_login_label))
                            .setValue(1)
                            .build());
                    progressDialog.show();
                    emailsPresenter.deleteEmails();
                    WebBroadcastReceiver.startService(getApplicationContext(),
                            WebService.ACTION_USERS, WebService.METHOD_POST, json.toString());
                } else {
                    Toast.makeText(UsersActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        logOut = findViewById(R.id.user_log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailsPresenter.deleteEmails();
                WebBroadcastReceiver.startService(getApplicationContext(),
                        WebService.ACTION_USERS, WebService.METHOD_DELETE, null);
                startActivity(new Intent(UsersActivity.this, HistoryActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.action_delete_history)
                        .putExtra(Utilities.SELECT_ALL, true));
                finish();
            }
        });

        Intent intent = getIntent();
        category = intent.getIntExtra(Utilities.CATEGORY, R.string.app_name);
        setUser(intent);
    }

    /**
     * Método para manejar nuevas llamadas a la actividad, dependiendo de la accion del intento
     * puede cambiar el titulo de la actividad y cambiar su configuración.
     * @param intent Intento que contiene la accion a realizar.
     */
    @Override
    public void handleIntent(Intent intent) {
        if (getSupportActionBar() != null) {
            setIntent(intent);
            category = intent.getIntExtra(Utilities.CATEGORY, R.string.app_name);
            setUser(intent);
        }
    }

    /**
     * Utilizando el intento establece la informacion del usuario y dependiendo de la categoria
     * actual modifica la configuracion llamando la funcion para ello.
     * @param intent Intento que contiene la accion a realizar.
     */
    public void setUser(Intent intent) {
        user = intent.getParcelableExtra("USER");

        if (user != null) {
            name.setText(user.getName());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
            address.setText(user.getAddress());
            document.setText(user.getDocument());
        }

        if (category == R.string.sign_up || category == R.string.edit_account) {
            passwordLayout.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
            changeConfiguration(true);
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utilities.haveNetworkConnection(UsersActivity.this)) {
                        Toast.makeText(UsersActivity.this, R.string.no_internet,
                                Toast.LENGTH_SHORT).show();
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

        if (category == R.string.edit_account) {
            logOut.setVisibility(View.VISIBLE);
        } else {
            logOut.setVisibility(View.GONE);
        }
    }

    /**
     * Cambia la configuracion de la instancia de la actividad dependiendo de si se va solamente a
     * visualizar la informacion del usuario o si tambien se va a dar la posibilidad de editarlo.
     * @param edit Valor booleano que indica si se quiere editar o no el usuario.
     */
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
        phone.setHint(edit ? getString(R.string.user_detail_phone_hint) : null);
        address.setClickable(edit);
        address.setCursorVisible(edit);
        address.setFocusable(edit);
        address.setFocusableInTouchMode(edit);
        address.setHint(edit ? getString(R.string.user_detail_address_hint) : null);
        document.setClickable(edit);
        document.setCursorVisible(edit);
        document.setFocusable(edit);
        document.setFocusableInTouchMode(edit);
        document.setHint(edit ? getString(R.string.user_detail_document_hint) : null);
    }

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra un
     * receptor para estar atento a los intentos relacionados con los usuarios.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(usersReceiver, usersFilter);
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del receptor para los usuarios.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(usersReceiver);
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        name = null;
        email = null;
        phone = null;
        address = null;
        document = null;
        passwordLayout = null;
        password = null;
        password_verify = null;
        send = null;
        logOut = null;
        user = null;
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
