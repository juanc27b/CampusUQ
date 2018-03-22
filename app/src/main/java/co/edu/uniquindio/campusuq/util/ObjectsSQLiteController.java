package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.vo.LostObject;

public class ObjectsSQLiteController {

    private static final String tablename = "Objetos";
    public static final String columns[] =
            {"_ID", "Usuario_Perdio_ID", "Nombre", "Lugar", "Fecha", "Descripcion", "Imagen", "Usuario_Encontro_ID", "Leido"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public ObjectsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    static String createTable() {
        return "CREATE TABLE `"+tablename+"` (`"+columns[0]+"` INTEGER PRIMARY KEY, `"+columns[1]+"` INTEGER NOT NULL, `"+
                TextUtils.join("` TEXT NOT NULL, `", Arrays.copyOfRange(columns, 2, columns.length-1))+
                "` INTEGER, `"+columns[8]+"` TEXT NOT NULL )";
    }

    public ArrayList<LostObject> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<LostObject> objects = new ArrayList<>();
        Cursor c = db.query(tablename, columns, selection, selectionArgs,
                null, null, columns[4]+" DESC", limit);
        if(c.moveToFirst()) do {
            objects.add(new LostObject(
                    c.getString(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6),
                    c.getString(7)
            ));
        } while(c.moveToNext());
        c.close();
        return objects;
    }

    public void insert(String... values) {
        db.execSQL("INSERT INTO `"+tablename+"`(`"+
                TextUtils.join("`, `", columns)+"`) VALUES ("+
                TextUtils.join(", ", Collections.nCopies(columns.length, "?"))+")", values);
    }

    public void update(String... values) {
        db.execSQL("UPDATE `"+tablename+"` SET `"+
                TextUtils.join("` = ?, `", Arrays.copyOfRange(columns, 0, columns.length-1))+
                "` = ? WHERE `"+columns[0]+"` = ?", values);
    }

    public void readed(String... values) {
        db.execSQL("UPDATE `"+tablename+"` SET `"+columns[8]+"` = 'S' WHERE `"+columns[0]+"` = ?", values);
    }

    public void unreadAll() {
        db.execSQL("UPDATE `"+tablename+"` SET `"+columns[8]+"` = 'N'");
    }

    public void delete(ArrayList<String> ids) {
        db.execSQL("DELETE FROM `"+tablename+"` WHERE `"+columns[0]+"` IN("+TextUtils.join(", ", Collections.nCopies(ids.size(), "?"))+")", ids.toArray());
    }

    public void destroy() {
        usdbh.close();
    }
}
