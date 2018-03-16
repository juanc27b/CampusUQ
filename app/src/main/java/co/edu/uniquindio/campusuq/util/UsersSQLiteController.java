package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.vo.User;

/**
 * Created by Juan Camilo on 15/03/2018.
 */

public class UsersSQLiteController {

    private static final String tablename = "Usuario";
    public static final String columns[] =
            {"_ID", "Nombre", "Correo", "Telefono", "Direccion", "Documento", "Contrasena", "Clave_Api", "Administrador"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public UsersSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    static String createTable() {
        return "CREATE TABLE `"+tablename+"` (`"+
                columns[0]+"` INTEGER PRIMARY KEY, `"+
                columns[1]+"` TEXT NOT NULL, `"+
                columns[2]+"` TEXT NOT NULL UNIQUE, `"+
                TextUtils.join("` TEXT NOT NULL, `", Arrays.copyOfRange(columns, 3, columns.length))+
                "` TEXT NOT NULL )";
    }

    public ArrayList<User> select(String selection, String[] selectionArgs) {
        ArrayList<User> users = new ArrayList<>();
        Cursor c = db.query(tablename, columns, selection, selectionArgs, null, null, columns[0]+" ASC");
        if(c.moveToFirst()) do {
            users.add(new User(
                    c.getString(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6),
                    c.getString(7),
                    c.getString(8)
            ));
        } while(c.moveToNext());
        c.close();
        return users;
    }

    public void insert(String... values) {
        db.execSQL("INSERT INTO `"+tablename+"`(`"+
                TextUtils.join("`, `", columns)+"`) VALUES ("+
                TextUtils.join(", ", Collections.nCopies(columns.length, "?"))+")", values);
    }

    void update(String... values) {
        db.execSQL("UPDATE `"+tablename+"` SET `"+
                TextUtils.join("` = ?, `", columns)+"` = ? WHERE `"+columns[0]+"` = ?", values);
    }

    public void delete(String id) {
        db.execSQL("DELETE FROM `"+tablename+"` WHERE `"+columns[0]+"` = ?", new String[]{id});
    }

    public void destroy() {
        usdbh.close();
    }

}
