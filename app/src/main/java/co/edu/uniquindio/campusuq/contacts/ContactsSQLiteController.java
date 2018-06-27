package co.edu.uniquindio.campusuq.contacts;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para las tablas Contacto y Contacto_Categoria, que hacen parte de
 * la funcionalidad de Directorio telefónico.
 */
public class ContactsSQLiteController extends SQLiteController {

    private static final String tablename = "Contacto";
    public static final String columns[] = {"_ID", "Categoria_ID", "Nombre", "Direccion",
            "Telefono", "Email", "Cargo", "Informacion_Adicional"};

    private static final String categoryTablename = "Contacto_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace"};

    /**
     * Constructor del controlador de la base de datos, el cual utiliza un SQLiteHelper para
     * asegurar que las tablas se creen al crear la base de datos, y obtiene una instancia de la
     * base de datos con permisos de escritura.
     * @param context Contexto necesario para crear un SQLiteHelper.
     * @param version Versión de la base de datos.
     */
    public ContactsSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index Parametro que permite elegir entre la tabla Contacto y la tabla
     *              Contacto_Categoria.
     * @return Nombre de la tabla elegida.
     */
    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, categoryTablename}[index];
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index Parametro que permite elegir entre la tabla Contacto y la tabla
     *              Contacto_Categoria.
     * @return Nombre de las columnas elegidas.
     */
    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, categoryColumns}[index];
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
     * Obtiene de la base de datos un arreglo de categorías de contacto de acuerdo a los filtros y
     * al límite de resultados pasados como parámetro a la función, y las ordena por nombre en orden
     * ascendente.
     * @param limit Límite de resultados a obtener.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de categorías de contacto extraído de la base de datos.
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
        insert(1, values);
    }

    /**
     * Elimina de la base de datos las categorías de contacto cuyas IDs se encuentren dentro del
     * arreglo de IDs pasado como parámetro.
     * @param ids Arreglo de IDs de las categorías de contacto que se quiere eliminar.
     */
    public void deleteCategory(Object... ids) {
        delete(1, ids);
    }

}
