package co.edu.uniquindio.campusuq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
public class MenuActivity extends MainActivity implements View.OnClickListener {

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
                progressDialog.setMessage(getString(intent.getIntExtra(Utilities.FEEDBACK,
                        R.string.wait_to_informations)));
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
     * Asigna el fondo de la actividad, infla el diseño del menú, asigna listeners a cada uno de los
     * módulos para abrir las actividades correspondientes, asigna el diálogo de progreso a ser
     * mostrado cuando se deben descargar los datos de la aplicación, y por último inicia la
     * descarga de datos.
     * @param savedInstanceState Parámetro usado para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_background, R.drawable.landscape_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_menu);
        stub.inflate();

        progressDialog = Utilities.getProgressDialog(this, false);
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle(R.string.notifications_text)
                        .setMessage(R.string.notifications_dialog)
                        .setPositiveButton(R.string.dialog_ok, null)
                        .show();
            }
        });

        findViewById(R.id.information_module_layout).setOnClickListener(this);
        findViewById(R.id.services_module_layout).setOnClickListener(this);
        findViewById(R.id.state_module_layout).setOnClickListener(this);
        findViewById(R.id.communication_module_layout).setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.information_module_layout:
                startActivity(new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.information_module));
                break;
            case R.id.services_module_layout:
                startActivity(new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.services_module));
                break;
            case R.id.state_module_layout:
                startActivity(new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.state_module));
                break;
            case R.id.communication_module_layout:
                startActivity(new Intent(this, ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.communication_module));
                break;
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
