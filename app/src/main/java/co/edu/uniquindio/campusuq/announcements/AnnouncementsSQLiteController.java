package co.edu.uniquindio.campusuq.announcements;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsSQLiteController {

    private static final String tablename = "Anuncio";
    public static final String columns[] = {"_ID", "Usuario_ID", "Tipo", "Nombre", "Fecha",
            "Descripcion", "Leido"};

    private static final String linkTablename = "Anuncio_Enlace";
    public static final String linkColumns[] = {"_ID", "Anuncio_ID", "Tipo", "Enlace"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public AnnouncementsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " INTEGER NOT NULL, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " TEXT NOT NULL, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " INTEGER NOT NULL)";
    }

    public ArrayList<Announcement> select(String limit, String selection, String... selectionArgs) {
        ArrayList<Announcement> announcements = new ArrayList<>();

        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[4] + " DESC", limit);
        if (c.moveToFirst()) do {
            announcements.add(new Announcement(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3), c.getString(4), c.getString(5),
                    c.getString(6)));
        } while (c.moveToNext());
        c.close();

        return announcements;
    }

    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    public void update(Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " + TextUtils.join(" = ?, ",
                Arrays.copyOfRange(columns, 0, columns.length - 1)) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    void readed(Object... ids) {
        db.execSQL("UPDATE " + tablename + " SET " +
                columns[6] + " = 1 WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    void unreadAll() {
        db.execSQL("UPDATE " + tablename + " SET " + columns[6] + " = 0");
    }

    public void delete(Object... ids) {
        db.execSQL("DELETE FROM " + tablename + " WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    public static String createLinkTable(){
        return "CREATE TABLE " + linkTablename + '(' + linkColumns[0] + " INTEGER PRIMARY KEY, " +
                linkColumns[1] + " INTEGER NOT NULL REFERENCES " +
                tablename + '(' + columns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                linkColumns[2] + " TEXT NOT NULL, " + linkColumns[3] + " TEXT UNIQUE)";
    }

    public ArrayList<AnnouncementLink> selectLink(String selection, String... selectionArgs) {
        ArrayList<AnnouncementLink> links = new ArrayList<>();

        Cursor c = db.query(linkTablename, null, selection, selectionArgs, null,
                null, linkColumns[0] + " ASC");
        if (c.moveToFirst()) do {
            links.add(new AnnouncementLink(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3)));
        } while (c.moveToNext());
        c.close();

        return links;
    }

    public void insertLink(Object... values) {
        db.execSQL("INSERT INTO " + linkTablename + '(' +
                TextUtils.join(", ", linkColumns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(linkColumns.length, '?')) +
                ')', values);
    }

    public void updateLink(Object... values) {
        db.execSQL("UPDATE " + linkTablename + " SET " +
                TextUtils.join(" = ?, ", linkColumns) + " = ? WHERE " +
                linkColumns[0] + " = ?", values);
    }

    public void deleteLink(Object... ids) {
        db.execSQL("DELETE FROM " + linkTablename + " WHERE " + linkColumns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    public void destroy() {
        usdbh.close();
    }

}
