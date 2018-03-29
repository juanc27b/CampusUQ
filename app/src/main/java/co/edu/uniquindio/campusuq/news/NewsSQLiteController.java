package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewsSQLiteController {

    private static final String NOMBRE_TABLA = "Noticia";
    public static final String CAMPOS_TABLA[] =
            new String[]{"_ID", "Nombre", "Enlace", "Imagen", "Resumen", "Contenido", "Fecha", "Autor"};

    private static final String NOMBRE_CATEGORIA = "Noticia_Categoria";
    public static final String CAMPOS_CATEGORIA[] = new String[]{"_ID", "Nombre", "Enlace"};

    private static final String NOMBRE_RELACION = "Noticia_Relacion";
    public static final String CAMPOS_RELACION[] = new String[]{"Categoria_ID", "Noticia_ID"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public NewsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, " +
                "? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, " +
                "? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[4]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[5]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[6]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[7]);
        return builder.toString();
    }

    public ArrayList<New> select(String limit, String selection, String[] selectionArgs) {
        ArrayList<New> news = new ArrayList<>();
        Cursor c = db.query(NOMBRE_TABLA, CAMPOS_TABLA, selection, selectionArgs,
                null, null, CAMPOS_TABLA[6]+" DESC", limit);
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String name = c.getString(1);
                String link = c.getString(2);
                String image = c.getString(3);
                String summary = c.getString(4);
                String content = c.getString(5);
                String date = c.getString(6);
                String author = c.getString(7);
                New mNew = new New(id, name, link, image, summary, content, date, author);
                news.add(mNew);
            } while (c.moveToNext());
        }
        c.close();
        return news;
    }

    public void insert(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?,?,?,?,?,?) VALUES (?,?,?,?,?,?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[4]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[5]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[6]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[7]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2],
                campos[3],
                campos[4],
                campos[5],
                campos[6],
                campos[7]
        });
    }

    public void update(String... campos) {
        String update = "UPDATE "+NOMBRE_TABLA+" SET ?=?,?=?,?=?,?=?,?=?,?=?,?=? WHERE ? = ?";
        StringBuilder builder = new StringBuilder(update);
        int offset = builder.indexOf("?");
        for (int i = 1; i < 8; i++) {
            builder.replace(offset, offset+1, CAMPOS_TABLA[i]);
            offset = builder.indexOf("?", offset)+2;
        }
        offset = offset+6;
        builder.replace(offset, offset+1, CAMPOS_TABLA[0]);
        db.execSQL(builder.toString(), new String[] {
                campos[1],
                campos[2],
                campos[3],
                campos[4],
                campos[5],
                campos[6],
                campos[7],
                campos[0]
        });
    }

    public void delete(String id) {
        StringBuilder builder = new StringBuilder("DELETE FROM ? WHERE ? = '?'");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, id);
        db.execSQL(builder.toString());
    }

    public static String createCategoryTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, " +
                "? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL)";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[2]);
        return builder.toString();
    }

    public ArrayList<NewCategory> selectCategory(String limit, String selection, String[] selectionArgs) {
        ArrayList<NewCategory> categories = new ArrayList<>();
        Cursor c = db.query(NOMBRE_CATEGORIA, CAMPOS_CATEGORIA, selection, selectionArgs,
                null, null, null, limit);
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String name = c.getString(1);
                String link = c.getString(2);
                NewCategory newCategory = new NewCategory(id, name, link);
                categories.add(newCategory);
            } while (c.moveToNext());
        }
        c.close();
        return categories;
    }

    public void insertCategory(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?) VALUES (?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[2]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2]
        });
    }

    public static String createRelationTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER NOT NULL, ? INTEGER NOT NULL, " +
                "PRIMARY KEY (?, ?), FOREIGN KEY (?) REFERENCES ? (?), FOREIGN KEY (?) REFERENCES ? (?) )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_RELACION);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        return builder.toString();
    }

    public ArrayList<NewRelation> selectRelation(String limit, String selection, String[] selectionArgs) {
        ArrayList<NewRelation> relations = new ArrayList<>();
        Cursor c = db.query(NOMBRE_RELACION, CAMPOS_RELACION, selection, selectionArgs,
                null, null, null, limit);
        if (c.moveToFirst()) {
            do {
                String categoryId = c.getString(0);
                String newId = c.getString(1);
                NewRelation newRelation = new NewRelation(categoryId, newId);
                relations.add(newRelation);
            } while (c.moveToNext());
        }
        c.close();
        return relations;
    }

    public void insertRelation(String... campos) {
        String insertar = "INSERT INTO ? (?,?) VALUES (?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_RELACION);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1]
        });
    }

    public void destroy() {
        usdbh.close();
    }

}
