package co.edu.uniquindio.campusuq.emails;

import android.content.Context;
import android.database.Cursor;

import java.math.BigInteger;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

public class EmailsSQLiteController extends SQLiteController {

    private static final String tablename = "Correo";
    public static final String columns[] = {"_ID", "Nombre", "De", "Para", "Fecha", "Retazo",
            "Contenido", "History_ID"};

    public EmailsSQLiteController(Context context, int version) {
        super(context, version);
    }

    @Override
    protected String getTablename(int index) {
        return tablename;
    }

    @Override
    protected String[] getColumns(int index) {
        return columns;
    }

    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " TEXT PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " TEXT NOT NULL, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " TEXT NOT NULL, " +
                columns[7] + " TEXT NOT NULL)";
    }

    public ArrayList<Email> select(String limit) {
        ArrayList<Email> emails = new ArrayList<>();
        Cursor c = db.query(tablename, null, null, null,
                null, null, columns[4] + " DESC", limit);

        if (c.moveToFirst()) do {
            emails.add(new Email(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    new BigInteger(c.getString(7))));
        } while (c.moveToNext());

        c.close();
        return emails;
    }

}
