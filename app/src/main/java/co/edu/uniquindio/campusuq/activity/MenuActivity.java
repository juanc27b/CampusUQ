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
import co.edu.uniquindio.campusuq.util.StarterReceiver;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.users.User;

/**
 * Actividad que muestra el menú de los 4 módulos (Información, Servicios, Estado y Comunicación) en
 * que se agrupan las funcionalidades de la aplicación. Al hacer click en cualquier módulo se
 * abrirá una actividad con la lista de funcionalidades de dicho módulo.
 */
public class MenuActivity extends MainActivity {

    /**
     * Se define un filtro de intentos para el BroadcastReceiver del menú.
     */
    private IntentFilter menuFilter = new IntentFilter(WebService.ACTION_ALL);
    /**
     * Se define un BroadcastReceiver para que el servicio que se encarga de las operaciones web le
     * pueda comunicar a la actividad que ha terminado de cargar/descargar los datos, y se define
     * el callback para saber qué hacer cuando esto ocurre. El BroadcastReceiver del menú se encarga
     * de administrar el diálogo de progreso de la descarga inicial de datos, de manera que
     * actualiza el progreso cada vez que el servicio web le informa que se ha completado una tarea
     * y cierra el diálogo una vez que se ha completado la totalidad de las tareas.
     */
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

    /**
     * Constructor de la actividad del menú que se encarga de establecer que no se utilizará la
     * opción de búsqueda de ítems.
     */
    public MenuActivity() {
        super.setHasSearch(false);
    }

    /**
     * Asigna el fongo de la actividad, infla el diseño del menú, asigna listeners a cada uno de los
     * módulos para abrir las actividades correspondientes, asigna el diálogo de progreso a ser
     * mostrado cuando se deben descargar los datos de la aplicación, pide los permisos necesarios
     * para el correcto funcionamiento de la aplicación (API >= 23) si no los tiene, y por último
     * inicia la descarga de datos si la aplicación no dispone de ellos.
     * @param savedInstanceState Parámetro usado para recuperar estados anteriores de la actividad.
     */
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

    /**
     * Callback para el resultado de la solicitud de permisos, en el que se inicia la descarga de
     * datos de la aplicación si la misma no dispone de ellos en el momento. Si el usuario no da
     * los permisos entonces no podrá visualizar multimedia de la aplicación (imágenes y videos) y
     * tampoco dispondrá de GPS en la funcionalidad del Mapa de la universidad hasta que decida
     * otorgar los permisos.
     * @param requestCode Código de la solicitud.
     * @param permissions Arreglo de permisos que se solicitaron.
     * @param grantResults Resultados de las solicitudes.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        loadContent();
    }

    /**
     * Método que revisa si la aplicación ya cuenta con datos descargados desde el servidor, y de no
     * ser así, se programa con una alarma el inicio del servicio web para la descarga inicial de
     * datos y actualizaciones posteriores en intérvalos de tiempo.
     */
    public void loadContent() {
        WebService.PENDING_ACTION = WebService.ACTION_NONE;
        User user = UsersPresenter.loadUser(getApplicationContext());
        if (user == null) {
            Log.i(MenuActivity.class.getSimpleName(), "Activando alarma");
            if (!progressDialog.isShowing()) progressDialog.show();
            StarterReceiver.cancelAlarm(getApplicationContext());
            StarterReceiver.scheduleAlarm(getApplicationContext(), true);
            WebBroadcastReceiver.startService(getApplicationContext(),
                    WebService.ACTION_ALL, WebService.METHOD_GET, null);
        }
    }

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra el
     * BroadcastReceiver del menú para administrar el díalogo de progreso de la descarga inicial
     * de datos desde el servidor.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(menuReceiver, menuFilter);
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del BroadcastReceiver del menú.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(menuReceiver);
    }

}
