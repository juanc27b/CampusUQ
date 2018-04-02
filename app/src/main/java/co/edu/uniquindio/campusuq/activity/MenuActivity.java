package co.edu.uniquindio.campusuq.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.items.ItemsActivity;
import co.edu.uniquindio.campusuq.web.StarterReceiver;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.users.User;

public class MenuActivity extends MainActivity {

    private IntentFilter menuFilter = new IntentFilter(WebService.ACTION_ALL);
    private BroadcastReceiver menuReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (progressDialog.isShowing()) {
                int progress = intent.getIntExtra("PROGRESS", 0);
                progressDialog.setProgress(progress);
                if (progress == 12) progressDialog.dismiss();
            }
        }
    };

    public MenuActivity() {
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_background, R.drawable.landscape_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_menu);
        stub.inflate();

        findViewById(R.id.information_module_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.information_module));
                startActivity(intent);
            }
        });
        findViewById(R.id.services_module_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.services_module));
                startActivity(intent);
            }
        });
        findViewById(R.id.state_module_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.state_module));
                startActivity(intent);
            }
        });
        findViewById(R.id.communication_module_layout)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.communication_module));
                startActivity(intent);
            }
        });

        progressDialog = Utilities.getProgressDialog(this, false);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        else loadContent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        loadContent();
    }

    public void loadContent() {
        WebService.PENDING_ACTION = WebService.ACTION_NONE;
        User user = UsersPresenter.loadUser(getApplicationContext());
        if (user == null) {
            Log.i(MenuActivity.class.getSimpleName(), "Activando alarma");
            if (!progressDialog.isShowing()) progressDialog.show();
            StarterReceiver.cancelAlarm(getApplicationContext());
            StarterReceiver.scheduleAlarm(getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(menuReceiver, menuFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(menuReceiver);
    }

}
