package co.edu.uniquindio.campusuq.contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

public class ContactsSQLiteController {

    private static final String NOMBRE_TABLA = "Contacto";
    public static final String CAMPOS_TABLA[] = new String[]{"_ID", "Categoria_ID", "Nombre", "Direccion",
            "Telefono", "Email", "Cargo", "Informacion_Adicional"};

    private static final String NOMBRE_CATEGORIA = "Contacto_Categoria";
    public static final String CAMPOS_CATEGORIA[] = new String[]{"_ID", "Nombre", "Enlace"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public ContactsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? INTEGER NOT NULL, " +
                "? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, " +
                "? TEXT NOT NULL, FOREIGN KEY (?) REFERENCES ? (?) )";
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
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        return builder.toString();
    }

    public ArrayList<Contact> select(String selection, String[] selectionArgs) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor c = db.query(NOMBRE_TABLA, CAMPOS_TABLA, selection, selectionArgs,
                null, null, CAMPOS_TABLA[2]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String categoryId = c.getString(1);
                String name = c.getString(2);
                String address = c.getString(3);
                String phone = c.getString(4);
                String email = c.getString(5);
                String charge = c.getString(6);
                String additionalInformation = c.getString(7);
                Contact contact = new Contact(id, categoryId, name, address, phone, email, charge, additionalInformation);
                contacts.add(contact);
            } while (c.moveToNext());
        }
        c.close();
        return contacts;
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

    public void delete() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        db.execSQL(builder.toString());
    }

    public static String createCategoryTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, " +
                "? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[2]);
        return builder.toString();
    }

    public ArrayList<ContactCategory> selectCategory(String selection, String[] selectionArgs) {
        ArrayList<ContactCategory> categories = new ArrayList<>();
        Cursor c = db.query(NOMBRE_CATEGORIA, CAMPOS_CATEGORIA, selection, selectionArgs,
                null, null, CAMPOS_CATEGORIA[1]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String name = c.getString(1);
                String link = c.getString(2);
                ContactCategory category = new ContactCategory(id, name, link);
                categories.add(category);
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

    public void deleteCategory() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        db.execSQL(builder.toString());
    }

    public void destroy() {
        usdbh.close();
    }

}
