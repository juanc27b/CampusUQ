package co.edu.uniquindio.campusuq.notifications;

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
 * Created by Juan Camilo on 22/03/2018.
 */

public class NotificationsSQLiteController {

    private static final String tablename = "Notificacion";
    public static final String columns[] = {"_ID", "Nombre", "Activada"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    NotificationsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable() {
        return "CREATE TABLE `"+tablename+"` (`"+columns[0]+"` INTEGER PRIMARY KEY, `"+
                columns[1]+"` TEXT NOT NULL UNIQUE, `"+columns[2]+"` TEXT NOT NULL )";
    }

    public ArrayList<Notification> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<Notification> notifications = new ArrayList<>();
        Cursor c = db.query(tablename, columns, selection, selectionArgs,
                null, null, columns[0]+" ASC", limit);
        if(c.moveToFirst()) do {
            notifications.add(new Notification(
                    c.getString(0),
                    c.getString(1),
                    c.getString(2)
            ));
        } while(c.moveToNext());
        c.close();
        return notifications;
    }

    public void insert(String... values) {
        db.execSQL("INSERT INTO `"+tablename+"`(`"+
                TextUtils.join("`, `", columns)+"`) VALUES ("+
                TextUtils.join(", ", Collections.nCopies(columns.length, "?"))+")", values);
    }

    public void update(String... values) {
        db.execSQL("UPDATE `"+tablename+"` SET `"+
                TextUtils.join("` = ?, `", Arrays.copyOfRange(columns, 0, columns.length))+
                "` = ? WHERE `"+columns[0]+"` = ?", values);
    }

    public void delete(ArrayList<String> ids) {
        db.execSQL("DELETE FROM `"+tablename+"` WHERE `"+columns[0]+"` IN("+
                TextUtils.join(", ", Collections.nCopies(ids.size(), "?"))+")", ids.toArray());
    }

    public void destroy() {
        usdbh.close();
    }

}
