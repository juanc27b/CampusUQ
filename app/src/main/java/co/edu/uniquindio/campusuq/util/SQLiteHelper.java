package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    String controller;

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, String controller) {
        super(context, name, factory, version);
        this.controller = controller;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        switch(controller) {
            case "NewsSQLiteController":
                db.execSQL(NewsSQLiteController.createTable());
                db.execSQL(NewsSQLiteController.createCategoryTable());
                db.execSQL(NewsSQLiteController.createRelationTable());
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
