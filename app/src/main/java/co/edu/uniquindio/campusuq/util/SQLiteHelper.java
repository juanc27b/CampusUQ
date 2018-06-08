package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import co.edu.uniquindio.campusuq.announcements.AnnouncementsSQLiteController;
import co.edu.uniquindio.campusuq.contacts.ContactsSQLiteController;
import co.edu.uniquindio.campusuq.dishes.DishesSQLiteController;
import co.edu.uniquindio.campusuq.emails.EmailsSQLiteController;
import co.edu.uniquindio.campusuq.events.EventsSQLiteController;
import co.edu.uniquindio.campusuq.informations.InformationsSQLiteController;
import co.edu.uniquindio.campusuq.news.NewsSQLiteController;
import co.edu.uniquindio.campusuq.notifications.NotificationsSQLiteController;
import co.edu.uniquindio.campusuq.objects.ObjectsSQLiteController;
import co.edu.uniquindio.campusuq.programs.ProgramsSQLiteController;
import co.edu.uniquindio.campusuq.quotas.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.users.UsersSQLiteController;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteDatabase db;

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    static SQLiteDatabase getDatabaseInstance(Context context, int version) {
        if (db == null) {
            db = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version)
                    .getWritableDatabase();
        }

        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NewsSQLiteController.createTable());
        db.execSQL(NewsSQLiteController.createCategoryTable());
        db.execSQL(NewsSQLiteController.createRelationTable());

        db.execSQL(InformationsSQLiteController.createCategoryTable());
        db.execSQL(InformationsSQLiteController.createTable());

        db.execSQL(ContactsSQLiteController.createCategoryTable());
        db.execSQL(ContactsSQLiteController.createTable());

        db.execSQL(ProgramsSQLiteController.createCategoryTable());
        db.execSQL(ProgramsSQLiteController.createFacultyTable());
        db.execSQL(ProgramsSQLiteController.createTable());

        db.execSQL(EventsSQLiteController.createCategoryTable());
        db.execSQL(EventsSQLiteController.createTable());
        db.execSQL(EventsSQLiteController.createPeriodTable());
        db.execSQL(EventsSQLiteController.createDateTable());
        db.execSQL(EventsSQLiteController.createRelationTable());

        db.execSQL(AnnouncementsSQLiteController.createTable());
        db.execSQL(AnnouncementsSQLiteController.createLinkTable());

        db.execSQL(ObjectsSQLiteController.createTable());

        db.execSQL(DishesSQLiteController.createTable());

        db.execSQL(QuotasSQLiteController.createTable());

        db.execSQL(UsersSQLiteController.createTable());

        db.execSQL(EmailsSQLiteController.createTable());

        db.execSQL(NotificationsSQLiteController.createTable());
        db.execSQL(NotificationsSQLiteController.createDetailTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
