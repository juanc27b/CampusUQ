package co.edu.uniquindio.campusuq.web;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

/**
 * Servicio de trabajo web.
 */
public class WebJobService extends JobIntentService {

    private static final String TAG = WebJobService.class.getSimpleName();
    boolean isWorking = false;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WebService.class, WebBroadcastReceiver.JOB_ID, work);
    }

    /**
     * Responde a la manipulacion del trabajo.
     * @param intent Intento.
     */
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "Job started!");
        isWorking = true;

        WebBroadcastReceiver.startService(getApplicationContext(),
                intent.getStringExtra("ACTION"),
                intent.getStringExtra("METHOD"),
                intent.getStringExtra("OBJECT"));

        Log.i(TAG, "Job finished!");
        isWorking = false;
    }

    /**
     * Responde a la detencion del trabajo actual.
     * @return Valor que indica si se esta trabajando.
     */
    @Override
    public boolean onStopCurrentWork() {
        Log.i(TAG, "Job cancelled before being completed");
        return isWorking;
    }

    /**
     * Responde a la destruccion del trabajo.
     */
    @Override
    public void onDestroy() {
        Log.i(WebJobService.class.getSimpleName(), "Job destroyed!");
        super.onDestroy();
    }

}
