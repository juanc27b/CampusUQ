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
import co.edu.uniquindio.campusuq.activity.NewsActivity;
import co.edu.uniquindio.campusuq.vo.Contact;
import co.edu.uniquindio.campusuq.vo.ContactCategory;
import co.edu.uniquindio.campusuq.vo.Information;
import co.edu.uniquindio.campusuq.vo.InformationCategory;
import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;
import co.edu.uniquindio.campusuq.vo.Program;
import co.edu.uniquindio.campusuq.vo.ProgramCategory;
import co.edu.uniquindio.campusuq.vo.ProgramFaculty;

/**
 * Created by Juan Camilo on 21/02/2018.
 */

public class WebService extends JobService {

    public static final String ACTION_NONE = "co.edu.uniquindio.campusuq.ACTION_NONE";
    public static final String ACTION_ALL = "co.edu.uniquindio.campusuq.ACTION_ALL";
    public static final String ACTION_NEWS = "co.edu.uniquindio.campusuq.ACTION_NEWS";
    public static final String ACTION_EVENTS = "co.edu.uniquindio.campusuq.ACTION_EVENTS";
    public static final String ACTION_SYMBOLS = "co.edu.uniquindio.campusuq.ACTION_SYMBOLS";
    public static final String ACTION_WELFARE = "co.edu.uniquindio.campusuq.ACTION_WELFARE";
    public static final String ACTION_CONTACTS = "co.edu.uniquindio.campusuq.ACTION_CONTACTS";
    public static final String ACTION_PROGRAMS = "co.edu.uniquindio.campusuq.ACTION_PROGRAMS";

    public static String PENDING_ACTION = ACTION_NONE;

    private static final int mNotificationId = 1;

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
                loadInformations();
                loadContacts();
                loadPrograms();
                loadNews(ACTION_EVENTS);
                loadNews(ACTION_NEWS);
                break;
            case ACTION_EVENTS:
                loadNews(ACTION_EVENTS);
                break;
            case ACTION_NEWS:
                loadNews(ACTION_NEWS);
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
        if (Utilities.haveNetworkConnection(getApplicationContext())) {
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
                if (notify && inserted <= 5) {
                    manager.notify(mNew.getName(), mNotificationId, buildNotification(type, mNew));
                }
            }
        }

        dbController.destroy();

        Intent intent = new Intent(ACTION_NEWS);
        intent.putExtra("INSERTED", inserted);
        sendBroadcast(intent);

    }

    private void loadInformations() {

        InformationsSQLiteController dbController = new InformationsSQLiteController(getApplicationContext(), 1);

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            boolean updateInformations = false;
            ArrayList<InformationCategory> updatedCategories = InformationsServiceController.getInformationCategories();
            for (InformationCategory category : updatedCategories) {
                // If the job has been cancelled, stop working; the job will be rescheduled.
                if (jobCancelled)
                    return;

                ArrayList<InformationCategory> oldCategories = dbController.selectCategory(
                        InformationsSQLiteController.CAMPOS_CATEGORIA[0] + " = ?", new String[]{category.get_ID()});
                if (oldCategories.size() == 0) {
                    updateInformations = true;
                    dbController.insertCategory(category.get_ID(), category.getName(), category.getLink(), category.getDate());
                } else if (oldCategories.get(0).getDate().compareTo(category.getDate()) < 0) {
                    updateInformations = true;
                    dbController.updateCategory(category.get_ID(), category.getName(), category.getLink(), category.getDate());
                }
                if (updateInformations){
                    ArrayList<Information> updatedInformations = InformationsServiceController.getInformations(category.get_ID());
                    for (Information information : updatedInformations) {
                        ArrayList<Information> olds = dbController.select(
                                InformationsSQLiteController.CAMPOS_TABLA[0]+" = ?", new String[]{information.get_ID()});
                        if (olds.size() > 0) {
                            dbController.update(information.get_ID(), information.getCategory_ID(),
                                    information.getName(), information.getContent());
                        } else {
                            dbController.insert(information.get_ID(), information.getCategory_ID(),
                                    information.getName(), information.getContent());
                        }
                    }
                }
            }
        }

        dbController.destroy();

        Intent intent = new Intent(PENDING_ACTION);
        sendBroadcast(intent);

    }

    private void loadContacts() {

        ContactsSQLiteController dbController = new ContactsSQLiteController(getApplicationContext(), 1);

        ArrayList<ContactCategory> oldCategories = dbController.selectCategory(null, null);
        if (oldCategories.size() == 0 && Utilities.haveNetworkConnection(getApplicationContext())) {

            ArrayList<ContactCategory> updatedCategories = ContactsServiceController.getContactCategories();
            for (ContactCategory category : updatedCategories) {
                dbController.insertCategory(category.get_ID(), category.getName(), category.getLink());
            }

            ArrayList<Contact> updatedContacts = ContactsServiceController.getContacts();
            for (Contact contact : updatedContacts) {
                dbController.insert(contact.get_ID(), contact.getCategory_ID(),
                        contact.getName(), contact.getAddress(), contact.getPhone(),
                        contact.getEmail(), contact.getCharge(), contact.getAdditionalInformation());
            }

        }

        dbController.destroy();

        Intent intent = new Intent(PENDING_ACTION);
        sendBroadcast(intent);

    }

    private void loadPrograms() {

        ProgramsSQLiteController dbController = new ProgramsSQLiteController(getApplicationContext(), 1);

        ArrayList<ProgramCategory> oldCategories = dbController.selectCategory(null, null);
        if (oldCategories.size() == 0 && Utilities.haveNetworkConnection(getApplicationContext())) {

            ArrayList<ProgramCategory> updatedCategories = ProgramsServiceController.getProgramCategories();
            for (ProgramCategory category : updatedCategories) {
                dbController.insertCategory(category.get_ID(), category.getName());
            }

            ArrayList<ProgramFaculty> updatedFaculties = ProgramsServiceController.getProgramFaculties();
            for (ProgramFaculty faculty : updatedFaculties) {
                dbController.insertFaculty(faculty.get_ID(), faculty.getName());
            }

        }

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            ArrayList<Program> updatedPrograms = ProgramsServiceController.getPrograms();
            for (Program program : updatedPrograms) {
                // If the job has been cancelled, stop working; the job will be rescheduled.
                if (jobCancelled)
                    return;

                ArrayList<Program> olds = dbController.select(
                        ProgramsSQLiteController.CAMPOS_TABLA[0]+" = ?", new String[]{program.get_ID()});
                if (olds.size() == 0) {
                    dbController.insert(program.get_ID(), program.getCategory_ID(), program.getFaculty_ID(),
                            program.getName(), program.getHistory(), program.getHistoryLink(), program.getHistoryDate(),
                            program.getMissionVision(), program.getMissionVisionLink(), program.getMissionVisionDate(),
                            program.getCurriculum(), program.getCurriculumLink(), program.getCurriculumDate(),
                            program.getProfiles(), program.getProfilesLink(), program.getProfilesDate(), program.getContact());
                } else if (olds.get(0).getHistoryDate().compareTo(program.getHistoryDate()) < 0) {
                    dbController.update(4, program.getHistory(),
                            program.getHistoryLink(), program.getHistoryDate());
                } else if (olds.get(0).getMissionVisionDate().compareTo(program.getMissionVisionDate()) < 0) {
                    dbController.update(7, program.getMissionVision(),
                            program.getMissionVisionLink(), program.getMissionVisionDate());
                } else if (olds.get(0).getCurriculumDate().compareTo(program.getCurriculumDate()) < 0) {
                    dbController.update(10, program.getCurriculum(),
                            program.getCurriculumLink(), program.getCurriculumDate());
                } else if (olds.get(0).getProfilesDate().compareTo(program.getProfilesDate()) < 0) {
                    dbController.update(13, program.getProfiles(),
                            program.getProfilesLink(), program.getProfilesDate());
                }
            }
        }

        dbController.destroy();

        Intent intent = new Intent(PENDING_ACTION);
        sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        Log.i(WebBroadcastReceiver.class.getSimpleName(), "destruyendo el web service");
        super.onDestroy();
    }

}
