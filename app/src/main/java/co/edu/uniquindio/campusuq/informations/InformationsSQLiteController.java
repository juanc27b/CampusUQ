package co.edu.uniquindio.campusuq.informations;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 21/02/2018.
 */

public class InformationsSQLiteController {

    private static final String tablename = "Informacion";
    public static final String columns[] = {"_ID", "Categoria_ID", "Nombre", "Contenido"};

    private static final String categoryTablename = "Informacion_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace", "Fecha"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public InformationsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " INTEGER NOT NULL REFERENCES " + categoryTablename + '(' +
                categoryColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                columns[2] + " TEXT NOT NULL UNIQUE, " + columns[3] + " TEXT NOT NULL)";
    }

    public ArrayList<Information> select(String selection, String... selectionArgs) {
        ArrayList<Information> informations = new ArrayList<>();

        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[0] + " ASC");
        if (c.moveToFirst()) do {
            informations.add(new Information(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3)));
        } while (c.moveToNext());
        c.close();

        return informations;
    }

    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    public void update(Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " +
                TextUtils.join(" = ?, ", columns) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    public void delete(Object... ids) {
        db.execSQL("DELETE FROM " + tablename + " WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE, " +
                categoryColumns[2] + " TEXT NOT NULL, " +
                categoryColumns[3] + " TEXT NOT NULL)";
    }

    public ArrayList<InformationCategory> selectCategory(String selection,
                                                         String... selectionArgs) {
        ArrayList<InformationCategory> categories = new ArrayList<>();

        Cursor c = db.query(categoryTablename, null, selection, selectionArgs,
                null, null, null);
        if (c.moveToFirst()) do {
            categories.add(new InformationCategory(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3)));
        } while (c.moveToNext());
        c.close();

        return categories;
    }

    public void insertCategory(Object... values) {
        db.execSQL("INSERT INTO " + categoryTablename + '(' +
                TextUtils.join(", ", categoryColumns) + ") VALUES(" +
                TextUtils.join(", ",
                        Collections.nCopies(categoryColumns.length, '?')) + ')', values);
    }

    public void updateCategory(Object... values) {
        db.execSQL("UPDATE " + categoryTablename + " SET " +
                TextUtils.join(" = ?, ", categoryColumns) + " = ? WHERE " +
                categoryColumns[0] + " = ?", values);
    }

    public void destroy() {
        usdbh.close();
    }

}
