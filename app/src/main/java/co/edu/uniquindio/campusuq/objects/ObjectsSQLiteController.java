package co.edu.uniquindio.campusuq.objects;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para la tabla Objeto.
 */
public class ObjectsSQLiteController extends SQLiteController {

    private static final String tablename = "Objeto";
    static final String columns[] = {"_ID", "Usuario_Perdio_ID", "Nombre", "Lugar", "Fecha_Perdio",
            "Fecha", "Descripcion", "Imagen", "Usuario_Encontro_ID", "Leido"};

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public ObjectsSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index No utilizado.
     * @return Nombre de la tabla Objeto.
     */
    @Override
    protected String getTablename(int index) {
        return tablename;
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index No utilizado.
     * @return Nombre de las columnas de la tabla Objeto.
     */
    @Override
    protected String[] getColumns(int index) {
        return columns;
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas para la funcion update a
     * la clase base.
     * @param index No utilizado.
     * @return Columnas de la tabla Objeto excepto la ultima la cual corresponde a la marca de
     *         elemento leido.
     */
    @Override
    protected String[] getUpdateColumns(int index) {
        return Arrays.copyOfRange(columns, 0, columns.length - 1);
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Objeto.
     * @return Cadena con las instrucciones SQL para crear la tabla Objeto.
     */
    public static String createTable() {
        return "CREATE TABLE " + tablename + '('+columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " INTEGER NOT NULL, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " TEXT NOT NULL, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " TEXT NOT NULL, " +
                columns[7] + " TEXT UNIQUE, " + columns[8] + " INTEGER, " +
                columns[9] + " INTEGER NOT NULL)";
    }

    /**
     * Selecciona un arreglo de objetos perdidos desde la base de datos permitiendo difinir
     * opcinalmente un maximo numero de objetos perdidos a seleccionar.
     * @param limit Maximo numero de objetos perdidos a seleccionar.
     * @param selection Sentencia WHERE para filtrar los objetos perdidos que se obtendran de la
     *                  base de datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de objetos perdidos de la base de datos.
     */
    public ArrayList<LostObject> select(String limit, String selection, String... selectionArgs) {
        ArrayList<LostObject> objects = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[5] + " DESC", limit);

        if (c.moveToFirst()) do {
            objects.add(new LostObject(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.isNull(7) ? null : c.getString(7),
                    c.isNull(8) ? null : c.getString(8), c.getString(9)));
        } while (c.moveToNext());

        c.close();
        return objects;
    }

}
