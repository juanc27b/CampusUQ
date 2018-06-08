package co.edu.uniquindio.campusuq.notifications;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

public class NotificationsSQLiteController extends SQLiteController {

    private static final String tablename = "Notificacion";
    public static final String columns[] = {"_ID", "Nombre", "Activada"};

    private static final String detailTablename = "NotificacionDetail";
    private static final String detailColumns[] =
            {"_ID", "Categoria", "Nombre", "Fecha_Hora", "Descripcion"};

    NotificationsSQLiteController(Context context, int version) {
        super(context, version);
    }

    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, detailTablename}[index];
    }

    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, detailColumns}[index];
    }

    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL UNIQUE, " + columns[2] + " TEXT NOT NULL)";
    }

    public ArrayList<Notification> select(String limit, String selection, String... selectionArgs) {
        ArrayList<Notification> notifications = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs,
                null, null, columns[0] + " ASC", limit);

        if (c.moveToFirst()) do {
            notifications.add(new Notification(c.getString(0), c.getString(1),
                    c.getString(2)));
        } while (c.moveToNext());

        c.close();
        return notifications;
    }

    public static String createDetailTable() {
        return "CREATE TABLE " + detailTablename + '(' +
                detailColumns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                detailColumns[1] + " INTEGER NOT NULL, " + detailColumns[2] + " TEXT NOT NULL, " +
                detailColumns[3] + " TEXT NOT NULL, " + detailColumns[4] + " TEXT)";
    }

    ArrayList<NotificationDetail> selectDetail() {
        ArrayList<NotificationDetail> notificationDetails = new ArrayList<>();
        Cursor c = db.query(detailTablename, null, null, null,
                null, null, detailColumns[0] + " ASC");

        if (c.moveToFirst()) do {
            notificationDetails.add(new NotificationDetail(c.getString(0), c.getInt(1),
                    c.getString(2), c.getString(3), c.getString(4)));
        } while (c.moveToNext());

        c.close();
        return notificationDetails;
    }

    void insertDetail(Object... values) {
        insert(1, values);
    }

    void deleteDetail(Object... ids) {
        delete(1, ids);
    }

}
