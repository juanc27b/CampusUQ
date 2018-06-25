package co.edu.uniquindio.campusuq.web;

import android.app.IntentService;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.announcements.Announcement;
import co.edu.uniquindio.campusuq.announcements.AnnouncementLink;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsActivity;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsSQLiteController;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsServiceController;
import co.edu.uniquindio.campusuq.contacts.Contact;
import co.edu.uniquindio.campusuq.contacts.ContactCategory;
import co.edu.uniquindio.campusuq.contacts.ContactsSQLiteController;
import co.edu.uniquindio.campusuq.contacts.ContactsServiceController;
import co.edu.uniquindio.campusuq.dishes.Dish;
import co.edu.uniquindio.campusuq.dishes.DishesSQLiteController;
import co.edu.uniquindio.campusuq.dishes.DishesServiceController;
import co.edu.uniquindio.campusuq.emails.Email;
import co.edu.uniquindio.campusuq.emails.EmailsActivity;
import co.edu.uniquindio.campusuq.emails.EmailsSQLiteController;
import co.edu.uniquindio.campusuq.emails.EmailsServiceController;
import co.edu.uniquindio.campusuq.events.Event;
import co.edu.uniquindio.campusuq.events.EventCategory;
import co.edu.uniquindio.campusuq.events.EventDate;
import co.edu.uniquindio.campusuq.events.EventPeriod;
import co.edu.uniquindio.campusuq.events.EventRelation;
import co.edu.uniquindio.campusuq.events.EventsSQLiteController;
import co.edu.uniquindio.campusuq.events.EventsServiceController;
import co.edu.uniquindio.campusuq.informations.Information;
import co.edu.uniquindio.campusuq.informations.InformationCategory;
import co.edu.uniquindio.campusuq.informations.InformationsSQLiteController;
import co.edu.uniquindio.campusuq.informations.InformationsServiceController;
import co.edu.uniquindio.campusuq.items.ItemsActivity;
import co.edu.uniquindio.campusuq.items.ItemsPresenter;
import co.edu.uniquindio.campusuq.news.New;
import co.edu.uniquindio.campusuq.news.NewCategory;
import co.edu.uniquindio.campusuq.news.NewRelation;
import co.edu.uniquindio.campusuq.news.NewsActivity;
import co.edu.uniquindio.campusuq.news.NewsSQLiteController;
import co.edu.uniquindio.campusuq.news.NewsServiceController;
import co.edu.uniquindio.campusuq.notifications.NotificationDetail;
import co.edu.uniquindio.campusuq.notifications.NotificationsPresenter;
import co.edu.uniquindio.campusuq.objects.LostObject;
import co.edu.uniquindio.campusuq.objects.ObjectsActivity;
import co.edu.uniquindio.campusuq.objects.ObjectsSQLiteController;
import co.edu.uniquindio.campusuq.objects.ObjectsServiceController;
import co.edu.uniquindio.campusuq.programs.Program;
import co.edu.uniquindio.campusuq.programs.ProgramCategory;
import co.edu.uniquindio.campusuq.programs.ProgramFaculty;
import co.edu.uniquindio.campusuq.programs.ProgramsSQLiteController;
import co.edu.uniquindio.campusuq.programs.ProgramsServiceController;
import co.edu.uniquindio.campusuq.quotas.Quota;
import co.edu.uniquindio.campusuq.quotas.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.quotas.QuotasServiceController;
import co.edu.uniquindio.campusuq.users.User;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.users.UsersSQLiteController;
import co.edu.uniquindio.campusuq.users.UsersServiceController;
import co.edu.uniquindio.campusuq.util.State;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Servicio que permite obtener del servidor los datos necesarios para la base de datos local,
 * ademas tambien permite enviar peticiones al servidor para insertar, actualizar o eliminar items
 * de algunas de las tablas.
 */
public class WebService extends IntentService {

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

    public static final String FOREGROUND_NOTIFICATION =
            "co.edu.uniquindio.campusuq.FOREGROUND_NOTIFICATION";

    public static String PENDING_ACTION = ACTION_NONE;

    public static final String[] NOTIFICATIONS = {"Events", "News", "Academic Calendar",
            "Lost Objects", "Security System", "Billboard Information", "Institutional Mail"};

    private static final int foregroundNotificationId = 1;
    private static final int mNotificationId = 2;

    private static final String TAG = WebService.class.getSimpleName();

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public WebService() {
        super("WebService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service starting");

        Notification notification = buildNotification(FOREGROUND_NOTIFICATION, null);
        startForeground(foregroundNotificationId, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Service started!");

        try {
            User user = UsersPresenter.loadUser(getApplicationContext());
            ArrayList<co.edu.uniquindio.campusuq.notifications.Notification> notifications =
                    NotificationsPresenter.loadNotifications(getApplicationContext());

            if (Utilities.haveNetworkConnection(getApplicationContext())) {
                if (user == null) try {
                    loadUsers(METHOD_GET, new JSONObject()
                            .put(UsersSQLiteController.columns[2],
                                    "campusuq@uniquindio.edu.co")
                            .put(UsersSQLiteController.columns[6], "campusuq")
                            .toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (notifications.size() < NOTIFICATIONS.length) {
                    NotificationsPresenter.insertNotifications(getApplicationContext());
                }

                doWork(intent);
                PENDING_ACTION = ACTION_NONE;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in Service");
            e.printStackTrace();
        }
    }

    private void doWork(Intent intent) {
        Log.i(TAG, "Working");
        String action = intent.getStringExtra("ACTION");
        String method = intent.getStringExtra("METHOD");
        String object = intent.getStringExtra("OBJECT");

        switch (action) {
            case ACTION_ALL:
                int progress = 0;
                Intent menuIntent = new Intent(ACTION_ALL);
                loadInformations();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_contacts));
                loadContacts();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_programs));
                loadPrograms();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_calendar));
                loadCalendar();
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_events));
                loadNews(ACTION_EVENTS);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_news));
                loadNews(ACTION_NEWS);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_incidents));
                loadAnnouncements(ACTION_INCIDENTS, method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_communiques));
                loadAnnouncements(ACTION_COMMUNIQUES, method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_objects));
                loadObjects(method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_dishes));
                loadDishes(method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_quotas));
                loadQuotas(method, object);
                sendBroadcast(menuIntent.putExtra("PROGRESS", ++progress)
                        .putExtra(Utilities.FEEDBACK,
                                R.string.wait_to_emails));
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

        Log.i(TAG, "Work finished!");
    }

    private Notification buildNotification(String type, Object object) {
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        NotificationDetail notificationDetail = new NotificationDetail(null, 0,
                null, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
                new Locale("es", "CO")).format(Calendar.getInstance().getTime()),
                null);

        switch (type) {
            case ACTION_NEWS:
            case ACTION_EVENTS: {
                New mNew = (New) object;
                builder.setContentTitle(getString(R.string.app_name) + " - " + getString(ACTION_NEWS.equals(type) ?
                                R.string.news : R.string.events))
                        .setContentText(mNew.getName())
                        .setSubText(mNew.getSummary());
                file = new File(mNew.getImage());
                notificationDetail.setCategory(ACTION_NEWS.equals(type) ?
                        R.string.news : R.string.events);
                notificationDetail.setName(mNew.getName());
                notificationDetail.setDescription(mNew.getSummary());
                break;
            }
            case ACTION_OBJECTS: {
                LostObject lostObject = (LostObject) object;
                builder.setContentTitle(getString(R.string.app_name) + " - " + getString(R.string.lost_objects))
                        .setContentText(lostObject.getName())
                        .setSubText(lostObject.getDescription());
                // Se concatena una cadena vacia para evitar el caso File(null)
                file = new File("" + lostObject.getImage());
                notificationDetail.setCategory(R.string.lost_objects);
                notificationDetail.setName(lostObject.getName());
                notificationDetail.setDescription(lostObject.getDescription());
                break;
            }
            case ACTION_CALENDAR: {
                Event event = (Event) object;
                builder.setContentTitle(getString(R.string.app_name) + " - " + getString(R.string.academic_calendar))
                        .setContentText(event.getName());
                notificationDetail.setCategory(R.string.academic_calendar);
                notificationDetail.setName(event.getName());
                break;
            }
            case ACTION_INCIDENTS:
            case ACTION_COMMUNIQUES: {
                Announcement announcement = (Announcement) object;
                builder.setContentTitle(getString(R.string.app_name) + " - " +
                        getString(type.equals(ACTION_INCIDENTS) ?
                                R.string.security_system : R.string.billboard_information))
                        .setContentText(announcement.getName())
                        .setSubText(announcement.getDescription());
                ArrayList<AnnouncementLink> links =
                        new AnnouncementsSQLiteController(getApplicationContext(), 1)
                                .selectLink(AnnouncementsSQLiteController.linkColumns[1] +
                                        " = ?", announcement.get_ID());
                file = links.size() > 0 ? new File(links.get(0).getLink()) : new File("");
                notificationDetail.setCategory(type.equals(ACTION_INCIDENTS) ?
                        R.string.security_system : R.string.billboard_information);
                notificationDetail.setName(announcement.getName());
                notificationDetail.setDescription(announcement.getDescription());
                break;
            }
            case ACTION_EMAILS: {
                Email email = (Email) object;
                builder.setContentTitle(getString(R.string.app_name) + " - " + getString(R.string.institutional_mail))
                        .setContentText(email.getName())
                        .setSubText(email.getSnippet());
                file = new File("");
                notificationDetail.setCategory(R.string.institutional_mail);
                notificationDetail.setName(email.getName());
                notificationDetail.setDescription(email.getSnippet());
                break;
            }
            default:
                builder.setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.loading_content))
                        .setUsesChronometer(true);
                break;
        }

        if (file != null && file.exists()) {
            builder.setLargeIcon(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            builder.setLargeIcon(BitmapFactory
                    .decodeResource(getApplicationContext().getResources(), R.drawable.app_icon));
        }

        if (!FOREGROUND_NOTIFICATION.equals(type)) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(buildPendingIntent(type))
                    .setAutoCancel(true);
        }

        if (notificationDetail.getCategory() != 0) {
            NotificationsPresenter.insertNotificationDetail(getApplicationContext(),
                    notificationDetail.get_ID(), notificationDetail.getCategory(),
                    notificationDetail.getName(), notificationDetail.getDateTime(),
                    notificationDetail.getDescription());
        }

        return builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    private PendingIntent buildPendingIntent(String type) {
        Intent resultIntent = null;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

        switch (type) {
            case ACTION_NEWS:
            case ACTION_EVENTS:
                resultIntent = new Intent(getApplicationContext(), NewsActivity.class)
                        .putExtra(Utilities.CATEGORY,
                                ACTION_NEWS.equals(type) ? R.string.news : R.string.events);
                stackBuilder.addParentStack(NewsActivity.class);
                stackBuilder.editIntentAt(0)
                        .putExtra(Utilities.CATEGORY, R.string.app_title_menu);
                stackBuilder.editIntentAt(1)
                        .putExtra(Utilities.CATEGORY, R.string.information_module);
                break;
            case ACTION_OBJECTS:
                resultIntent = new Intent(getApplicationContext(), ObjectsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.lost_objects);
                stackBuilder.addParentStack(ObjectsActivity.class);
                stackBuilder.editIntentAt(0)
                        .putExtra(Utilities.CATEGORY, R.string.app_title_menu);
                stackBuilder.editIntentAt(1)
                        .putExtra(Utilities.CATEGORY, R.string.services_module);
                break;
            case ACTION_CALENDAR:
                resultIntent = new Intent(getApplicationContext(), ItemsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.academic_calendar)
                        .putParcelableArrayListExtra(Utilities.ITEMS,
                        ItemsPresenter.getEventCategories(getApplicationContext()));
                stackBuilder.addParentStack(ItemsActivity.class);
                stackBuilder.editIntentAt(0)
                        .putExtra(Utilities.CATEGORY, R.string.app_title_menu);
                break;
            case ACTION_INCIDENTS:
            case ACTION_COMMUNIQUES:
                resultIntent = new Intent(getApplicationContext(), AnnouncementsActivity.class)
                        .putExtra(Utilities.CATEGORY, ACTION_INCIDENTS.equals(type) ?
                        R.string.security_system : R.string.billboard_information);
                stackBuilder.addParentStack(AnnouncementsActivity.class);
                stackBuilder.editIntentAt(0)
                        .putExtra(Utilities.CATEGORY, R.string.app_title_menu);
                stackBuilder.editIntentAt(1)
                        .putExtra(Utilities.CATEGORY,
                        ACTION_INCIDENTS.equals(type) ? R.string.services_module : R.string.state_module);
                break;
            case ACTION_EMAILS:
                resultIntent = new Intent(getApplicationContext(), EmailsActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.institutional_mail);
                stackBuilder.addParentStack(EmailsActivity.class);
                stackBuilder.editIntentAt(0)
                        .putExtra(Utilities.CATEGORY, R.string.app_title_menu);
                stackBuilder.editIntentAt(1)
                        .putExtra(Utilities.CATEGORY, R.string.communication_module);
                break;
            default:
                break;
        }

        return stackBuilder.addNextIntent(resultIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void loadNews(String type) {
        Context context = getApplicationContext();
        int inserted = 0;

        if (Utilities.haveNetworkConnection(context)) {
            NewsSQLiteController dbController = new NewsSQLiteController(context, 1);

            NotificationManager manager;
            String category_date;
            State state = new State(Utilities.FAILURE_STATE);
            ArrayList<String> _IDs = new ArrayList<>();
            ArrayList<String> images = new ArrayList<>();

            {
                ArrayList<NewCategory> categories = dbController.selectCategory("1",
                        NewsSQLiteController.categoryColumns[1] + " = ?",
                        "Eventos");
                String selection;
                boolean notify;

                if (ACTION_NEWS.equals(type)) {
                    selection = " NOT IN(";
                    notify = "S".equals(NotificationsPresenter
                            .getNotification(context, "0").getActivated());
                    category_date = "/no_eventos";
                } else {
                    selection = " IN(";
                    notify = "S".equals(NotificationsPresenter
                            .getNotification(context, "1").getActivated());
                    category_date = "/eventos";
                }

                ArrayList<New> news = new ArrayList<>();

                if (!categories.isEmpty()) {
                    ArrayList<NewRelation> relations = dbController.selectRelation(
                            NewsSQLiteController.relationColumns[0] + " = ?",
                            categories.get(0).get_ID());

                    String[] New_IDs = new String[relations.size()];

                    for (int i = 0; i < New_IDs.length; i++) {
                        New_IDs[i] = relations.get(i).getNew_ID();
                    }

                    news = dbController.select(null,
                            NewsSQLiteController.columns[0] + selection + TextUtils.join(
                                    ", ", Collections.nCopies(New_IDs.length, '?')) +
                                    ')',
                            New_IDs);
                }

                if (news.isEmpty()) notify = false;
                else category_date += '/' + news.get(0).getDate();
                manager = notify ?
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) : null;

                for (New n : news) {
                    _IDs.add(n.get_ID());
                    images.add(n.getImage());
                }
            }

            {
                ArrayList<String> category_ID = new ArrayList<>();

                for (NewCategory category :
                        dbController.selectCategory(null, null)) {
                    category_ID.add(category.get_ID());
                }

                ArrayList<NewCategory> categories = NewsServiceController.getNewCategories(context);

                for (NewCategory category : categories) {
                    int index = category_ID.indexOf(category.get_ID());

                    if (index == -1) {
                        dbController.insertCategory(category.get_ID(), category.getName(),
                                category.getLink());
                    } else {
                        category_ID.remove(index);
                        dbController.updateCategory(category.get_ID(), category.getName(),
                                category.getLink(), category.get_ID());
                    }
                }

                if (!categories.isEmpty()) dbController.deleteCategory(category_ID.toArray());
            }

            for (New n :
                    NewsServiceController.getNews(context, category_date, state, _IDs, images)) {
                int index = _IDs.indexOf(n.get_ID());

                if (index == -1) {
                    dbController.insert(n.get_ID(), n.getName(), n.getLink(), Utilities.saveMedia(
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

                    dbController.update(n.get_ID(), n.getName(), n.getLink(), Utilities.saveMedia(
                            n.getImage(), "/news", context), n.getSummary(), n.getContent(),
                            n.getDate(), n.getAuthor(), n.get_ID());
                }

                if (++inserted <= 5 && manager != null) {
                    manager.notify(n.getName(), mNotificationId, buildNotification(type, n));
                }
            }

            // Se eliminan los items que hay en la aplicacion pero no en el servidor
            if (state.get() == Utilities.SUCCESS_STATE) {
                dbController.delete(_IDs.toArray());

                for (String image : images) if (image != null) {
                    Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                }
            }
        }

        sendBroadcast(new Intent(ACTION_NEWS)
                .putExtra("INSERTED", inserted));
    }

    private void loadInformations() {
        Context context = getApplicationContext();

        if (Utilities.haveNetworkConnection(context)) {
            InformationsSQLiteController dbController =
                    new InformationsSQLiteController(context, 1);
            boolean updateInformations = false;
            ArrayList<InformationCategory> updatedCategories =
                    InformationsServiceController.getInformationCategories(context);

            for (InformationCategory category : updatedCategories) {
                ArrayList<InformationCategory> oldCategories = dbController.selectCategory(
                        InformationsSQLiteController.categoryColumns[0] + " = ?",
                        category.get_ID());

                if (oldCategories.size() == 0) {
                    updateInformations = true;
                    dbController.insertCategory(category.get_ID(), category.getName(),
                            category.getLink(), category.getDate());
                } else if (oldCategories.get(0).getDate().compareTo(category.getDate()) < 0) {
                    updateInformations = true;
                    dbController.updateCategory(category.get_ID(), category.getName(),
                            category.getLink(), category.getDate(), category.get_ID());
                }

                if (updateInformations){
                    ArrayList<Information> updatedInformations = InformationsServiceController
                            .getInformations(context, '/' + category.get_ID());

                    for (Information information : updatedInformations) {
                        ArrayList<Information> olds = dbController.select(
                                InformationsSQLiteController.columns[0] + " = ?",
                                information.get_ID());

                        if (!olds.isEmpty()) {
                            dbController.update(information.get_ID(), information.getCategory_ID(),
                                    information.getName(), information.getContent(),
                                    information.get_ID());
                        } else {
                            dbController.insert(information.get_ID(), information.getCategory_ID(),
                                    information.getName(), information.getContent());
                        }
                    }
                }
            }
        }

        sendBroadcast(new Intent(PENDING_ACTION));
    }

    private void loadContacts() {
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
        }

        sendBroadcast(new Intent(PENDING_ACTION));
    }

    private void loadPrograms() {
        ProgramsSQLiteController dbController =
                new ProgramsSQLiteController(getApplicationContext(), 1);

        ArrayList<ProgramCategory> oldCategories = dbController.selectCategory();

        if (oldCategories.size() == 0 && Utilities.haveNetworkConnection(getApplicationContext())) {
            for (ProgramCategory category :
                    ProgramsServiceController.getProgramCategories(getApplicationContext())) {
                dbController.insertCategory(category.get_ID(), category.getName());
            }

            for (ProgramFaculty faculty :
                    ProgramsServiceController.getProgramFaculties(getApplicationContext())) {
                dbController.insertFaculty(faculty.get_ID(), faculty.getName());
            }
        }

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            for (Program program : ProgramsServiceController.getPrograms(getApplicationContext())) {
                ArrayList<Program> olds = dbController.select(
                        ProgramsSQLiteController.columns[0]+" = ?", program.get_ID());

                if (olds.size() == 0) {
                    dbController.insert(program.get_ID(), program.getCategory_ID(),
                            program.getFaculty_ID(), program.getName(), program.getHistory(),
                            program.getHistoryLink(), program.getHistoryDate(),
                            program.getMissionVision(), program.getMissionVisionLink(),
                            program.getMissionVisionDate(), program.getCurriculum(),
                            program.getCurriculumLink(), program.getCurriculumDate(),
                            program.getProfiles(), program.getProfilesLink(),
                            program.getProfilesDate(), program.getContact());
                } else if (olds.get(0).getHistoryDate().compareTo(program.getHistoryDate()) < 0) {
                    dbController.partialUpdate(4, program.getHistory(),
                            program.getHistoryLink(), program.getHistoryDate(), program.get_ID());
                } else if (olds.get(0).getMissionVisionDate()
                        .compareTo(program.getMissionVisionDate()) < 0) {
                    dbController.partialUpdate(7, program.getMissionVision(),
                            program.getMissionVisionLink(), program.getMissionVisionDate(),
                            program.get_ID());
                } else if (olds.get(0).getCurriculumDate()
                        .compareTo(program.getCurriculumDate()) < 0) {
                    dbController.partialUpdate(10, program.getCurriculum(),
                            program.getCurriculumLink(), program.getCurriculumDate(),
                            program.get_ID());
                } else if (olds.get(0).getProfilesDate().compareTo(program.getProfilesDate()) < 0) {
                    dbController.partialUpdate(13, program.getProfiles(),
                            program.getProfilesLink(), program.getProfilesDate(), program.get_ID());
                }
            }
        }

        sendBroadcast(new Intent(PENDING_ACTION));
    }

    private void loadCalendar() {
        Context context = getApplicationContext();
        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        if (Utilities.haveNetworkConnection(context)) {
            ArrayList<EventDate> oldDates = dbController.selectDate(
                    EventsSQLiteController.dateColumns[1] + " = 'fechasPub'");
            ArrayList<EventDate> dates =
                    EventsServiceController.getEventDates(context, "/fechasPub");

            if (!oldDates.isEmpty() && !dates.isEmpty() &&
                    oldDates.get(0).getDate().compareTo(dates.get(0).getDate()) < 0) {
                dbController.deleteDate();
                dbController.deletePeriod();
                dbController.delete();
                dbController.deleteCategory();
                // No es nesesario eliminar Evento_Relacion porque ya tiene referecias
                // ON DELETE CASCADE a las tablas anteriores
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

            NotificationManager manager = "S".equals(
                    NotificationsPresenter.getNotification(context, "2").getActivated()) ?
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) : null;

            if (manager != null) {
                SimpleDateFormat datesFormat =
                        new SimpleDateFormat("d 'de' MMMM 'de' yyyy", locale);
                ArrayList<EventRelation> relations = dbController.selectRelation(
                        EventsSQLiteController.relationColumns, null);
                HashMap<String, Event> events = new HashMap<>();
                int inserted = 0;

                for (Event event : dbController.select(null)) {
                    events.put(event.get_ID(), event);
                }

                for (EventDate date : dbController.selectDate(null)) {
                    try {
                        if (!"fechasPub".equals(date.getType()) && notifyDate.equals(
                                notifyDateFormat.format(datesFormat.parse(date.getDate())))) {
                            for (EventRelation relation : relations) {
                                if (relation.getDate_ID().equals(date.get_ID())) {
                                    Event event = events.get(relation.getEvent_ID());

                                    if (event != null) {
                                        manager.notify(event.getName(), mNotificationId,
                                                buildNotification(ACTION_CALENDAR, event));
                                        if (++inserted >= 5) break;
                                    }
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

        sendBroadcast(new Intent(PENDING_ACTION));
    }

    /**
     * Permite insertar, actualizar o eliminar un anuncio en el servidor para luego actualizar los
     * anuncios de la base de datos local, o solamente actualizar los anuncios de la base de datos
     * local.
     * @param type El tipo de anuncios (incidentes o comunicados) en los cuales realizar la
     *             operación.
     * @param method El tipo de operación a realizar: insertar, actualizar, eliminar, o obtener.
     * @param object Cadena de texto con formato JSON para realizar una inserción, actualización o
     *               eliminación.
     */
    private void loadAnnouncements(String type, String method, String object) {
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
                } catch (JSONException | IOException e) {
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
                    notify = "S".equals(NotificationsPresenter
                            .getNotification(context, "4").getActivated());
                } else {
                    selectionArg = "C";
                    category_date = "/comunicados";
                    notify = "S".equals(NotificationsPresenter
                            .getNotification(context, "5").getActivated());
                }

                AnnouncementsSQLiteController dbController =
                        new AnnouncementsSQLiteController(context, 1);

                ArrayList<String> _IDs = new ArrayList<>();
                ArrayList<Announcement> announcements = dbController.select(null,
                        AnnouncementsSQLiteController.columns[2]+" = ?", selectionArg);
                for (Announcement announcement : announcements) _IDs.add(announcement.get_ID());
                if (_IDs.isEmpty()) notify = false;
                else category_date += '/'+announcements.get(0).getDate();
                State state = new State(Utilities.FAILURE_STATE);
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
                                    link.getType(), Utilities.saveMedia(link.getLink(),
                                            "/announcements", context));
                        } else {
                            links_IDs.remove(linkIndex);
                            String linkLink = linksLinks.remove(index);

                            if (linkLink != null) {
                                Log.i(TAG, "delete " + linkLink + ": " +
                                        new File(linkLink).delete());
                            }

                            dbController.updateLink(link.get_ID(), link.getAnnouncement_ID(),
                                    link.getType(), Utilities.saveMedia(link.getLink(),
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
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_INCIDENTS)
                .putExtra("INSERTED", inserted)
                .putExtra("RESPONSE", response));
    }

    /**
     * Permite insertar, actualizar o eliminar un objeto perdido en el servidor para luego
     * actualizar los objetos perdidos de la base de datos local, o solamente actualizar los objetos
     * perdidos de la base de datos
     * local.
     * @param method El tipo de operación a realizar: insertar, actualizar, eliminar, o obtener.
     * @param object Cadena de texto con formato JSON para realizar una inserción, actualización o
     *               eliminación.
     */
    private void loadObjects(String method, String object) {
        Context context = getApplicationContext();
        String response = null;
        int inserted = 0;

        if (Utilities.haveNetworkConnection(context)) switch (method) {
            case METHOD_POST:
            case METHOD_PUT:
            case METHOD_DELETE:
                try {
                    response = ObjectsServiceController
                            .modifyObject(context, encodeImageString(new JSONObject(object)));
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                // Despues de modificar se actualiza, por eso aqui no hay break
            case METHOD_GET: {
                ObjectsSQLiteController dbController =
                        new ObjectsSQLiteController(context, 1);

                String date;
                State state = new State(Utilities.FAILURE_STATE);
                ArrayList<String> _IDs = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();

                {
                    ArrayList<LostObject> objects = dbController.select(null, null);
                    date = objects.isEmpty() ? "" : '/' + objects.get(0).getDate();

                    for (LostObject lostObject : objects) {
                        _IDs.add(lostObject.get_ID());
                        images.add(lostObject.getImage());
                    }
                }

                NotificationManager manager = "S".equals(NotificationsPresenter
                        .getNotification(context, "3").getActivated()) && !_IDs.isEmpty() ?
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) : null;

                for (LostObject obj :
                        ObjectsServiceController.getObjects(context, date, state, _IDs, images)) {
                    int index = _IDs.indexOf(obj.get_ID());

                    if (index == -1) {
                        dbController.insert(obj.get_ID(), obj.getUserLost_ID(), obj.getName(),
                                obj.getPlace(), obj.getDateLost(), obj.getDate(),
                                obj.getDescription(), Utilities.saveMedia(obj.getImage(),
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
                                obj.getDescription(), Utilities.saveMedia(obj.getImage(),
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
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_OBJECTS)
                .putExtra("INSERTED", inserted)
                .putExtra("RESPONSE", response));
    }

    /**
     * Permite insertar, actualizar o eliminar un plato en el servidor para luego actualizar los
     * platos de la base de datos local, o solamente actualizar los platos de la base de datos
     * local.
     * @param method El tipo de operación a realizar: insertar, actualizar, eliminar, o obtener.
     * @param object Cadena de texto con formato JSON para realizar una inserción, actualización o
     *               eliminación.
     */
    private void loadDishes(String method, String object) {
        Context context = getApplicationContext();
        String response = null;
        int inserted = 0;

        if (Utilities.haveNetworkConnection(context)) switch (method) {
            case METHOD_POST:
            case METHOD_PUT:
            case METHOD_DELETE:
                try {
                    response = DishesServiceController
                            .modifyDish(context, encodeImageString(new JSONObject(object)));
                } catch (JSONException | IOException  e) {
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
                                dish.getPrice(), Utilities.saveMedia(dish.getImage(),
                                        "/dishes", context));
                        inserted++;
                    } else {
                        _IDs.remove(index);
                        String image = images.remove(index);

                        if (image != null) {
                            Log.i(TAG, "delete " + image + ": " + new File(image).delete());
                        }

                        dbController.update(dish.get_ID(), dish.getName(), dish.getDescription(),
                                dish.getPrice(), Utilities.saveMedia(dish.getImage(),
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
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_DISHES)
                .putExtra("INSERTED", inserted)
                .putExtra("RESPONSE", response));
    }

    private static String encodeImageString(JSONObject json) throws JSONException, IOException {
        if (!json.isNull("imageString")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Utilities.copy(new FileInputStream(json.getString("imageString")),
                    new Base64OutputStream(byteArrayOutputStream, Base64.NO_WRAP));
            json.put("imageString", byteArrayOutputStream.toString());
        }

        return json.toString();
    }

    /**
     * Permite insertar, actualizar o eliminar un cupo en el servidor para luego actualizar los
     * cupos de la base de datos local, o solamente actualizar los cupos de la base de datos local.
     * @param method El tipo de operación a realizar: insertar, actualizar, eliminar, o obtener.
     * @param object Cadena de texto con formato JSON para realizar una inserción, actualización o
     *               eliminación.
     */
    private void loadQuotas(String method, String object) {
        Context context = getApplicationContext();
        String response = null;

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
                        _IDs.remove(index);
                        dbController.update(quota.get_ID(), quota.getType(), quota.getName(),
                                quota.getQuota(), quota.get_ID());
                    }
                }

                // Se eliminan los items que hay en la aplicacion pero no en el servidor
                if (!quotas.isEmpty()) dbController.delete(_IDs.toArray());
                break;
            }
            default:
                break;
        }

        sendBroadcast(new Intent(ACTION_QUOTAS)
                .putExtra("RESPONSE", response));
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
                        if (!"campusuq@uniquindio.edu.co".equals(u.getEmail())) {
                            dbController.delete(u.get_ID());
                        }
                    }
                    break;
                case METHOD_GET:
                    user = UsersServiceController.getUser(object);

                    if (user != null && user.getPassword() != null && user.getApiKey() != null &&
                            user.getAdministrator() != null) {
                        dbController.insert(user.get_ID(), user.getName(), user.getEmail(),
                                user.getPhone(), user.getAddress(), user.getDocument(),
                                user.getPassword(), user.getApiKey(), user.getAdministrator());
                    }
                    break;
            }
        }

        sendBroadcast(new Intent(ACTION_USERS)
                .putExtra("USER", user));
    }

    private void loadEmails(String method, String object) {
        Intent intent = null;
        int inserted = 0;

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            switch (method) {
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
                    User user = UsersPresenter.loadUser(getApplicationContext());

                    if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail())) {
                        EmailsSQLiteController dbController =
                                new EmailsSQLiteController(getApplicationContext(), 1);

                        ArrayList<String> oldIDs = new ArrayList<>();
                        ArrayList<Email> olds = dbController.select("50");
                        for (Email old : olds) oldIDs.add(old.get_ID());

                        NotificationManager manager = "S".equals(NotificationsPresenter
                                .getNotification(getApplicationContext(), "6")
                                .getActivated()) && !oldIDs.isEmpty() ? (NotificationManager)
                                getSystemService(Context.NOTIFICATION_SERVICE) : null;

                        try {
                            for (Email email : EmailsServiceController.getEmails(
                                    getApplicationContext(),
                                    !olds.isEmpty() ? olds.get(0).getHistoryID() : null)) {
                                int index = oldIDs.indexOf(email.get_ID());

                                if (index == -1) {
                                    oldIDs.add(email.get_ID());
                                    dbController.insert(email.get_ID(), email.getName(),
                                            email.getFrom(), email.getTo(), email.getDate(),
                                            email.getSnippet(), email.getContent(),
                                            "" + email.getHistoryID());

                                    if (++inserted <= 5 && manager != null) {
                                        manager.notify(email.getName(), mNotificationId,
                                                buildNotification(ACTION_EMAILS, email));
                                    }
                                }
                            }
                        } catch (UserRecoverableAuthIOException e) {
                            intent = e.getIntent();
                        }
                    }
                    break;
            }
        }

        sendBroadcast(new Intent(ACTION_EMAILS)
                .putExtra("INSERTED", inserted)
                .putExtra("INTENT", intent));
    }

    @Override
    public void onDestroy() {
        Log.i(WebService.class.getSimpleName(), "Service destroyed!");
        stopForeground(true);
        super.onDestroy();
    }

}
