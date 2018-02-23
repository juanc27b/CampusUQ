package co.edu.uniquindio.campusuq.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
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
            scheduleJob(context, WebService.ACTION_ALL);
        }
    }

    public static void scheduleJob(Context context, String action) {
        PersistableBundle extras = new PersistableBundle();
        extras.putString("ACTION", action);
        ComponentName serviceComponent = new ComponentName(context, WebService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceComponent)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setOverrideDeadline(0)
                .setExtras(extras)
                .build();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.i(WebBroadcastReceiver.class.getSimpleName(), "Job scheduled!");
        } else {
            Log.i(WebBroadcastReceiver.class.getSimpleName(), "Job not scheduled");
        }
    }

}
