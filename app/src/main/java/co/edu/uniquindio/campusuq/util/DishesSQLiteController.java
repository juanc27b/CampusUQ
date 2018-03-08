package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.vo.Dish;

public class DishesSQLiteController {
    private static final String tablename = "Plato";
    public static final String columns[] = {"_ID", "Nombre", "Descripcion", "Precio", "Imagen"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public DishesSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    static String createTable() {
        return "CREATE TABLE `"+tablename+"`(`"+columns[0]+"` INTEGER PRIMARY KEY, `"+TextUtils.join("` TEXT NOT NULL, `", Arrays.copyOfRange(columns, 1, columns.length))+"` TEXT NOT NULL)";
    }

    public ArrayList<Dish> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<Dish> dishes = new ArrayList<>();
        Cursor c = db.query(tablename, columns, selection, selectionArgs, null, null, null, limit);
        if(c.moveToFirst()) do {
            dishes.add(new Dish(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
        } while(c.moveToNext());
        c.close();
        return dishes;
    }

    public void insert(String... values) {
        db.execSQL("INSERT INTO `"+tablename+"`(`"+TextUtils.join("`, `", columns)+"`) VALUES("+TextUtils.join(", ", Collections.nCopies(columns.length, "?"))+")", values);
    }

    void update(String... values) {
        db.execSQL("UPDATE `"+tablename+"` SET `"+TextUtils.join("` = ?, `", columns)+"` = ? WHERE `"+columns[0]+"` = ?", values);
    }

    public void delete(String id) {
        db.execSQL("DELETE FROM `"+tablename+"` WHERE `"+columns[0]+"` = ?", new String[]{id});
    }

    public void destroy() {
        usdbh.close();
    }
}