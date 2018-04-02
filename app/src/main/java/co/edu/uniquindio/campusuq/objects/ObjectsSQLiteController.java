package co.edu.uniquindio.campusuq.objects;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

public class ObjectsSQLiteController {

    private static final String tablename = "Objetos";
    public static final String columns[] = {"_ID", "Usuario_Perdio_ID", "Nombre", "Lugar",
            "Fecha_Perdio", "Fecha", "Descripcion", "Imagen", "Usuario_Encontro_ID", "Leido"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public ObjectsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable() {
        return "CREATE TABLE "+tablename+'('+columns[0]+" INTEGER PRIMARY KEY, "+
                columns[1]+" INTEGER NOT NULL, "+columns[2]+" TEXT NOT NULL, "+
                columns[3]+" TEXT NOT NULL, "+columns[4]+" TEXT NOT NULL, "+
                columns[5]+" TEXT NOT NULL, "+columns[6]+" TEXT NOT NULL, "+columns[7]+" TEXT, "+
                columns[8]+" INTEGER, "+columns[9]+" TEXT NOT NULL)";
    }

    public ArrayList<LostObject> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<LostObject> objects = new ArrayList<>();

        Cursor c = db.query(tablename, columns, selection, selectionArgs, null,
                null, columns[5]+" DESC", limit);
        if (c.moveToFirst()) do {
            objects.add(new LostObject(c.getInt(0), c.getInt(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.isNull(7) ? null : c.getString(7),
                    c.isNull(8) ? null : c.getInt(8), c.getString(9)));
        } while (c.moveToNext());
        c.close();

        return objects;
    }

    public void insert(Object... values) {
        db.execSQL("INSERT INTO "+tablename+'('+
                TextUtils.join(", ", columns)+") VALUES("+
                TextUtils.join(", ", Collections.nCopies(columns.length, '?'))+
                ')', values);
    }

    public void update(Object... values) {
        db.execSQL("UPDATE "+tablename+" SET "+TextUtils.join(" = ?, ",
                Arrays.copyOfRange(columns, 0, columns.length-1))+" = ? WHERE "+
                columns[0]+" = ?", values);
    }

    void readed(Object... ids) {
        db.execSQL("UPDATE "+tablename+" SET "+columns[9]+" = 'S' WHERE "+columns[0]+" IN("+
                TextUtils.join(", ", Collections.nCopies(ids.length, '?'))+')', ids);
    }

    void unreadAll() {
        db.execSQL("UPDATE "+tablename+" SET "+columns[9]+" = 'N'");
    }

    public void delete(Object... ids) {
        db.execSQL("DELETE FROM "+tablename+" WHERE "+columns[0]+" IN("+
                TextUtils.join(", ", Collections.nCopies(ids.length, '?'))+')', ids);
    }

    public void destroy() {
        usdbh.close();
    }

}
