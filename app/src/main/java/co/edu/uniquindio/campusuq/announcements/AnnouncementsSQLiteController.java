package co.edu.uniquindio.campusuq.announcements;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para las tablas Anuncio y Anuncio_Enlace.
 */
public class AnnouncementsSQLiteController extends SQLiteController {

    private static final String tablename = "Anuncio";
    public static final String columns[] =
            {"_ID", "Usuario_ID", "Tipo", "Nombre", "Fecha", "Descripcion", "Leido"};

    private static final String linkTablename = "Anuncio_Enlace";
    public static final String linkColumns[] = {"_ID", "Anuncio_ID", "Tipo", "Enlace"};

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public AnnouncementsSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index Parametro que permite elegir entre la tabla Anuncio y la tabla Anuncio_Enlace.
     * @return Nombre de la tabla elegida.
     */
    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, linkTablename}[index];
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index Parametro que permite elegir entre la tabla Anuncio y la tabla Anuncio_Enlace.
     * @return Nombre de las columnas elegidas.
     */
    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, linkColumns}[index];
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas para la funcion update a
     * la clase base.
     * @param index Parametro que permite elegir entre la tabla Anuncio y la tabla Anuncio_Enlace,
     *              para la tabla Anuncio se actualizan todas las columnas excepto la ultima la cual
     *              corresponde a la marca de elemento leido.
     * @return Nombre de las columnas elegidas.
     */
    @Override
    protected String[] getUpdateColumns(int index) {
        return new String[][]{Arrays.copyOfRange(columns, 0, columns.length - 1),
                linkColumns}[index];
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Anuncio.
     * @return Cadena con las instrucciones SQL para crear la tabla Anuncio.
     */
    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " INTEGER NOT NULL, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " TEXT NOT NULL, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " INTEGER NOT NULL)";
    }

    /**
     * Selecciona un arreglo de anuncios desde la base de datos permitiendo definir opcinalmente un
     * maximo numero de anuncios a seleccionar.
     * @param limit Maximo numero de anuncios a seleccionar.
     * @param selection Sentencia WHERE para filtrar los anuncios que se obtendran de la base de
     *                  datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de anuncios de la base de datos.
     */
    public ArrayList<Announcement> select(String limit, String selection, String... selectionArgs) {
        ArrayList<Announcement> announcements = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[4] + " DESC", limit);

        if (c.moveToFirst()) do {
            announcements.add(new Announcement(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3), c.getString(4), c.getString(5),
                    c.getString(6)));
        } while (c.moveToNext());

        c.close();
        return announcements;
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Anuncio_Enlace.
     * @return Cadena con las instrucciones SQL para crear la tabla Anuncio_Enlace.
     */
    public static String createLinkTable(){
        return "CREATE TABLE " + linkTablename + '(' + linkColumns[0] + " INTEGER PRIMARY KEY, " +
                linkColumns[1] + " INTEGER NOT NULL REFERENCES " + tablename + '(' +
                columns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                linkColumns[2] + " TEXT NOT NULL, " + linkColumns[3] + " TEXT UNIQUE)";
    }

    /**
     * Selecciona un arreglo de enlaces de anuncios desde la base de datos.
     * @param selection Sentencia WHERE para filtrar los enlaces de anuncios que se obtendran de la
     *                  base de datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de enlaces de anuncios de la base de datos.
     */
    public ArrayList<AnnouncementLink> selectLink(String selection, String... selectionArgs) {
        ArrayList<AnnouncementLink> links = new ArrayList<>();

        Cursor c = db.query(linkTablename, null, selection, selectionArgs, null,
                null, linkColumns[0] + " ASC");
        if (c.moveToFirst()) do {
            links.add(new AnnouncementLink(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3)));
        } while (c.moveToNext());
        c.close();

        return links;
    }

    /**
     * Inserta un enlace de anuncio en la base de datos, de acuerdo a los valores de las columnas
     * pasados como parámetros.
     * @param values Valores de las columnas del enlace de anuncio a insertar.
     */
    public void insertLink(Object... values) {
        insert(1, values);
    }

    /**
     * Actualiza un enlace de anuncio en la base de datos de acuerdo a los valores de las columnas
     * pasados como parámetros, siendo el último de estos la ID de la fila a modificar.
     * @param values Valores de las columnas del enlace de anuncio a actualizar seguidos de la ID de
     *               dicho enlace de anuncio.
     */
    public void updateLink(Object... values) {
        update(1, values);
    }

    /**
     * Elimina un conjunto de enlaces de anuncios de la base de datos.
     * @param ids Conjunto de IDs de los enlaces de anuncios que se desea eliminar.
     */
    public void deleteLink(Object... ids) {
        delete(1, ids);
    }

}
