package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Information;
import co.edu.uniquindio.campusuq.vo.InformationCategory;

/**
 * Created by Juan Camilo on 21/02/2018.
 */

public class InformationsSQLiteController {

    public static final String NOMBRE_TABLA = "Informacion";
    public static final String CAMPOS_TABLA[] = new String[]{"_ID", "Categoria_ID", "Nombre", "Contenido"};

    public static final String NOMBRE_CATEGORIA = "Informacion_Categoria";
    public static final String CAMPOS_CATEGORIA[] = new String[]{"_ID", "Nombre", "Enlace", "Fecha"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public InformationsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? INTEGER NOT NULL, " +
                "? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL, FOREIGN KEY (?) REFERENCES ? (?) )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        return builder.toString();
    }

    public ArrayList<Information> select(String selection, String[] selectionArgs) {
        ArrayList<Information> informations = new ArrayList<>();
        Cursor c = db.query(NOMBRE_TABLA, CAMPOS_TABLA, selection, selectionArgs,
                null, null, CAMPOS_TABLA[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String categoryId = c.getString(1);
                String name = c.getString(2);
                String content = c.getString(3);
                Information information = new Information(id, name, categoryId, content);
                informations.add(information);
            } while (c.moveToNext());
        }
        return informations;
    }

    public void insert(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?,?) VALUES (?,?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2],
                campos[3]
        });
    }

    public void update(String... campos) {
        String update = "UPDATE "+NOMBRE_TABLA+" SET ?=?,?=?,?=? WHERE ? = ?";
        StringBuilder builder = new StringBuilder(update);
        int offset = builder.indexOf("?");
        for (int i = 1; i < 4; i++) {
            builder.replace(offset, offset+1, CAMPOS_TABLA[i]);
            offset = builder.indexOf("?", offset)+2;
        }
        offset = offset+6;
        builder.replace(offset, offset+1, CAMPOS_TABLA[0]);
        db.execSQL(builder.toString(), new String[] {
                campos[1],
                campos[2],
                campos[3],
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
                "? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL, ? TEXT NOT NULL )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[3]);
        return builder.toString();
    }

    public ArrayList<InformationCategory> selectCategory(String selection, String[] selectionArgs) {
        ArrayList<InformationCategory> categories = new ArrayList<>();
        Cursor c = db.query(NOMBRE_CATEGORIA, CAMPOS_CATEGORIA, selection, selectionArgs,
                null, null, null);
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String name = c.getString(1);
                String link = c.getString(2);
                String date = c.getString(3);
                InformationCategory category = new InformationCategory(id, name, link, date);
                categories.add(category);
            } while (c.moveToNext());
        }
        return categories;
    }

    public void insertCategory(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?,?) VALUES (?,?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[3]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2],
                campos[3]
        });
    }

    public void updateCategory(String... campos) {
        String update = "UPDATE "+NOMBRE_CATEGORIA+" SET ?=?,?=?,?=? WHERE ? = ?";
        StringBuilder builder = new StringBuilder(update);
        int offset = builder.indexOf("?");
        for (int i = 1; i < 4; i++) {
            builder.replace(offset, offset+1, CAMPOS_CATEGORIA[i]);
            offset = builder.indexOf("?", offset)+2;
        }
        offset = offset+6;
        builder.replace(offset, offset+1, CAMPOS_CATEGORIA[0]);
        db.execSQL(builder.toString(), new String[] {
                campos[1],
                campos[2],
                campos[3],
                campos[0]
        });
    }


    public void destroy() {
        usdbh.close();
    }

}
