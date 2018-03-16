package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.vo.Email;

public class EmailsSQLiteController {
    private static final String tablename = "Correo";
    public static final String columns[] = {"_ID", "Nombre", "De", "Para", "Fecha", "Contenido"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public EmailsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    static String createTable() {
        return "CREATE TABLE `"+tablename+"`(`"+columns[0]+"` INTEGER PRIMARY KEY, `"+ TextUtils.join("` TEXT NOT NULL, `", Arrays.copyOfRange(columns, 1, columns.length))+"` TEXT NOT NULL)";
    }

    public ArrayList<Email> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<Email> emails = new ArrayList<>();
        Cursor c = db.query(tablename, columns, selection, selectionArgs, null, null, null, limit);
        if(c.moveToFirst()) do {
            emails.add(new Email(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)));
        } while(c.moveToNext());
        c.close();
        return emails;
    }

    public void insert(String... values) {
        db.execSQL("INSERT INTO `"+tablename+"`(`"+TextUtils.join("`, `", columns)+"`) VALUES("+TextUtils.join(", ", Collections.nCopies(columns.length, "?"))+")", values);
    }

    void update(String... values) {
        db.execSQL("UPDATE `"+tablename+"` SET `"+TextUtils.join("` = ?, `", columns)+"` = ? WHERE `"+columns[0]+"` = ?", values);
    }

    public void delete(ArrayList<String> ids) {
        db.execSQL("DELETE FROM `"+tablename+"` WHERE `"+columns[0]+"` IN("+TextUtils.join(", ", Collections.nCopies(ids.size(), "?"))+")", ids.toArray());
    }

    public void destroy() {
        usdbh.close();
    }
}
