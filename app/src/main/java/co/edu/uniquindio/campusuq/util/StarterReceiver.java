package co.edu.uniquindio.campusuq.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;

/**
 * Iniciador de recepciones.
 */
public class StarterReceiver extends BroadcastReceiver {

    /**
     * Funcion que responde a las recepciones.
     * @param context Contexto utilizado para realizar la operacion.
     * @param intent Intetno.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleAlarm(context, false);
    }

    /**
     * Programa una alarma.
     * @param context Contexto utilizado para realizar la operacion.
     * @param firstTime Primer tiempo.
     */
    // Setup a recurring alarm every half hour
    public static void scheduleAlarm(Context context, boolean firstTime) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, WebBroadcastReceiver.class);
        intent.setAction(WebBroadcastReceiver.ACTION_START_WEB_SERVICE);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        if (firstTime) firstMillis += AlarmManager.INTERVAL_HOUR;
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_HOUR, pIntent);
    }

    /**
     * Cancela la alarma.
     * @param context Contexto utilizado para realizar la operacion.
     */
    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, WebBroadcastReceiver.class);
        intent.setAction(WebBroadcastReceiver.ACTION_START_WEB_SERVICE);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

}
