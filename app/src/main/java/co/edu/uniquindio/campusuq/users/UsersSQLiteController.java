package co.edu.uniquindio.campusuq.users;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 15/03/2018.
 */

public class UsersSQLiteController {

    private static final String tablename = "Usuario";
    public static final String columns[] = {"_ID", "Nombre", "Correo", "Telefono", "Direccion",
            "Documento", "Contrasena", "Clave_Api", "Administrador"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public UsersSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL, " + columns[2] + " TEXT NOT NULL UNIQUE, " +
                columns[3] + " TEXT, " + columns[4] + " TEXT, " + columns[5] + " TEXT, " +
                columns[6] + " TEXT NOT NULL, " + columns[7] + " TEXT NOT NULL, " +
                columns[8] + " TEXT NOT NULL)";
    }

    public ArrayList<User> select() {
        ArrayList<User> users = new ArrayList<>();

        Cursor c = db.query(tablename, null, null, null,
                null, null, columns[0] + " ASC");
        if (c.moveToFirst()) do {
            users.add(new User(c.getString(0), c.getString(1), c.getString(2),
                    c.isNull(3) ? null : c.getString(3),
                    c.isNull(4) ? null : c.getString(4),
                    c.isNull(5) ? null : c.getString(5), c.getString(6), c.getString(7),
                    c.getString(8)));
        } while(c.moveToNext());
        c.close();

        return users;
    }

    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    void update(Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " +
                TextUtils.join(" = ?, ", columns) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    public void delete(Object... ids) {
        db.execSQL("DELETE FROM " + tablename + " WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    public void destroy() {
        usdbh.close();
    }

}
