package co.edu.uniquindio.campusuq.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.activity.NewsActivity;
import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;

/**
 * Created by Juan Camilo on 21/02/2018.
 */

public class WebService extends JobService {

    public static final String ACTION_ALL = "co.edu.uniquindio.campusuq.ACTION_ALL";
    public static final String ACTION_NEWS = "co.edu.uniquindio.campusuq.ACTION_NEWS";
    public static final String ACTION_EVENTS = "co.edu.uniquindio.campusuq.ACTION_EVENTS";

    private static final int mNotificationId = 2;

    private static final String TAG = WebService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "Job started!");
        isWorking = true;
        // We need 'jobParameters' so we can call 'jobFinished'
        startWorkOnNewThread(params); // Services do NOT run on a separate thread
        return isWorking;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "Job cancelled before being completed.");
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(params, needsReschedule);
        return needsReschedule;
    }

    private void startWorkOnNewThread(final JobParameters jobParameters) {
        new Thread(new Runnable() {
            public void run() {
                doWork(jobParameters);
            }
        }).start();
    }

    private void doWork(JobParameters params) {
        Log.i(TAG, "entrando a doWork");
        String action = params.getExtras().getString("ACTION");
        switch (action) {
            case ACTION_ALL:
                loadNews(ACTION_NEWS);
                loadNews(ACTION_EVENTS);
                break;
            case ACTION_NEWS:
                loadNews(ACTION_NEWS);
                break;
            case ACTION_EVENTS:
                loadNews(ACTION_EVENTS);
                break;
            default:
                break;
        }

        Log.i(TAG, "Job finished!");
        isWorking = false;
        boolean needsReschedule = false;
        jobFinished(params, needsReschedule);
    }

    private Notification buildNotification(String type, Object object) {

        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String nId = getString(R.string.web_notification_id);
            CharSequence nName = getString(R.string.web_notification_name);
            String nDescription = getString(R.string.web_notification_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(nId, nName, importance);
            mChannel.setDescription(nDescription);
            mNotificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), nId);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        switch (type) {
            case ACTION_NEWS:
                New mNew = (New) object;
                builder
                        .setContentTitle(getString(R.string.app_name)+" - "+getString(R.string.news))
                        .setContentText(mNew.getName())
                        .setSubText(mNew.getSummary());
                File file = new File(mNew.getImage());
                if (file.exists()) {
                    builder.setLargeIcon(BitmapFactory.decodeFile(file.getAbsolutePath()));
                } else {
                    builder.setLargeIcon(BitmapFactory.decodeResource(
                            getApplicationContext().getResources(), R.drawable.app_icon));
                }
                break;
            default:
                break;
        }

        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(buildPendingIntent(type))
                .setAutoCancel(true);

        return builder.build();

    }

    private PendingIntent buildPendingIntent(String type) {
        Intent resultIntent = null;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        PendingIntent resultPendingIntent = null;
        switch (type) {
            case ACTION_NEWS:
                resultIntent = new Intent(getApplicationContext(), NewsActivity.class);
                resultIntent.putExtra("CATEGORY", getString(R.string.news));
                stackBuilder.addParentStack(NewsActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.information_module));
                break;
            case ACTION_EVENTS:
                resultIntent = new Intent(getApplicationContext(), NewsActivity.class);
                resultIntent.putExtra("CATEGORY", getString(R.string.events));
                stackBuilder.addParentStack(NewsActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.information_module));
                break;
            default:
                break;
        }
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

    private void loadNews(String type) {

        NewsSQLiteController dbController = new NewsSQLiteController(getApplicationContext(), 1);
        String validRows = null;
        String lastNewId = null;
        boolean notify = true;

        String events = "";
        ArrayList<NewCategory> categories = dbController.selectCategory(null,
                NewsSQLiteController.CAMPOS_CATEGORIA[1] + " = ?", new String[]{"Eventos"});
        ArrayList<NewRelation> relations;
        if (categories.size() > 0) {
            relations = dbController.selectRelation(null,
                    NewsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{categories.get(0).get_ID()});
            for (NewRelation relation : relations) {
                events += relation.getNew_ID() + ",";
            }
            events = events.substring(0, events.length() - 1);
            if (ACTION_NEWS.equals(type)) {
                validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" NOT IN ("+events+")";
                lastNewId = "/no_eventos";
            } else {
                validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" IN ("+events+")";
                lastNewId = "/eventos";
            }

        } else {
            notify = false;
        }

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        int inserted = 0;
        if (MainActivity.haveNetworkConnection(getApplicationContext())) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            categories = NewsServiceController.getNewCategories();
            for (NewCategory category: categories) {
                ArrayList<NewCategory> oldCategories = dbController.selectCategory("1",
                        NewsSQLiteController.CAMPOS_CATEGORIA[0]+" = ?", new String[]{category.get_ID()});
                if (oldCategories.size() == 0) {
                    dbController.insertCategory(category.get_ID(), category.getName(), category.getLink());
                }
            }

            ArrayList<New> lastNews = dbController.select("1", validRows, null);
            if (lastNews.size() > 0) {
                lastNewId += "/"+lastNews.get(0).get_ID();
            }
            ArrayList<New> updated = NewsServiceController.getNews(lastNewId);
            for (New mNew : updated) {
                // If the job has been cancelled, stop working; the job will be rescheduled.
                if (jobCancelled)
                    return;

                String imagePath = Utilities.saveImage(mNew.getImage(), getApplicationContext());
                if (imagePath != null) {
                    mNew.setImage(imagePath);
                }
                ArrayList<New> olds = dbController.select("1",
                        NewsSQLiteController.CAMPOS_TABLA[0]+" = ?", new String[]{mNew.get_ID()});
                if (olds.size() > 0) {
                    dbController.update(mNew.get_ID(), mNew.getName(), mNew.getLink(), mNew.getImage(),
                            mNew.getSummary(), mNew.getContent(), mNew.getDate(), mNew.getAuthor());
                } else {
                    dbController.insert(mNew.get_ID(), mNew.getName(), mNew.getLink(), mNew.getImage(),
                            mNew.getSummary(), mNew.getContent(), mNew.getDate(), mNew.getAuthor());
                    relations = NewsServiceController.getNewRelations(mNew.get_ID());
                    for (NewRelation relation : relations) {
                        dbController.insertRelation(relation.getCategory_ID(), relation.getNew_ID());
                    }
                }
                inserted += 1;
                if (notify) {
                    manager.notify(mNew.getName(), mNotificationId, buildNotification(type, mNew));
                }
            }
        }

        dbController.destroy();

        Intent intent = new Intent(ACTION_NEWS);
        intent.putExtra("INSERTED", inserted);
        sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        Log.i(WebBroadcastReceiver.class.getSimpleName(), "destruyendo el web service");
        super.onDestroy();
    }

}
