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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.AnnouncementsActivity;
import co.edu.uniquindio.campusuq.activity.NewsActivity;
import co.edu.uniquindio.campusuq.activity.ObjectsActivity;
import co.edu.uniquindio.campusuq.vo.Announcement;
import co.edu.uniquindio.campusuq.vo.AnnouncementLink;
import co.edu.uniquindio.campusuq.vo.Contact;
import co.edu.uniquindio.campusuq.vo.ContactCategory;
import co.edu.uniquindio.campusuq.vo.Dish;
import co.edu.uniquindio.campusuq.vo.Email;
import co.edu.uniquindio.campusuq.vo.Event;
import co.edu.uniquindio.campusuq.vo.EventCategory;
import co.edu.uniquindio.campusuq.vo.EventDate;
import co.edu.uniquindio.campusuq.vo.EventPeriod;
import co.edu.uniquindio.campusuq.vo.EventRelation;
import co.edu.uniquindio.campusuq.vo.Information;
import co.edu.uniquindio.campusuq.vo.InformationCategory;
import co.edu.uniquindio.campusuq.vo.LostObject;
import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;
import co.edu.uniquindio.campusuq.vo.Program;
import co.edu.uniquindio.campusuq.vo.ProgramCategory;
import co.edu.uniquindio.campusuq.vo.ProgramFaculty;
import co.edu.uniquindio.campusuq.vo.Quota;
import co.edu.uniquindio.campusuq.vo.User;

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
    public static final String ACTION_CALENDAR = "co.edu.uniquindio.campusuq.ACTION_CALENDAR";
    public static final String ACTION_INCIDENTS = "co.edu.uniquindio.campusuq.ACTION_INCIDENTS";
    public static final String ACTION_COMMUNIQUES = "co.edu.uniquindio.campusuq.ACTION_COMMUNIQUES";
    public static final String ACTION_OBJECTS = "co.edu.uniquindio.campusuq.ACTION_OBJECTS";
    public static final String ACTION_DISHES = "co.edu.uniquindio.campusuq.ACTION_DISHES";
    public static final String ACTION_QUOTAS = "co.edu.uniquindio.campusuq.ACTION_QUOTAS";
    public static final String ACTION_EMAILS = "co.edu.uniquindio.campusuq.ACTION_EMAILS";
    public static final String ACTION_USERS = "co.edu.uniquindio.campusuq.ACTION_USERS";

    public static final String METHOD_GET = "co.edu.uniquindio.campusuq.METHOD_GET";
    public static final String METHOD_POST = "co.edu.uniquindio.campusuq.METHOD_POST";
    public static final String METHOD_PUT = "co.edu.uniquindio.campusuq.METHOD_PUT";
    public static final String METHOD_DELETE = "co.edu.uniquindio.campusuq.METHOD_DELETE";

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
                try {
                    User user = UsersPresenter.loadUser(getApplicationContext());
                    if (Utilities.haveNetworkConnection(getApplicationContext())) {
                        if (user == null) {
                            JSONObject json = new JSONObject();
                            try {
                                json.put(UsersSQLiteController.columns[2], "campusuq@uniquindio.edu.co");
                                json.put(UsersSQLiteController.columns[6], "campusuq");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            loadUsers(METHOD_GET, json.toString());
                        }
                        doWork(jobParameters);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception in Job");
                    e.printStackTrace();
                    boolean needsReschedule = true;
                    jobFinished(jobParameters, needsReschedule);
                }
            }
        }).start();
    }

    private void doWork(JobParameters params) {
        Log.i(TAG, "entrando a doWork");
        String action = params.getExtras().getString("ACTION");
        String method = params.getExtras().getString("METHOD");
        String object = params.getExtras().getString("OBJECT");
        switch (action) {
            case ACTION_ALL:
                loadInformations();
                loadContacts();
                loadPrograms();
                loadCalendar();
                loadNews(ACTION_EVENTS);
                loadNews(ACTION_NEWS);
                loadAnnouncements(ACTION_INCIDENTS);
                loadAnnouncements(ACTION_COMMUNIQUES);
                loadObjects(method, object);
                loadDishes(method, object);
                loadQuotas(method, object);
                loadEmails(method, object);
                break;
            case ACTION_EVENTS:
                loadNews(ACTION_EVENTS);
                break;
            case ACTION_NEWS:
                loadNews(ACTION_NEWS);
                break;
            case ACTION_INCIDENTS:
                loadAnnouncements(ACTION_INCIDENTS);
                break;
            case ACTION_COMMUNIQUES:
                loadAnnouncements(ACTION_COMMUNIQUES);
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
        if (jobCancelled)
            return;

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
                file = new File(lostObject.getImage());
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
                AnnouncementsSQLiteController dbController = new AnnouncementsSQLiteController(getApplicationContext(), 1);
                ArrayList<AnnouncementLink> links = dbController.selectLink(
                        AnnouncementsSQLiteController.linkColumns[1] + " = ?", new String[]{String.valueOf(announcement.get_ID())});
                dbController.destroy();
                file = links.size() > 0 ? new File(links.get(0).getLink()) : new File("");
                break;
            default:
                break;
        }

        if (file.exists()) {
            builder.setLargeIcon(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    getApplicationContext().getResources(), R.drawable.app_icon));
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
            default:
                break;
        }
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

    private void loadNews(String type) {

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

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

        int inserted = 0;
        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            categories = NewsServiceController.getNewCategories(getApplicationContext());
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
            ArrayList<New> updated = NewsServiceController.getNews(getApplicationContext(), lastNewId);
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
                    relations = NewsServiceController.getNewRelations(getApplicationContext(), mNew.get_ID());
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

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        InformationsSQLiteController dbController = new InformationsSQLiteController(getApplicationContext(), 1);

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

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        ContactsSQLiteController dbController = new ContactsSQLiteController(getApplicationContext(), 1);

        ArrayList<ContactCategory> oldCategories = dbController.selectCategory(null, null);
        if (oldCategories.size() == 0 && Utilities.haveNetworkConnection(getApplicationContext())) {

            ArrayList<ContactCategory> updatedCategories = ContactsServiceController.getContactCategories(getApplicationContext());
            for (ContactCategory category : updatedCategories) {
                dbController.insertCategory(category.get_ID(), category.getName(), category.getLink());
            }

            ArrayList<Contact> updatedContacts = ContactsServiceController.getContacts(getApplicationContext());
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

        Intent intent = new Intent(PENDING_ACTION);
        sendBroadcast(intent);

    }

    private void loadCalendar() {

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        EventsSQLiteController dbController = new EventsSQLiteController(getApplicationContext(), 1);

        ArrayList<EventDate> oldDates = dbController.selectDate(
                EventsSQLiteController.CAMPOS_FECHA[1]+" = ?", new String[]{"fechasPub"});
        if (oldDates.size() > 0 && Utilities.haveNetworkConnection(getApplicationContext())) {
            ArrayList<EventDate> updatedDates = EventsServiceController.getEventDates(getApplicationContext());
            for (EventDate date : updatedDates) {
                if (date.getType().equals("fechasPub")) {
                    if (oldDates.get(0).getDate().compareTo(date.getDate()) < 0) {
                        dbController.deleteRelation();
                        dbController.deleteDate();
                        dbController.deletePeriod();
                        dbController.delete();
                        dbController.deleteCategory();
                    }
                    break;
                }
            }
        }

        ArrayList<EventCategory> oldCategories = dbController.selectCategory(null, null);
        if (oldCategories.size() == 0 && Utilities.haveNetworkConnection(getApplicationContext())) {

            ArrayList<EventCategory> updatedCategories = EventsServiceController.getEventCategories(getApplicationContext());
            for (EventCategory category : updatedCategories) {
                dbController.insertCategory(category.get_ID(), category.getAbbreviation(), category.getName());
            }

            ArrayList<Event> updatedEvents = EventsServiceController.getEvents(getApplicationContext());
            for (Event event : updatedEvents) {
                dbController.insert(event.get_ID(), event.getName());
            }

            ArrayList<EventPeriod> updatedPeriods = EventsServiceController.getEventPeriods(getApplicationContext());
            for (EventPeriod period : updatedPeriods) {
                dbController.insertPeriod(period.get_ID(), period.getName());
            }

            ArrayList<EventDate> updatedDates = EventsServiceController.getEventDates(getApplicationContext());
            for (EventDate date : updatedDates) {
                dbController.insertDate(date.get_ID(), date.getType(), date.getDate());
            }

            ArrayList<EventRelation> updatedRelations = EventsServiceController.getEventRelations(getApplicationContext());
            for (EventRelation relation : updatedRelations) {
                dbController.insertRelation(relation.getCategory_ID(), relation.getEvent_ID(),
                        relation.getPeriod_ID(), relation.getDate_ID());
            }

        }

        dbController.destroy();

        Intent intent = new Intent(PENDING_ACTION);
        sendBroadcast(intent);

    }

    private void loadAnnouncements(String type) {

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        AnnouncementsSQLiteController dbController = new AnnouncementsSQLiteController(getApplicationContext(), 1);
        String validRows = null;
        String lastAnnouncementId = null;
        boolean notify = true;

        if (ACTION_INCIDENTS.equals(type)) {
            validRows = "I";
            lastAnnouncementId = "/incidentes";
        } else {
            validRows = "C";
            lastAnnouncementId = "/comunicados";
        }

        int inserted = 0;
        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            ArrayList<Announcement> lastAnnouncements = dbController.select("1",
                    AnnouncementsSQLiteController.columns[1] + " = ?", new String[]{validRows});
            if (lastAnnouncements.size() > 0) {
                lastAnnouncementId += "/"+lastAnnouncements.get(0).get_ID();
            } else {
                notify = false;
            }

            ArrayList<Integer> IDs = new ArrayList<>();
            ArrayList<Announcement> olds = dbController.select(null, null, null);
            for (Announcement announcement : olds) {
                IDs.add(announcement.get_ID());
            }

            ArrayList<Announcement> updated =
                    AnnouncementsServiceController.getAnnouncements(getApplicationContext(), lastAnnouncementId);
            for (Announcement announcement : updated) {
                boolean insert = true;
                int index = IDs.indexOf(announcement.get_ID());
                if (index != -1) {
                    if (olds.get(index).getDate().compareTo(announcement.getDate()) < 0) {
                        dbController.deleteLink(announcement.get_ID());
                        dbController.delete(announcement.get_ID());
                    } else {
                        insert = false;
                    }
                    IDs.remove(index);
                }
                if (insert) {
                    dbController.insert(announcement.get_ID(), announcement.getUser_ID(), announcement.getType(),
                            announcement.getName(), announcement.getDate(), announcement.getDescription(), announcement.getRead());
                    ArrayList<AnnouncementLink> links =
                            AnnouncementsServiceController.getAnnouncementLinks(getApplicationContext(), String.valueOf(announcement.get_ID()));
                    for (AnnouncementLink link : links) {
                        String imagePath = Utilities.saveImage(link.getLink(), getApplicationContext());
                        if (imagePath != null) {
                            link.setLink(imagePath);
                        }
                        dbController.insertLink(link.get_ID(), link.getAnnouncement_ID(), link.getType(), link.getLink());
                    }
                    inserted += 1;
                    if (notify && inserted <= 5) {
                        manager.notify(announcement.getName(), mNotificationId, buildNotification(type, announcement));
                    }
                }
            }

            if (updated.size() > 0) dbController.delete(IDs.toArray());

        }

        dbController.destroy();

        Intent intent = new Intent(ACTION_INCIDENTS);
        intent.putExtra("INSERTED", inserted);
        sendBroadcast(intent);

    }

    private void loadObjects(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        int inserted = 0;
        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            ObjectsSQLiteController dbController = new ObjectsSQLiteController(getApplicationContext(), 1);
            switch (method) {
            case METHOD_POST:
            case METHOD_PUT:
            case METHOD_DELETE:
                ObjectsServiceController.modifyObject(getApplicationContext(), object);
            case METHOD_GET:
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                boolean remove = false;
                ArrayList<Integer> oldIDs = new ArrayList<>();
                for (LostObject old : dbController.select(null, null, null)) oldIDs.add(old.get_ID());
                String lastObjectId = oldIDs.size() > 0 ? "/"+oldIDs.get(0) : null;
                for (LostObject lostObject : ObjectsServiceController.getObjects(getApplicationContext(), lastObjectId)) {
                    remove = true;
                    String imagePath = Utilities.saveImage(lostObject.getImage(), getApplicationContext());
                    if (imagePath != null) lostObject.setImage(imagePath);
                    int index = oldIDs.indexOf(lostObject.get_ID());
                    if (index == -1) {
                        dbController.insert(
                            lostObject.get_ID(), lostObject.getUserLost_ID(), lostObject.getName(),
                                lostObject.getPlace(), lostObject.getDate(), lostObject.getDescription(),
                                lostObject.getImage(), lostObject.getUserFound_ID(), lostObject.getReaded());
                    } else {
                        dbController.update(
                            lostObject.get_ID(), lostObject.getUserLost_ID(), lostObject.getName(),
                                lostObject.getPlace(), lostObject.getDate(), lostObject.getDescription(),
                                lostObject.getImage(), lostObject.getUserFound_ID(), lostObject.get_ID());
                        oldIDs.remove(index);
                    }
                    inserted ++;
                    if (lastObjectId != null && inserted <= 5) {
                        manager.notify(lostObject.getName(), mNotificationId, buildNotification(ACTION_OBJECTS, lostObject));
                    }
                }
                if (remove) dbController.delete(oldIDs.toArray());
                break;
            }
            dbController.destroy();
        }
        sendBroadcast(new Intent(ACTION_OBJECTS).putExtra("INSERTED", inserted));
    }

    private void loadDishes(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        int inserted = 0;
        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            DishesSQLiteController dbController = new DishesSQLiteController(getApplicationContext(), 1);
            switch (method) {
                case METHOD_POST:
                case METHOD_PUT:
                case METHOD_DELETE:
                    DishesServiceController.modifyDish(getApplicationContext(), object);
                case METHOD_GET:
                    boolean remove = false;
                    ArrayList<Integer> oldIDs = new ArrayList<>();
                    for (Dish old : dbController.select(null, null, null)) oldIDs.add(old.get_ID());
                    for (Dish dish : DishesServiceController.getDishes(getApplicationContext())) {
                        remove = true;
                        String imagePath = Utilities.saveImage(dish.getImage(), getApplicationContext());
                        if (imagePath != null) dish.setImage(imagePath);
                        int index = oldIDs.indexOf(dish.get_ID());
                        if (index == -1) {
                            dbController.insert(dish.get_ID(), dish.getName(), dish.getDescription(),
                                    dish.getPrice(), dish.getImage());
                            inserted ++;
                        } else {
                            dbController.update(dish.get_ID(), dish.getName(), dish.getDescription(),
                                    dish.getPrice(), dish.getImage(), dish.get_ID());
                            oldIDs.remove(index);
                        }
                    }
                    if (remove) dbController.delete(oldIDs.toArray());
                    break;
            }
            dbController.destroy();
        }
        sendBroadcast(new Intent(ACTION_DISHES).putExtra("INSERTED", inserted));
    }

    private void loadQuotas(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            QuotasSQLiteController dbController = new QuotasSQLiteController(getApplicationContext(), 1);
            switch (method) {
                case METHOD_POST:
                case METHOD_PUT:
                case METHOD_DELETE:
                    QuotasServiceController.modifyQuota(getApplicationContext(), object);
                case METHOD_GET:
                    boolean remove = false;
                    ArrayList<Integer> oldIDs = new ArrayList<>();
                    for (Quota old : dbController.select(null, null)) oldIDs.add(old.get_ID());
                    for (Quota quota : QuotasServiceController.getQuotas(getApplicationContext())) {
                        remove = true;
                        int index = oldIDs.indexOf(quota.get_ID());
                        if (index == -1) {
                            dbController.insert(quota.get_ID(), quota.getType(), quota.getName(), quota.getQuota());
                        } else {
                            dbController.update(quota.get_ID(), quota.getType(), quota.getName(),
                                    quota.getQuota(), quota.get_ID());
                            oldIDs.remove(index);
                        }
                    }
                    if (remove) dbController.delete(oldIDs.toArray());
                    break;
            }
            dbController.destroy();
        }
        sendBroadcast(new Intent(ACTION_QUOTAS));
    }

    private void loadUsers(String method, String object) {
        User user = null;
        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            UsersSQLiteController dbController = new UsersSQLiteController(getApplicationContext(), 1);
            switch (method) {
                case METHOD_POST:
                case METHOD_PUT:
                    UsersServiceController.modifyUser(object);
                case METHOD_DELETE:
                    ArrayList<User> users = dbController.select(null, null);
                    for (User u : users) {
                        if (!u.getEmail().equals("campusuq@uniquindio.edu.co")) {
                            dbController.delete(u.get_ID());
                        }
                    }
                    break;
                case METHOD_GET:
                    user = UsersServiceController.login(object);
                    if (user != null) {
                        dbController.insert(user.get_ID(), user.getName(), user.getEmail(), user.getPhone(),
                                user.getAddress(), user.getDocument(), user.getPassword(), user.getApiKey(),
                                user.getAdministrator());
                    }
                    break;
            }
            dbController.destroy();
        }
        sendBroadcast(new Intent(ACTION_USERS).putExtra("USER", user));
    }

    private void loadEmails(String method, String object) {
        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        int inserted = 0;
        if (Utilities.haveNetworkConnection(getApplicationContext())) {
            EmailsSQLiteController dbController = new EmailsSQLiteController(getApplicationContext(), 1);
            switch(method) {
                case METHOD_POST:
                case METHOD_PUT:
                case METHOD_DELETE:
                    //EmailsServiceController.modify(object);
                case METHOD_GET:
                    User user = UsersPresenter.loadUser(getApplicationContext());
                    if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                        ArrayList<Integer> oldIDs = new ArrayList<>();
                        ArrayList<Email> oldEmails = dbController.select("50", null, null);
                        for (Email old : oldEmails) oldIDs.add(old.get_ID());
                        for (Email email : EmailsServiceController.getEmails(getApplicationContext(),
                                oldEmails.get(0) != null ? oldEmails.get(0).getHistoryID() : null)) {
                            int index = oldIDs.indexOf(email.get_ID());
                            if (index == -1) {
                                dbController.insert(email.get_ID(), email.getName(), email.getFrom(), email.getTo(),
                                        email.getDate(), email.getContent(), ""+email.getHistoryID());
                                inserted ++;
                            }
                        }
                    }
                    break;
            }
            dbController.destroy();
        }
        sendBroadcast(new Intent(ACTION_EMAILS).putExtra("INSERTED", inserted));
    }

    @Override
    public void onDestroy() {
        Log.i(WebBroadcastReceiver.class.getSimpleName(), "destruyendo el web service");
        super.onDestroy();
    }

}
