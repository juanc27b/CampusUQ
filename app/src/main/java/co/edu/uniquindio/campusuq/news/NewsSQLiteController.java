package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewsSQLiteController {

    private static final String tablename = "Noticia";
    public static final String columns[] = {"_ID", "Nombre", "Enlace", "Imagen", "Resumen",
            "Contenido", "Fecha", "Autor"};

    private static final String categoryTablename = "Noticia_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public NewsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        return "CREATE TABLE "+tablename+'('+columns[0]+" INTEGER PRIMARY KEY, "+
                columns[1]+" TEXT NOT NULL UNIQUE, "+columns[2]+" TEXT NOT NULL, "+
                columns[3]+" TEXT NOT NULL, "+columns[4]+" TEXT NOT NULL, "+
                columns[5]+" TEXT NOT NULL, "+columns[6]+" TEXT NOT NULL, "+
                columns[7]+" TEXT NOT NULL)";
    }

    public ArrayList<New> select(String limit, String selection, String... selectionArgs) {
        ArrayList<New> news = new ArrayList<>();

        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[6]+" DESC", limit);
        if (c.moveToFirst()) do {
            news.add(new New(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.getString(7)));
        } while (c.moveToNext());
        c.close();

        return news;
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

    public static String createCategoryTable(){
        return "CREATE TABLE "+categoryTablename+'('+categoryColumns[0]+" INTEGER PRIMARY KEY, "+
                categoryColumns[1]+" TEXT NOT NULL UNIQUE, "+categoryColumns[2]+" TEXT NOT NULL)";
    }

    public ArrayList<NewCategory> selectCategory(String limit, String selection,
                                                 String... selectionArgs) {
        ArrayList<NewCategory> categories = new ArrayList<>();

        Cursor c = db.query(categoryTablename, null, selection, selectionArgs,
                null, null, null, limit);
        if (c.moveToFirst()) do {
            categories.add(new NewCategory(c.getString(0), c.getString(1),
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

    private static final String relationTablename = "Noticia_Relacion";
    public static final String relationColumns[] = {"Categoria_ID", "Noticia_ID"};

    public static String createRelationTable(){
        return "CREATE TABLE "+relationTablename+'('+
                relationColumns[0]+" INTEGER NOT NULL REFERENCES "+
                categoryTablename+'('+categoryColumns[0]+") ON UPDATE CASCADE ON DELETE CASCADE, "+
                relationColumns[1]+" INTEGER NOT NULL REFERENCES "+
                tablename+'('+columns[0]+") ON UPDATE CASCADE ON DELETE CASCADE, "+
                "PRIMARY KEY ("+relationColumns[0]+", "+relationColumns[1]+"))";
    }

    public ArrayList<NewRelation> selectRelation(String selection, String... selectionArgs) {
        ArrayList<NewRelation> relations = new ArrayList<>();

        Cursor c = db.query(relationTablename, null, selection, selectionArgs,
                null, null, null);
        if (c.moveToFirst()) do {
            relations.add(new NewRelation(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        c.close();

        return relations;
    }

    public void insertRelation(Object... values) {
        db.execSQL("INSERT INTO "+relationTablename+'('+
                TextUtils.join(", ", relationColumns)+") VALUES("+
                TextUtils.join(", ", Collections.nCopies(relationColumns.length, '?'))+
                ')', values);
    }

    public void destroy() {
        usdbh.close();
    }

}
