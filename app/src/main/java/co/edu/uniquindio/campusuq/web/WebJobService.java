package co.edu.uniquindio.campusuq.web;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

public class WebJobService extends JobIntentService {

    private static final String TAG = WebJobService.class.getSimpleName();
    boolean isWorking = false;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WebService.class, WebBroadcastReceiver.JOB_ID, work);
    }

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

    @Override
    public boolean onStopCurrentWork() {
        Log.i(TAG, "Job cancelled before being completed");
        return isWorking;
    }

    @Override
    public void onDestroy() {
        Log.i(WebJobService.class.getSimpleName(), "Job destroyed!");
        super.onDestroy();
    }

}
