package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteController;
import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

public class NewsSQLiteController extends SQLiteController {

    private static final String tablename = "Noticia";
    public static final String columns[] =
            {"_ID", "Nombre", "Enlace", "Imagen", "Resumen", "Contenido", "Fecha", "Autor"};

    private static final String categoryTablename = "Noticia_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace"};

    private static final String relationTablename = "Noticia_Relacion";
    public static final String relationColumns[] = {"Categoria_ID", "Noticia_ID"};

    public NewsSQLiteController(Context context, int version) {
        super(context, version);
    }

    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, categoryTablename, relationTablename}[index];
    }

    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, categoryColumns, relationColumns}[index];
    }

    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL UNIQUE, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " TEXT NOT NULL, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " TEXT NOT NULL, " +
                columns[7] + " TEXT NOT NULL)";
    }

    public ArrayList<New> select(String limit, String selection, String... selectionArgs) {
        ArrayList<New> news = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[6] + " DESC", limit);

        if (c.moveToFirst()) do {
            news.add(new New(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.getString(7)));
        } while (c.moveToNext());

        c.close();
        return news;
    }

    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE, " +
                categoryColumns[2] + " TEXT NOT NULL)";
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
        insert(1, values);
    }

    public void updateCategory(Object... values) {
        update(1, values);
    }

    public void deleteCategory(Object... ids) {
        delete(1, ids);
    }

    public static String createRelationTable(){
        return "CREATE TABLE " + relationTablename + '(' +
                relationColumns[0] + " INTEGER NOT NULL REFERENCES " + categoryTablename + '(' +
                categoryColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                relationColumns[1] + " INTEGER NOT NULL REFERENCES " + tablename + '(' +
                columns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (" +
                relationColumns[0] + ", " + relationColumns[1] + "))";
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
        insert(2, values);
    }

}
