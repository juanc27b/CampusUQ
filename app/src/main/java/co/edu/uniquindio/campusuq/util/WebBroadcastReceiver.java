package co.edu.uniquindio.campusuq.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Juan Camilo on 21/02/2018.
 */

public class WebBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_START_WEB_SERVICE = "co.edu.uniquindio.campusuq.ACTION_START_WEB_SERVICE";
    public static final String ACTION_STOP_WEB_SERVICE = "co.edu.uniquindio.campusuq.ACTION_STOP_WEB_SERVICE";

    /**
     * Unique job ID for this service.
     */
    public static final int JOB_ID = 1000;


    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_START_WEB_SERVICE.equals(intent.getAction())) {
            scheduleJob(context, WebService.ACTION_ALL, WebService.METHOD_GET, null);
        }
    }

    public static void scheduleJob(Context context, String action, String method, String object) {
        Intent intent = new Intent(context, WebService.class);
        intent.putExtra("ACTION", action);
        intent.putExtra("METHOD", method);
        intent.putExtra("OBJECT", object);
        WebService.enqueueWork(context, intent);
        Log.i(WebBroadcastReceiver.class.getSimpleName(), "Job scheduled!");
    }

}
