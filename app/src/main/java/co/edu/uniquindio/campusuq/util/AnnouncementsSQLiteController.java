package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Announcement;
import co.edu.uniquindio.campusuq.vo.AnnouncementLink;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsSQLiteController {

    public static final String NOMBRE_TABLA = "Anuncio";
    public static final String CAMPOS_TABLA[] = new String[]{"_ID", "Tipo", "Nombre", "Fecha", "Descripcion", "Leido"};

    public static final String NOMBRE_ENLACE = "Anuncio_Enlace";
    public static final String CAMPOS_ENLACE[] = new String[]{"_ID", "Anuncio_ID", "Tipo", "Enlace"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public AnnouncementsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? TEXT NOT NULL, " +
                "? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[4]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[5]);
        return builder.toString();
    }

    public ArrayList<Announcement> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<Announcement> announcements = new ArrayList<>();
        Cursor c = db.query(NOMBRE_TABLA, CAMPOS_TABLA, selection, selectionArgs,
                null, null, CAMPOS_TABLA[3]+" DESC", limit);
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String type = c.getString(1);
                String name = c.getString(2);
                String date = c.getString(3);
                String description = c.getString(4);
                String read = c.getString(5);
                Announcement announcement = new Announcement(id, type, name, date, description, read);
                announcements.add(announcement);
            } while (c.moveToNext());
        }
        c.close();
        return announcements;
    }

    public void insert(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?,?,?,?) VALUES (?,?,?,?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[4]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[5]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2],
                campos[3],
                campos[4],
                campos[5]
        });
    }

    public void update(boolean all, String... campos) {
        String update = "UPDATE "+NOMBRE_TABLA+" SET ?=? WHERE ? = ?";
        StringBuilder builder = new StringBuilder(update);
        int offset = builder.indexOf("?");
        builder.replace(offset, offset+1, CAMPOS_TABLA[5]);
        if (!all) {
            offset = builder.indexOf("?", offset)+8;
            builder.replace(offset, offset+1, CAMPOS_TABLA[0]);
        }
        db.execSQL(builder.toString(), campos);
    }

    public void delete(String id) {
        StringBuilder builder = new StringBuilder("DELETE FROM ? WHERE ? = '?'");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, id);
        db.execSQL(builder.toString());
    }

    public static String createLinkTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? INTEGER NOT NULL, " +
                "? TEXT NOT NULL, ? TEXT NOT NULL UNIQUE, FOREIGN KEY (?) REFERENCES ? (?)  )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_ENLACE);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        return builder.toString();
    }

    public ArrayList<AnnouncementLink> selectLink(String selection, String[] selectionArgs) {
        ArrayList<AnnouncementLink> links = new ArrayList<>();
        Cursor c = db.query(NOMBRE_ENLACE, CAMPOS_ENLACE, selection, selectionArgs,
                null, null, CAMPOS_ENLACE[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String announcementId = c.getString(1);
                String type = c.getString(2);
                String link = c.getString(3);
                AnnouncementLink announcementLink = new AnnouncementLink(id, announcementId, type, link);
                links.add(announcementLink);
            } while (c.moveToNext());
        }
        c.close();
        return links;
    }

    public void insertLink(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?,?) VALUES (?,?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_ENLACE);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[3]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2],
                campos[3]
        });
    }

    public void deleteLink(String announcementId) {
        StringBuilder builder = new StringBuilder("DELETE FROM ? WHERE ? = '?'");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_ENLACE);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_ENLACE[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, announcementId);
        db.execSQL(builder.toString());
    }

    public void unreadAll() {
        db.execSQL("UPDATE `"+NOMBRE_TABLA+"` SET `"+CAMPOS_TABLA[5]+"` = 'N'");
    }

    public void destroy() {
        usdbh.close();
    }

}
