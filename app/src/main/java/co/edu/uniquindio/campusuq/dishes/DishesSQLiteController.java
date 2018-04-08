package co.edu.uniquindio.campusuq.dishes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

public class DishesSQLiteController {

    private static final String tablename = "Plato";
    public static final String columns[] = {"_ID", "Nombre", "Descripcion", "Precio", "Imagen"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public DishesSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable() {
        return "CREATE TABLE "+tablename+'('+columns[0]+" INTEGER PRIMARY KEY, "+
                columns[1]+" TEXT NOT NULL UNIQUE, "+columns[2]+" TEXT NOT NULL, "+
                columns[3]+" INTEGER NOT NULL, "+columns[4]+" TEXT)";
    }

    public ArrayList<Dish> select(String limit) {
        ArrayList<Dish> dishes = new ArrayList<>();

        Cursor c = db.query(tablename, columns, null, null, null,
                null, columns[0]+" DESC", limit);
        if (c.moveToFirst()) do {
            dishes.add(new Dish(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.isNull(4) ? null : c.getString(4)));
        } while (c.moveToNext());
        c.close();

        return dishes;
    }

    public void insert(Object... values) {
        db.execSQL("INSERT INTO "+tablename+'('+
                TextUtils.join(", ", columns)+") VALUES("+
                TextUtils.join(", ", Collections.nCopies(columns.length, '?'))+
                ')', values);
    }

    public void update(Object... values) {
        db.execSQL("UPDATE "+tablename+" SET "+
                TextUtils.join(" = ?, ", columns)+" = ? WHERE "+columns[0]+" = ?", values);
    }

    public void delete(Object... ids) {
        db.execSQL("DELETE FROM "+tablename+" WHERE "+columns[0]+" IN("+
                TextUtils.join(", ", Collections.nCopies(ids.length, '?'))+')', ids);
    }

    public void destroy() {
        usdbh.close();
    }

}