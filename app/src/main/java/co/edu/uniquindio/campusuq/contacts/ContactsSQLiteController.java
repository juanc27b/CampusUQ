package co.edu.uniquindio.campusuq.contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

public class ContactsSQLiteController {

    private static final String tablename = "Contacto";
    public static final String columns[] = {"_ID", "Categoria_ID", "Nombre", "Direccion",
            "Telefono", "Email", "Cargo", "Informacion_Adicional"};

    private static final String categoryTablename = "Contacto_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public ContactsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        return "CREATE TABLE "+tablename+'('+columns[0]+" INTEGER PRIMARY KEY, "+
                columns[1]+" INTEGER NOT NULL REFERENCES "+
                categoryTablename+'('+categoryColumns[0]+") ON UPDATE CASCADE ON DELETE CASCADE, "+
                columns[2]+" TEXT NOT NULL UNIQUE, "+columns[3]+" TEXT NOT NULL, "+
                columns[4]+" TEXT NOT NULL, "+columns[5]+" TEXT NOT NULL, "+
                columns[6]+" TEXT NOT NULL, "+columns[7]+" TEXT NOT NULL)";
    }

    public ArrayList<Contact> select(String selection, String... selectionArgs) {
        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor c = db.query(tablename, columns, selection, selectionArgs, null,
                null, columns[2]+" ASC");
        if (c.moveToFirst()) do {
            contacts.add(new Contact(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.getString(7)));
        } while (c.moveToNext());
        c.close();

        return contacts;
    }

    public void insert(Object... values) {
        db.execSQL("INSERT INTO "+tablename+'('+
                TextUtils.join(", ", columns)+") VALUES("+
                TextUtils.join(", ", Collections.nCopies(columns.length, '?'))+
                ')', values);
    }

    public void delete(Object... ids) {
        db.execSQL("DELETE FROM "+tablename+" WHERE "+columns[0]+" IN("+
                TextUtils.join(", ", Collections.nCopies(ids.length, '?'))+')', ids);
    }

    public static String createCategoryTable(){
        return "CREATE TABLE "+categoryTablename+'('+categoryColumns[0]+" INTEGER PRIMARY KEY, "+
                categoryColumns[1]+" TEXT NOT NULL UNIQUE, "+categoryColumns[2]+" TEXT NOT NULL)";
    }

    public ArrayList<ContactCategory> selectCategory(String limit, String selection,
                                                     String... selectionArgs) {
        ArrayList<ContactCategory> categories = new ArrayList<>();

        Cursor c = db.query(categoryTablename, categoryColumns, selection, selectionArgs,
                null, null, categoryColumns[1]+" ASC", limit);
        if (c.moveToFirst()) do {
            categories.add(new ContactCategory(c.getString(0), c.getString(1),
                    c.getString(2)));
        } while (c.moveToNext());
        c.close();

        return categories;
    }

    public void insertCategory(Object... values) {
        db.execSQL("INSERT INTO "+categoryTablename+'('+
                TextUtils.join(", ", categoryColumns)+") VALUES("+
                TextUtils.join(", ", Collections.nCopies(categoryColumns.length, '?'))+
                ')', values);
    }

    public void deleteCategory(Object... ids) {
        db.execSQL("DELETE FROM "+categoryTablename+" WHERE "+categoryColumns[0]+" IN("+
                TextUtils.join(", ", Collections.nCopies(ids.length, '?'))+')', ids);
    }

    public void destroy() {
        usdbh.close();
    }

}
