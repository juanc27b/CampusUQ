package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasSQLiteController {
    private static final String tablename = "Cupo";
    public static final String CAMPOS_TABLA[] = new String[]{"_ID", "Tipo", "Nombre", "Cupo"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public QuotasSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable() {
        return "CREATE TABLE `"+tablename+"`(`"+CAMPOS_TABLA[0]+"` INTEGER PRIMARY KEY, `"+TextUtils.join("` TEXT NOT NULL, `", Arrays.copyOfRange(CAMPOS_TABLA, 1, CAMPOS_TABLA.length))+"` TEXT NOT NULL)";
    }

    public ArrayList<Quota> select(String limit, String selection, String[] selectionArgs) {
        Cursor c = db.query(tablename, CAMPOS_TABLA, selection, selectionArgs, null, null, null, limit);
        ArrayList<Quota> quotas = new ArrayList<>();
        if(c.moveToFirst()) do {
            quotas.add(new Quota(c.getString(0), c.getString(1), c.getString(2), c.getString(3)));
        } while(c.moveToNext());
        return quotas;
    }

    public void insert(String... campos) {
        db.execSQL("INSERT INTO `"+tablename+"`(`"+TextUtils.join("`, `", CAMPOS_TABLA)+"`) VALUES(?, ?, ?, ?)", campos);
    }

    public void update(String... campos) {
        db.execSQL("UPDATE `"+tablename+"` SET `"+TextUtils.join("` = ?, `", Arrays.copyOfRange(CAMPOS_TABLA, 1, CAMPOS_TABLA.length))+"` = ? WHERE `"+CAMPOS_TABLA[0]+"` = ?", campos);
    }

    public void delete(String id) {
        db.execSQL("DELETE FROM `"+tablename+"` WHERE `"+CAMPOS_TABLA[0]+"` = ?", new String[]{id});
    }

    public void destroy() {
        usdbh.close();
    }
}
