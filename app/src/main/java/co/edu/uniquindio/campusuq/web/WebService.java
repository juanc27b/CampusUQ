package co.edu.uniquindio.campusuq.web;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsActivity;
import co.edu.uniquindio.campusuq.contacts.ContactsSQLiteController;
import co.edu.uniquindio.campusuq.contacts.ContactsServiceController;
import co.edu.uniquindio.campusuq.emails.EmailsActivity;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsSQLiteController;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsServiceController;
import co.edu.uniquindio.campusuq.dishes.DishesSQLiteController;
import co.edu.uniquindio.campusuq.dishes.DishesServiceController;
import co.edu.uniquindio.campusuq.emails.EmailsSQLiteController;
import co.edu.uniquindio.campusuq.emails.EmailsServiceController;
import co.edu.uniquindio.campusuq.events.CalendarActivity;
import co.edu.uniquindio.campusuq.events.CalendarDetailActivity;
import co.edu.uniquindio.campusuq.events.EventsSQLiteController;
import co.edu.uniquindio.campusuq.events.EventsServiceController;
import co.edu.uniquindio.campusuq.informations.InformationsSQLiteController;
import co.edu.uniquindio.campusuq.informations.InformationsServiceController;
import co.edu.uniquindio.campusuq.items.Item;
import co.edu.uniquindio.campusuq.items.ItemsActivity;
import co.edu.uniquindio.campusuq.items.ItemsPresenter;
import co.edu.uniquindio.campusuq.news.NewsActivity;
import co.edu.uniquindio.campusuq.notifications.NotificationsPresenter;
import co.edu.uniquindio.campusuq.objects.ObjectsActivity;
import co.edu.uniquindio.campusuq.news.NewsSQLiteController;
import co.edu.uniquindio.campusuq.news.NewsServiceController;
import co.edu.uniquindio.campusuq.announcements.Announcement;
import co.edu.uniquindio.campusuq.announcements.AnnouncementLink;
import co.edu.uniquindio.campusuq.objects.ObjectsSQLiteController;
import co.edu.uniquindio.campusuq.objects.ObjectsServiceController;
import co.edu.uniquindio.campusuq.programs.ProgramsSQLiteController;
import co.edu.uniquindio.campusuq.programs.ProgramsServiceController;
import co.edu.uniquindio.campusuq.quotas.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.quotas.QuotasServiceController;
import co.edu.uniquindio.campusuq.contacts.Contact;
import co.edu.uniquindio.campusuq.contacts.ContactCategory;
import co.edu.uniquindio.campusuq.dishes.Dish;
import co.edu.uniquindio.campusuq.emails.Email;
import co.edu.uniquindio.campusuq.events.Event;
import co.edu.uniquindio.campusuq.events.EventCategory;
import co.edu.uniquindio.campusuq.events.EventDate;
import co.edu.uniquindio.campusuq.events.EventPeriod;
import co.edu.uniquindio.campusuq.events.EventRelation;
import co.edu.uniquindio.campusuq.informations.Information;
import co.edu.uniquindio.campusuq.informations.InformationCategory;
import co.edu.uniquindio.campusuq.objects.LostObject;
import co.edu.uniquindio.campusuq.news.New;
import co.edu.uniquindio.campusuq.news.NewCategory;
import co.edu.uniquindio.campusuq.news.NewRelation;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.users.UsersSQLiteController;
import co.edu.uniquindio.campusuq.users.UsersServiceController;
import co.edu.uniquindio.campusuq.programs.Program;
import co.edu.uniquindio.campusuq.programs.ProgramCategory;
import co.edu.uniquindio.campusuq.programs.ProgramFaculty;
import co.edu.uniquindio.campusuq.quotas.Quota;
import co.edu.uniquindio.campusuq.users.User;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 21/02/2018.
 */

public class WebService extends JobIntentService {

    public static final String ACTION_NONE        = "co.edu.uniquindio.campusuq.ACTION_NONE";
    public static final String ACTION_ALL         = "co.edu.uniquindio.campusuq.ACTION_ALL";
    public static final String ACTION_NEWS        = "co.edu.uniquindio.campusuq.ACTION_NEWS";
    public static final String ACTION_EVENTS      = "co.edu.uniquindio.campusuq.ACTION_EVENTS";
    public static final String ACTION_SYMBOLS     = "co.edu.uniquindio.campusuq.ACTION_SYMBOLS";
    public static final String ACTION_WELFARE     = "co.edu.uniquindio.campusuq.ACTION_WELFARE";
    public static final String ACTION_CONTACTS    = "co.edu.uniquindio.campusuq.ACTION_CONTACTS";
    public static final String ACTION_PROGRAMS    = "co.edu.uniquindio.campusuq.ACTION_PROGRAMS";
    public static final String ACTION_CALENDAR    = "co.edu.uniquindio.campusuq.ACTION_CALENDAR";
    public static final String ACTION_INCIDENTS   = "co.edu.uniquindio.campusuq.ACTION_INCIDENTS";
    public static final String ACTION_COMMUNIQUES = "co.edu.uniquindio.campusuq.ACTION_COMMUNIQUES";
    public static final String ACTION_OBJECTS     = "co.edu.uniquindio.campusuq.ACTION_OBJECTS";
    public static final String ACTION_DISHES      = "co.edu.uniquindio.campusuq.ACTION_DISHES";
    public static final String ACTION_QUOTAS      = "co.edu.uniquindio.campusuq.ACTION_QUOTAS";
    public static final String ACTION_EMAILS      = "co.edu.uniquindio.campusuq.ACTION_EMAILS";
    public static final String ACTION_USERS       = "co.edu.uniquindio.campusuq.ACTION_USERS";

    public static final String METHOD_GET    = "co.edu.uniquindio.campusuq.METHOD_GET";
    public static final String METHOD_POST   = "co.edu.uniquindio.campusuq.METHOD_POST";
    public static final String METHOD_PUT    = "co.edu.uniquindio.campusuq.METHOD_PUT";
    public static final String METHOD_DELETE = "co.edu.uniquindio.campusuq.METHOD_DELETE";

    public static String PENDING_ACTION = ACTION_NONE;

    public static final String[] NOTIFICATIONS = {"Events", "News", "Academic Calendar",
            "Lost Objects", "Security System", "Billboard Information", "Institutional Mail"};

    private static final int mNotificationId = 1;

    private static final String TAG = WebService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;

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
        jobCancelled = false;
        try {
            User user = UsersPresenter.loadUser(getApplicationContext());
            ArrayList<co.edu.uniquindio.campusuq.notifications.Notification> notifications =
                    NotificationsPresenter.loadNotifications(getApplicationContext());
            if (Utilities.haveNetworkConnection(getApplicationContext())) {
                if (user == null) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put(UsersSQLiteController.columns[2],
                                "campusuq@uniquindio.edu.co");
                        json.put(UsersSQLiteController.columns[6], "campusuq");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadUsers(METHOD_GET, json.toString());
                }
                if (notifications.size() < NOTIFICATIONS.length) {
                    NotificationsPresenter.insertNotifications(getApplicationContext());
                }
                doWork(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in Job");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.i(TAG, "Job cancelled before being completed.");
        jobCancelled = true;
        return isWorking;
    }

    private void doWork(Intent intent) {
        Log.i(TAG, "entrando a doWork");
        String action = intent.getStringExtra("ACTION");
        String method = intent.getStringExtra("METHOD");
        String object = intent.getStringExtra("OBJECT");
        switch (action) {
            case ACTION_ALL:
                int progress = 0;
                Intent menuIntent = new Intent(ACTION_ALL);
                loadInformations();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadContacts();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadPrograms();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadCalendar();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadNews(ACTION_EVENTS);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadNews(ACTION_NEWS);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadAnnouncements(ACTION_INCIDENTS, method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadAnnouncements(ACTION_COMMUNIQUES, method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadObjects(method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadDishes(method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadQuotas(method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                loadEmails(method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress));
                break;
            case ACTION_EVENTS:
                loadNews(ACTION_EVENTS);
                break;
            case ACTION_NEWS:
                loadNews(ACTION_NEWS);
                break;
            case ACTION_INCIDENTS:
                loadAnnouncements(ACTION_INCIDENTS, method, object);
                break;
            case ACTION_COMMUNIQUES:
                loadAnnouncements(ACTION_COMMUNIQUES, method, object);
                break;
            case ACTION_OBJECTS:
                loadObjects(method, object);
                break;
            case ACTION_DISHES:
                loadDishes(method, object);
                break;
            case ACTION_QUOTAS:
                loadQuotas(method, object);
                break;
            case ACTION_EMAILS:
                loadEmails(method, object);
                break;
            case ACTION_USERS:
                loadUsers(method, object);
                break;
            default:
                break;
        }

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;

        Log.i(TAG, "Job finished!");
        isWorking = false;
    }

    private Notification buildNotification(String type, Object object) {
        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

        File file = null;
        switch (type) {
            case ACTION_NEWS:
            case ACTION_EVENTS:
                New mNew = (New) object;
                builder
                        .setContentTitle(getString(R.string.app_name)+" - "+
                                (type.equals(ACTION_NEWS) ? getString(R.string.news) : getString(R.string.events)))
                        .setContentText(mNew.getName())
                        .setSubText(mNew.getSummary());
                file = new File(mNew.getImage());
                break;
            case ACTION_OBJECTS:
                LostObject lostObject = (LostObject) object;
                builder
                        .setContentTitle(getString(R.string.app_name)+" - "+getString(R.string.lost_objects))
                        .setContentText(lostObject.getName())
                        .setSubText(lostObject.getDescription());
                // Se concatena una cadena vacia para evitar el caso File(null)
                file = new File(""+lostObject.getImage());
                break;
            case ACTION_CALENDAR:
                Event event = (Event) object;
                builder.setContentTitle(getString(R.string.app_name)+" - "+
                        getString(R.string.academic_calendar))
                        .setContentText(event.getName());
                break;
            case ACTION_INCIDENTS:
            case ACTION_COMMUNIQUES:
                Announcement announcement = (Announcement) object;
                builder
                        .setContentTitle(getString(R.string.app_name)+" - "+
                                (type.equals(ACTION_INCIDENTS) ?
                                        getString(R.string.security_system) : getString(R.string.billboard_information)))
                        .setContentText(announcement.getName())
                        .setSubText(announcement.getDescription());
                AnnouncementsSQLiteController dbController =
                        new AnnouncementsSQLiteController(getApplicationContext(), 1);
                ArrayList<AnnouncementLink> links = dbController.selectLink(
                        AnnouncementsSQLiteController.linkColumns[1]+" = ?",
                        announcement.get_ID());
                dbController.destroy();
                file = links.size() > 0 ? new File(links.get(0).getLink()) : new File("");
                break;
            case ACTION_EMAILS:
                Email email = (Email) object;
                builder
                        .setContentTitle(getString(R.string.app_name)+" - "+getString(R.string.institutional_mail))
                        .setContentText(email.getName())
                        .setSubText(email.getSnippet());
                file = new File("");
                break;
            default:
                break;
        }

        if (file != null && file.exists()) {
            builder.setLargeIcon(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    getApplicationContext().getResources(), R.drawable.app_icon));
        }

        return builder.setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(buildPendingIntent(type)).setAutoCancel(true).build();
    }

    private PendingIntent buildPendingIntent(String type) {
        Intent resultIntent = null;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        switch (type) {
            case ACTION_NEWS:
            case ACTION_EVENTS:
                resultIntent = new Intent(getApplicationContext(), NewsActivity.class);
                resultIntent.putExtra("CATEGORY",
                        (type.equals(ACTION_NEWS) ? getString(R.string.news) : getString(R.string.events)));
                stackBuilder.addParentStack(NewsActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.information_module));
                break;
            case ACTION_OBJECTS:
                resultIntent = new Intent(getApplicationContext(), ObjectsActivity.class);
                resultIntent.putExtra("CATEGORY", getString(R.string.lost_objects));
                stackBuilder.addParentStack(ObjectsActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.services_module));
                break;
            case ACTION_CALENDAR:
                resultIntent = new Intent(getApplicationContext(), ItemsActivity.class);
                resultIntent.putExtra("CATEGORY", getString(R.string.academic_calendar));
                resultIntent.putParcelableArrayListExtra("ITEMS",
                        ItemsPresenter.getEventCategories(getApplicationContext()));
                stackBuilder.addParentStack(CalendarActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                break;
            case ACTION_INCIDENTS:
                resultIntent = new Intent(getApplicationContext(), AnnouncementsActivity.class);
                resultIntent.putExtra("CATEGORY", getString(R.string.security_system));
                stackBuilder.addParentStack(AnnouncementsActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.services_module));
                break;
            case ACTION_COMMUNIQUES:
                resultIntent = new Intent(getApplicationContext(), AnnouncementsActivity.class);
                resultIntent.putExtra("CATEGORY", getString(R.string.billboard_information));
                stackBuilder.addParentStack(AnnouncementsActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.state_module));
                break;
            case ACTION_EMAILS:
                resultIntent = new Intent(getApplicationContext(), EmailsActivity.class);
                resultIntent.putExtra("CATEGORY", getString(R.string.institutional_mail));
                stackBuilder.addParentStack(EmailsActivity.class);
                stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
                stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.communication_module));
                break;
            default:
                break;
        }
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void loadNews(String type) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        Context context = getApplicationContext();

        NewsSQLiteController dbController = new NewsSQLiteController(context, 1);
        String validRows = null;
        String category_date = "";
        boolean notify = ACTION_EVENTS.equals(type) ?
                NotificationsPresenter.getNotification(context, "0").getActivated().equals("S") :
                NotificationsPresenter.getNotification(context, "1").getActivated().equals("S");

        String events = "";
        ArrayList<NewCategory> categories = dbController.selectCategory(null,
                NewsSQLiteController.categoryColumns[1] + " = ?", "Eventos");

        if (!categories.isEmpty()) {
            for (NewRelation relation : dbController.selectRelation(
                    NewsSQLiteController.relationColumns[0]+" = ?",
                    categories.get(0).get_ID())) {
                events += relation.getNew_ID() + ",";
            }

            events = events.substring(0, events.length() - 1);

            if (ACTION_NEWS.equals(type)) {
                validRows = NewsSQLiteController.columns[0]+" NOT IN ("+events+")";
                category_date += "/no_eventos";
            } else {
                validRows = NewsSQLiteController.columns[0]+" IN ("+events+")";
                category_date += "/eventos";
            }

        } else {
            notify = false;
        }

        int inserted = 0;

        if (Utilities.haveNetworkConnection(context)) {
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            categories = NewsServiceController.getNewCategories(context);

            for (NewCategory category: categories) {
                ArrayList<NewCategory> oldCategories = dbController.selectCategory("1",
                        NewsSQLiteController.categoryColumns[0]+" = ?", category.get_ID());

                if (oldCategories.isEmpty()) {
                    dbController.insertCategory(category.get_ID(), category.getName(),
                            category.getLink());
                }
            }

            ArrayList<String> _IDs = new ArrayList<>();
            ArrayList<String> images = new ArrayList<>();
            ArrayList<New> oldNews = dbController.select(null, validRows);
            if (!oldNews.isEmpty()) category_date += '/' + oldNews.get(0).getDate();

            for (New n : oldNews) {
                _IDs.add(n.get_ID());
                images.add(n.getImage());
            }

            ArrayList<New> news =
                    NewsServiceController.getNews(context, category_date, _IDs, images);

            for (New n : news) {
                int index = _IDs.indexOf(n.get_ID());

                if (index == -1) {
                    dbController.insert(n.get_ID(), n.getName(), n.getLink(), Utilities.saveImage(
                            n.getImage(), "/news", context), n.getSummary(), n.getContent(),
                            n.getDate(), n.getAuthor());

                    for (NewRelation relation : NewsServiceController
                            .getNewRelations(context, '/' + n.get_ID())) {
                        dbController
                                .insertRelation(relation.getCategory_ID(), relation.getNew_ID());
                    }
                } else {
                    _IDs.remove(index);
                    String image = images.remove(index);

                    if (image != null) {
                        Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                    }

                    dbController.update(n.get_ID(), n.getName(), n.getLink(), Utilities.saveImage(
                            n.getImage(), "/news", context), n.getSummary(), n.getContent(),
                            n.getDate(), n.getAuthor());
                }

                if (++inserted <= 5 && manager != null && notify) {
                    manager.notify(n.getName(), mNotificationId, buildNotification(type, n));
                }
            }

            // Se eliminan los items que hay en la aplicacion pero no en el servidor
            if (!news.isEmpty()) {
                dbController.delete(_IDs.toArray());

                for (String image : images) if (image != null) {
                    Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                }
            }
        }

        dbController.destroy();
        sendBroadcast(new Intent(ACTION_NEWS).putExtra("INSERTED", inserted));
    }

    private void loadInformations() {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        InformationsSQLiteController dbController =
                new InformationsSQLiteController(getApplicationContext(), 1);

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            boolean updateInformations = false;
            ArrayList<InformationCategory> updatedCategories =
                    InformationsServiceController.getInformationCategories(getApplicationContext());
            for (InformationCategory category : updatedCategories) {
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
                    ArrayList<Information> updatedInformations =
                            InformationsServiceController.getInformations(getApplicationContext(), category.get_ID());
                    for (Information information : updatedInformations) {
                        ArrayList<Information> olds = dbController.select(
                                InformationsSQLiteController.CAMPOS_TABLA[0]+" = ?", new String[]{information.get_ID()});
                        if (!olds.isEmpty()) {
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
        sendBroadcast(new Intent(PENDING_ACTION));
    }

    private void loadContacts() {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        Context context = getApplicationContext();

        if (Utilities.haveNetworkConnection(context)) {
            ContactsSQLiteController dbController =
                    new ContactsSQLiteController(context, 1);

            if (dbController.selectCategory("1", null).isEmpty()) {
                for (ContactCategory category :
                        ContactsServiceController.getContactCategories(context)) {
                    dbController.insertCategory(category.get_ID(), category.getName(),
                            category.getLink());
                }

                for (Contact contact : ContactsServiceController.getContacts(context)) {
                    dbController.insert(contact.get_ID(), contact.getCategory_ID(),
                            contact.getName(), contact.getAddress(), contact.getPhone(),
                            contact.getEmail(), contact.getCharge(),
                            contact.getAdditionalInformation());
                }
            }

            dbController.destroy();
        }

        sendBroadcast(new Intent(PENDING_ACTION));
    }

    private void loadPrograms() {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        ProgramsSQLiteController dbController = new ProgramsSQLiteController(getApplicationContext(), 1);

        ArrayList<ProgramCategory> oldCategories = dbController.selectCategory(null, null);
        if (oldCategories.size() == 0 && Utilities.haveNetworkConnection(getApplicationContext())) {

            ArrayList<ProgramCategory> updatedCategories = ProgramsServiceController.getProgramCategories(getApplicationContext());
            for (ProgramCategory category : updatedCategories) {
                dbController.insertCategory(category.get_ID(), category.getName());
            }

            ArrayList<ProgramFaculty> updatedFaculties = ProgramsServiceController.getProgramFaculties(getApplicationContext());
            for (ProgramFaculty faculty : updatedFaculties) {
                dbController.insertFaculty(faculty.get_ID(), faculty.getName());
            }

        }

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            ArrayList<Program> updatedPrograms = ProgramsServiceController.getPrograms(getApplicationContext());
            for (Program program : updatedPrograms) {
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
        sendBroadcast(new Intent(PENDING_ACTION));
    }

    private void loadCalendar() {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        Context context = getApplicationContext();
        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        if (Utilities.haveNetworkConnection(context)) {
            ArrayList<EventDate> oldDates = dbController.selectDate(
                    EventsSQLiteController.dateColumns[1]+" = 'fechasPub'");
            ArrayList<EventDate> dates =
                    EventsServiceController.getEventDates(context, "fechasPub");
            if (!oldDates.isEmpty() && !dates.isEmpty() &&
                    oldDates.get(0).getDate().compareTo(dates.get(0).getDate()) < 0) {
                dbController.deleteDate();
                dbController.deletePeriod();
                dbController.delete();
                dbController.deleteCategory();
            }

            if (dbController.selectCategory("1", null).isEmpty()) {
                for (EventCategory category : EventsServiceController.getEventCategories(context)) {
                    dbController.insertCategory(category.get_ID(), category.getAbbreviation(),
                            category.getName());
                }

                for (Event event : EventsServiceController.getEvents(context)) {
                    dbController.insert(event.get_ID(), event.getName());
                }

                for (EventPeriod period : EventsServiceController.getEventPeriods(context)) {
                    dbController.insertPeriod(period.get_ID(), period.getName());
                }

                for (EventDate date : EventsServiceController.getEventDates(context, "")) {
                    dbController.insertDate(date.get_ID(), date.getType(), date.getDate());
                }

                for (EventRelation relation : EventsServiceController.getEventRelations(context)) {
                    dbController.insertRelation(relation.getCategory_ID(), relation.getEvent_ID(),
                            relation.getPeriod_ID(), relation.getDate_ID());
                }
            }
        }

        Locale locale = new Locale("es", "CO");
        SimpleDateFormat notifyDateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        String notifyDate = notifyDateFormat.format(Calendar.getInstance().getTime());

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Utilities.PREFERENCES, Context.MODE_PRIVATE);

        if (!notifyDate.equals(sharedPreferences.getString(Utilities.CALENDAR_NOTIFY, null))) {
            sharedPreferences.edit().putString(Utilities.CALENDAR_NOTIFY, notifyDate).apply();

            NotificationManager manager = NotificationsPresenter
                    .getNotification(context, "2").getActivated().equals("S") ?
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) : null;

            if (manager != null) {
                SimpleDateFormat datesFormat =
                        new SimpleDateFormat("d 'de' MMMM 'de' yyyy", locale);

                int inserted = 0;

                ArrayList<EventRelation> relations = dbController.selectRelation(
                        EventsSQLiteController.relationColumns, null);
                ArrayList<Event> events = dbController.select(null);

                for (EventDate date : dbController.selectDate(null)) {
                    try {
                        if (notifyDateFormat.format(datesFormat.parse(date.getDate()))
                                .equals(notifyDate)) {
                            for (EventRelation relation : relations) {
                                if (relation.getDate_ID().equals(date.get_ID())) {
                                    for (Event event : events) {
                                        if (event.get_ID().equals(relation.getEvent_ID())) {
                                            manager.notify(event.getName(), mNotificationId,
                                                    buildNotification(ACTION_CALENDAR, event));

                                            inserted++;
                                            break;
                                        }
                                    }

                                    if (inserted >= 5) break;
                                }
                            }

                            if (inserted >= 5) break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        dbController.destroy();
        sendBroadcast(new Intent(PENDING_ACTION));
    }

    private void loadAnnouncements(String type, String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        String response = null;
        int inserted = 0;
        Context context = getApplicationContext();

        if (Utilities.haveNetworkConnection(context)) switch (method) {
            case METHOD_POST:
            case METHOD_PUT:
            case METHOD_DELETE:
                try {
                    JSONObject json = new JSONObject(object);
                    JSONArray links = (JSONArray) json.remove("links");
                    json = new JSONObject(AnnouncementsServiceController
                            .modifyAnnouncement(context, json.toString()));
                    StringBuilder builder = new StringBuilder(json.getString("mensaje"));
                    int _ID = json.getInt("id");

                    if (links != null) for (int i = 0; i < links.length(); i++) {
                        builder.append('\n').append(AnnouncementsServiceController
                                .modifyAnnouncementLink(context, encodeImageString(links
                                        .getJSONObject(i)
                                        .put(AnnouncementsSQLiteController.linkColumns[1], _ID))));
                    }

                    response = builder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Despues de modificar se actualiza, por eso aqui no hay break
            case METHOD_GET: {
                String selectionArg;
                String category_date;
                boolean notify;

                if (ACTION_INCIDENTS.equals(type)) {
                    selectionArg = "I";
                    category_date = "/incidentes";
                    notify = NotificationsPresenter.getNotification(context, "4")
                            .getActivated().equals("S");
                } else {
                    selectionArg = "C";
                    category_date = "/comunicados";
                    notify = NotificationsPresenter.getNotification(context, "5")
                            .getActivated().equals("S");
                }

                AnnouncementsSQLiteController dbController =
                        new AnnouncementsSQLiteController(context, 1);

                ArrayList<String> _IDs = new ArrayList<>();
                ArrayList<Announcement> announcements = dbController.select(null,
                        AnnouncementsSQLiteController.columns[2]+" = ?", selectionArg);
                for (Announcement announcement : announcements) _IDs.add(announcement.get_ID());
                if (_IDs.isEmpty()) notify = false;
                else category_date += '/'+announcements.get(0).getDate();
                Utilities.State state = new Utilities.State(Utilities.FAILURE_STATE);
                announcements = AnnouncementsServiceController
                        .getAnnouncements(context, category_date, state, _IDs);

                NotificationManager manager = notify ?
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) : null;

                for (Announcement announcement : announcements) {
                    int index = _IDs.indexOf(announcement.get_ID());

                    if (index == -1) {
                        dbController.insert(announcement.get_ID(), announcement.getUser_ID(),
                                announcement.getType(), announcement.getName(),
                                announcement.getDate(), announcement.getDescription(),
                                announcement.getRead());
                    } else {
                        dbController.update(announcement.get_ID(), announcement.getUser_ID(),
                                announcement.getType(), announcement.getName(),
                                announcement.getDate(), announcement.getDescription(),
                                announcement.get_ID());
                        _IDs.remove(index);
                    }

                    ArrayList<String> links_IDs = new ArrayList<>();
                    ArrayList<String> linksLinks = new ArrayList<>();

                    for (AnnouncementLink link : dbController.selectLink(
                            AnnouncementsSQLiteController.linkColumns[1]+" = ?",
                            announcement.get_ID())) {
                        links_IDs.add(link.get_ID());
                        linksLinks.add(link.getLink());
                    }

                    ArrayList<AnnouncementLink> links =
                            AnnouncementsServiceController.getAnnouncementLinks(context,
                                    '/' + announcement.get_ID());

                    for (AnnouncementLink link : links) {
                        int linkIndex = links_IDs.indexOf(link.get_ID());

                        if (linkIndex == -1) {
                            dbController.insertLink(link.get_ID(), link.getAnnouncement_ID(),
                                    link.getType(), Utilities.saveImage(link.getLink(),
                                            "/announcements", context));
                        } else {
                            links_IDs.remove(linkIndex);
                            String linkLink = linksLinks.remove(index);

                            if (linkLink != null) {
                                Log.i(TAG, "delete " + linkLink + ": " +
                                        new File(linkLink).delete());
                            }

                            dbController.updateLink(link.get_ID(), link.getAnnouncement_ID(),
                                    link.getType(), Utilities.saveImage(link.getLink(),
                                            "/announcements", context), link.get_ID());
                        }
                    }

                    // Se eliminan los items que hay en la aplicacion pero no en el servidor
                    if (state.get() == Utilities.SUCCESS_STATE) {
                        dbController.deleteLink(links_IDs.toArray());

                        for (String linkLink : linksLinks) if (linkLink != null) {
                            Log.i(TAG, "delete " + linkLink + ": " +
                                    new File(linkLink).delete());
                        }
                    }

                    if (++inserted <= 5 && manager != null) {
                        manager.notify(announcement.getName(), mNotificationId,
                                buildNotification(type, announcement));
                    }
                }

                // Se eliminan los items que hay en la aplicacion pero no en el servidor
                if (state.get() == Utilities.SUCCESS_STATE) {
                    for (AnnouncementLink announcementLink : dbController.selectLink(
                            AnnouncementsSQLiteController.linkColumns[1] + " IN(" +
                                    TextUtils.join(", ", Collections.nCopies(_IDs.size(),
                                            '?')) + ')',
                            _IDs.toArray(new String[_IDs.size()]))) {
                        Log.i(TAG, "delete " + announcementLink.getLink() + ": " +
                                new File(announcementLink.getLink()).delete());
                    }

                    dbController.delete(_IDs.toArray());
                }
                dbController.destroy();
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_INCIDENTS).putExtra("INSERTED", inserted)
                .putExtra("RESPONSE", response));
    }

    private void loadObjects(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        String response = null;
        int inserted = 0;
        Context context = getApplicationContext();

        if (Utilities.haveNetworkConnection(context)) switch (method) {
            case METHOD_POST:
            case METHOD_PUT:
            case METHOD_DELETE:
                try {
                    response = ObjectsServiceController
                            .modifyObject(context, encodeImageString(new JSONObject(object)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Despues de modificar se actualiza, por eso aqui no hay break
            case METHOD_GET: {
                ObjectsSQLiteController dbController =
                        new ObjectsSQLiteController(context, 1);

                ArrayList<String> _IDs = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                ArrayList<LostObject> objects = dbController.select(null, null);

                for (LostObject lostObject : objects) {
                    _IDs.add(lostObject.get_ID());
                    images.add(lostObject.getImage());
                }

                Utilities.State state = new Utilities.State(Utilities.FAILURE_STATE);
                objects = ObjectsServiceController.getObjects(context,
                        _IDs.isEmpty() ? "" : '/'+objects.get(0).getDate(), state, _IDs, images);

                NotificationManager manager = NotificationsPresenter.getNotification(context,
                        "3").getActivated().equals("S") && !_IDs.isEmpty() ?
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) : null;

                for (LostObject obj : objects) {
                    int index = _IDs.indexOf(obj.get_ID());

                    if (index == -1) {
                        dbController.insert(obj.get_ID(), obj.getUserLost_ID(), obj.getName(),
                                obj.getPlace(), obj.getDateLost(), obj.getDate(),
                                obj.getDescription(), Utilities.saveImage(obj.getImage(),
                                        "/objects", context), obj.getUserFound_ID(),
                                obj.getReaded());
                    } else {
                        _IDs.remove(index);
                        String image = images.remove(index);

                        if (image != null) {
                            Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                        }

                        dbController.update(obj.get_ID(), obj.getUserLost_ID(), obj.getName(),
                                obj.getPlace(), obj.getDateLost(), obj.getDate(),
                                obj.getDescription(), Utilities.saveImage(obj.getImage(),
                                        "/objects", context), obj.getUserFound_ID(),
                                obj.get_ID());
                    }

                    if (++inserted <= 5 && manager != null) {
                        manager.notify(obj.getName(), mNotificationId,
                                buildNotification(ACTION_OBJECTS, obj));
                    }
                }

                // Se eliminan los items que hay en la aplicacion pero no en el servidor
                if (state.get() == Utilities.SUCCESS_STATE) {
                    dbController.delete(_IDs.toArray());

                    for (String image : images) if (image != null) {
                        Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                    }
                }

                dbController.destroy();
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_OBJECTS).putExtra("INSERTED", inserted)
                .putExtra("RESPONSE", response));
    }

    private void loadDishes(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        String response = null;
        int inserted = 0;
        Context context = getApplicationContext();

        if (Utilities.haveNetworkConnection(context)) switch (method) {
            case METHOD_POST:
            case METHOD_PUT:
            case METHOD_DELETE:
                try {
                    response = DishesServiceController
                            .modifyDish(context, encodeImageString(new JSONObject(object)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Despues de modificar se actualiza, por eso aqui no hay break
            case METHOD_GET: {
                DishesSQLiteController dbController =
                        new DishesSQLiteController(context, 1);

                ArrayList<String> _IDs = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();

                for (Dish dish : dbController.select(null)) {
                    _IDs.add(dish.get_ID());
                    images.add(dish.getImage());
                }

                ArrayList<Dish> dishes = DishesServiceController.getDishes(context);

                for (Dish dish : dishes) {
                    int index = _IDs.indexOf(dish.get_ID());

                    if (index == -1) {
                        dbController.insert(dish.get_ID(), dish.getName(), dish.getDescription(),
                                dish.getPrice(), Utilities.saveImage(dish.getImage(),
                                        "/dishes", context));
                        inserted++;
                    } else {
                        _IDs.remove(index);
                        String image = images.remove(index);

                        if (image != null) {
                            Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                        }

                        dbController.update(dish.get_ID(), dish.getName(), dish.getDescription(),
                                dish.getPrice(), Utilities.saveImage(dish.getImage(),
                                        "/dishes", context), dish.get_ID());
                    }
                }

                // Se eliminan los items que hay en la aplicacion pero no en el servidor
                if (!dishes.isEmpty()) {
                    dbController.delete(_IDs.toArray());

                    for (String image : images) if (image != null) {
                        Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                    }
                }
                dbController.destroy();
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_DISHES).putExtra("INSERTED", inserted)
                .putExtra("RESPONSE", response));
    }

    private static String encodeImageString(JSONObject json) throws JSONException, IOException {
        if (!json.isNull("imageString")) {
            FileInputStream input =
                    new FileInputStream(json.getString("imageString"));
            byte[] buffer = new byte[4096];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream encoder =
                    new Base64OutputStream(output, Base64.NO_WRAP);

            while ((bytesRead = input.read(buffer)) != -1) {
                encoder.write(buffer, 0, bytesRead);
            }

            input.close();
            encoder.close();
            json.put("imageString", output.toString());
        }

        return json.toString();
    }

    private void loadQuotas(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        String response = null;
        Context context = getApplicationContext();

        if (Utilities.haveNetworkConnection(context)) switch (method) {
            case METHOD_POST:
            case METHOD_PUT:
            case METHOD_DELETE:
                response = QuotasServiceController.modifyQuota(context, object);
                // Despues de modificar se actualiza, por eso aqui no hay break
            case METHOD_GET: {
                QuotasSQLiteController dbController =
                        new QuotasSQLiteController(context, 1);

                ArrayList<String> _IDs = new ArrayList<>();
                for (Quota quota : dbController.select(null)) _IDs.add(quota.get_ID());
                ArrayList<Quota> quotas = QuotasServiceController.getQuotas(context);

                for (Quota quota : quotas) {
                    int index = _IDs.indexOf(quota.get_ID());
                    if (index == -1) {
                        dbController.insert(quota.get_ID(), quota.getType(), quota.getName(),
                                quota.getQuota());
                    } else {
                        dbController.update(quota.get_ID(), quota.getType(), quota.getName(),
                                quota.getQuota(), quota.get_ID());
                        _IDs.remove(index);
                    }
                }

                // Se eliminan los items que hay en la aplicacion pero no en el servidor
                if (!quotas.isEmpty()) dbController.delete(_IDs.toArray());
                dbController.destroy();
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_QUOTAS).putExtra("RESPONSE", response));
    }

    private void loadUsers(String method, String object) {
        User user = null;

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            UsersSQLiteController dbController =
                    new UsersSQLiteController(getApplicationContext(), 1);
            switch (method) {
                case METHOD_POST:
                case METHOD_PUT:
                    String respStr = UsersServiceController.modifyUser(object);
                    try {
                        JSONObject jsonResp = new JSONObject(respStr);
                        int estado = jsonResp.getInt("estado");
                        if (estado == 5) {
                            user = UsersPresenter.loadUser(getApplicationContext());
                        } else {
                            Log.e(TAG, jsonResp.getString("mensaje"));
                            break;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, respStr);
                        break;
                    }
                case METHOD_DELETE:
                    for (User u : dbController.select()) {
                        if (!u.getEmail().equals("campusuq@uniquindio.edu.co")) {
                            dbController.delete(u.get_ID());
                        }
                    }
                    break;
                case METHOD_GET:
                    user = UsersServiceController.login(object);
                    if (user != null) {
                        dbController.insert(user.get_ID(), user.getName(), user.getEmail(),
                                user.getPhone(), user.getAddress(), user.getDocument(),
                                user.getPassword(), user.getApiKey(), user.getAdministrator());
                    }
                    break;
            }
            dbController.destroy();
        }

        sendBroadcast(new Intent(ACTION_USERS).putExtra("USER", user));
    }

    private void loadEmails(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled) return;
        Intent intent = null;
        int inserted = 0;

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            switch(method) {
                case METHOD_POST:
                case METHOD_PUT:
                case METHOD_DELETE:
                    try {
                        JSONObject jsonEmail = new JSONObject(object);
                        Email email = new Email("0",
                                jsonEmail.getString(EmailsSQLiteController.columns[1]),
                                jsonEmail.getString(EmailsSQLiteController.columns[2]),
                                jsonEmail.getString(EmailsSQLiteController.columns[3]),
                                jsonEmail.getString(EmailsSQLiteController.columns[4]), "",
                                jsonEmail.getString(EmailsSQLiteController.columns[6]),
                                new BigInteger("0"));
                        inserted = EmailsServiceController
                                .sendEmail(getApplicationContext(), email) ? 1 : 0;
                    } catch (Exception e) {
                        if (e instanceof UserRecoverableAuthIOException) {
                            intent = ((UserRecoverableAuthIOException) e).getIntent();
                        } else {
                            e.printStackTrace();
                        }
                    }
                    break;
                case METHOD_GET:
                    boolean notify = NotificationsPresenter.getNotification(getApplicationContext(),
                            "6").getActivated().equals("S");
                    NotificationManager manager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    User user = UsersPresenter.loadUser(getApplicationContext());
                    if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                        EmailsSQLiteController dbController =
                                new EmailsSQLiteController(getApplicationContext(), 1);

                        ArrayList<String> oldIDs = new ArrayList<>();
                        ArrayList<Email> olds = dbController.select("50");
                        for (Email old : olds) oldIDs.add(old.get_ID());
                        try {
                            ArrayList<Email> emails = EmailsServiceController.getEmails(
                                    getApplicationContext(),
                                    !olds.isEmpty() ? olds.get(0).getHistoryID() : null);
                            notify = emails.size() > 0 && notify;
                            for (Email email : emails) {
                                int index = oldIDs.indexOf(email.get_ID());
                                if (index == -1) {
                                    oldIDs.add(email.get_ID());
                                    dbController.insert(email.get_ID(), email.getName(),
                                            email.getFrom(), email.getTo(), email.getDate(),
                                            email.getSnippet(), email.getContent(),
                                            ""+email.getHistoryID());
                                    inserted ++;
                                    if (manager != null && notify && inserted <= 5) {
                                        manager.notify(email.getName(), mNotificationId,
                                                buildNotification(ACTION_EMAILS, email));
                                    }
                                }
                            }
                        } catch (UserRecoverableAuthIOException e) {
                            intent = e.getIntent();
                        }

                        dbController.destroy();
                    }
                    break;
            }
        }

        sendBroadcast(new Intent(ACTION_EMAILS).putExtra("INSERTED", inserted)
                .putExtra("INTENT", intent));
    }

    @Override
    public void onDestroy() {
        Log.i(WebBroadcastReceiver.class.getSimpleName(), "destruyendo el web service");
        super.onDestroy();
    }

}
