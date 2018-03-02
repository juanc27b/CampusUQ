package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
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

        db.execSQL(QuotasSQLiteController.createTable());
        db.execSQL(DishesSQLiteController.createTable());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
