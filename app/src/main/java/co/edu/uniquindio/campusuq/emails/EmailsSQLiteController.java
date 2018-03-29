package co.edu.uniquindio.campusuq.emails;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

public class EmailsSQLiteController {

    private static final String tablename = "Correo";
    public static final String columns[] = {"_ID", "Nombre", "De", "Para", "Fecha", "Retazo",
            "Contenido", "History_ID"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public EmailsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable() {
        return "CREATE TABLE "+tablename+'('+columns[0]+" TEXT PRIMARY KEY, "+
                columns[1]+" TEXT NOT NULL, "+columns[2]+" TEXT NOT NULL, "+
                columns[3]+" TEXT NOT NULL, "+columns[4]+" TEXT NOT NULL, "+
                columns[5]+" TEXT NOT NULL, "+columns[6]+" TEXT NOT NULL, "+
                columns[7]+" TEXT NOT NULL)";
    }

    public ArrayList<Email> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<Email> emails = new ArrayList<>();

        Cursor c = db.query(tablename, columns, selection, selectionArgs, null,
                null, columns[4]+" DESC", limit);
        if (c.moveToFirst()) do {
            emails.add(new Email(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    new BigInteger(c.getString(7))));
        } while (c.moveToNext());
        c.close();

        return emails;
    }

    public void insert(Object... values) {
        db.execSQL("INSERT INTO "+tablename+"("+
                TextUtils.join(", ", columns)+") VALUES("+
                TextUtils.join(", ", Collections.nCopies(columns.length, "?"))+
                ")", values);
    }

    void update(Object... values) {
        db.execSQL("UPDATE "+tablename+" SET "+
                TextUtils.join(" = ?, ", columns)+" = ? WHERE "+columns[0]+" = ?", values);
    }

    public void delete(Object... ids) {
        db.execSQL("DELETE FROM "+tablename+" WHERE "+columns[0]+" IN("+
                TextUtils.join(", ", Collections.nCopies(ids.length, "?"))+")", ids);
    }

    public void destroy() {
        usdbh.close();
    }

}
