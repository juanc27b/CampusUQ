package co.edu.uniquindio.campusuq.web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Receptor de transmisiones web.
 */
public class WebBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_START_WEB_SERVICE =
            "co.edu.uniquindio.campusuq.ACTION_START_WEB_SERVICE";
    public static final String ACTION_STOP_WEB_SERVICE =
            "co.edu.uniquindio.campusuq.ACTION_STOP_WEB_SERVICE";

    /**
     * Unique job ID for this service.
     */
    public static final int JOB_ID = 1000;


    /**
     * Responde a recepciones.
     * @param context Contexto con el cual realizar la operacion.
     * @param intent Intento.
     */
    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_START_WEB_SERVICE.equals(intent.getAction())) {
            scheduleJob(context, WebService.ACTION_ALL, WebService.METHOD_GET, null);
        }
    }

    /**
     * Programa el trabajo.
     * @param context Contexto con el cual realizar la operacion.
     * @param action Accion.
     * @param method Metodo.
     * @param object Objeto.
     */
    public static void scheduleJob(Context context, String action, String method, String object) {
        WebJobService.enqueueWork(context, new Intent(context, WebJobService.class)
                .putExtra("ACTION", action)
                .putExtra("METHOD", method)
                .putExtra("OBJECT", object));
        Log.i(WebBroadcastReceiver.class.getSimpleName(), "Job scheduled!");
    }

    /**
     * Inicia el servicio.
     * @param context Contexto con el cual realizar la operacion.
     * @param action Accion.
     * @param method Metodo.
     * @param object Objeto.
     */
    public static void startService(Context context, String action, String method, String object) {
        Intent intent = new Intent(context, WebService.class)
                .putExtra("ACTION", action)
                .putExtra("METHOD", method)
                .putExtra("OBJECT", object);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(intent);
        else context.startService(intent);
        Log.i(WebBroadcastReceiver.class.getSimpleName(), "Service called!");
    }

}
