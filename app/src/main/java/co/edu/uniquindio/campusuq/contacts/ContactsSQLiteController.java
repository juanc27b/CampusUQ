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
 * Controlador de la base de datos para las tablas Contacto y Contacto_Categoria, que hacen parte de
 * la funcionalidad de Directorio telefónico.
 */
public class ContactsSQLiteController {

    private static final String tablename = "Contacto";
    public static final String columns[] = {"_ID", "Categoria_ID", "Nombre", "Direccion",
            "Telefono", "Email", "Cargo", "Informacion_Adicional"};

    private static final String categoryTablename = "Contacto_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    /**
     * Constructor del controlador de la base de datos, el cual utiliza un SQLiteHelper para
     * asegurar que las tablas se creen al crear la base de datos, y obtiene una instancia de la
     * base de datos con permisos de escritura.
     * @param context Contexto necesario para crear un SQLiteHelper.
     * @param version Versión de la base de datos.
     */
    public ContactsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    /**
     * Crea una cadena válida para crear la tabla Contacto en la base de datos.
     * @return Cadena SQL para crear la tabla Contacto.
     */
    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " INTEGER NOT NULL REFERENCES " + categoryTablename + '(' +
                categoryColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                columns[2] + " TEXT NOT NULL UNIQUE, " + columns[3] + " TEXT NOT NULL, " +
                columns[4] + " TEXT NOT NULL, " + columns[5] + " TEXT NOT NULL, " +
                columns[6] + " TEXT NOT NULL, " + columns[7] + " TEXT NOT NULL)";
    }

    /**
     * Obtiene de la base de datos un arreglo de contactos de acuerdo a los filtros pasados como
     * parámetro a la función, y los ordena por nombre en orden ascendente.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de contactos extraído de la base de datos.
     */
    public ArrayList<Contact> select(String selection, String... selectionArgs) {
        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[2] + " ASC");
        if (c.moveToFirst()) do {
            contacts.add(new Contact(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.getString(7)));
        } while (c.moveToNext());
        c.close();

        return contacts;
    }

    /**
     * Inserta en la base de datos un contacto con los valores de las columnas pasados como
     * parámetro a la función
     * @param values Valores de las columnas del contacto.
     */
    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    /**
     * Elimina de la base de datos los contactos cuyas IDs se encuentren dentro del arreglo de
     * IDs parado como parámetro.
     * @param ids Arreglo de IDs de los contactos que se quiere eliminar.
     */
    public void delete(Object... ids) {
        db.execSQL("DELETE FROM " + tablename + " WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Crea una cadena válida para crear la tabla Contacto_Categoria en la base de datos.
     * @return Cadena SQL para crear la tabla Contacto_Categoria.
     */
    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE, " +
                categoryColumns[2] + " TEXT NOT NULL)";
    }

    /**
     * Obtiene de la base de datos un arreglo de categorías de contactos de acuerdo a los filtros y
     * al límite de resultados pasados como parámetro a la función, y las ordena por nombre en orden
     * ascendente.
     * @param limit Límite de resultados a obtener.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de categorías de contactos extraído de la base de datos.
     */
    public ArrayList<ContactCategory> selectCategory(String limit, String selection,
                                                     String... selectionArgs) {
        ArrayList<ContactCategory> categories = new ArrayList<>();

        Cursor c = db.query(categoryTablename, null, selection, selectionArgs,
                null, null, categoryColumns[1] + " ASC", limit);
        if (c.moveToFirst()) do {
            categories.add(new ContactCategory(c.getString(0), c.getString(1),
                    c.getString(2)));
        } while (c.moveToNext());
        c.close();

        return categories;
    }

    /**
     * Inserta en la base de datos una categoría de contacto con los valores de las columnas pasados
     * como parámetro a la función
     * @param values Valores de las columnas de la categoría de contacto.
     */
    public void insertCategory(Object... values) {
        db.execSQL("INSERT INTO " + categoryTablename + '(' +
                TextUtils.join(", ", categoryColumns) + ") VALUES(" +
                TextUtils.join(", ",
                        Collections.nCopies(categoryColumns.length, '?')) + ')', values);
    }

    /**
     * Elimina de la base de datos las categorías de contactos cuyas IDs se encuentren dentro del
     * arreglo de IDs parado como parámetro.
     * @param ids Arreglo de IDs de las categorías de contactos que se quiere eliminar.
     */
    public void deleteCategory(Object... ids) {
        db.execSQL("DELETE FROM " + categoryTablename + " WHERE " +
                categoryColumns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Método para cerrar cualquier conexión abierta a la base de datos.
     */
    public void destroy() {
        usdbh.close();
    }

}
