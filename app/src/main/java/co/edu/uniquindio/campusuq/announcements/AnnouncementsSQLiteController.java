package co.edu.uniquindio.campusuq.announcements;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Controlador de la base de datos para las tablas Anuncio y Anuncio_Enlace.
 */
public class AnnouncementsSQLiteController {

    private static final String tablename = "Anuncio";
    public static final String columns[] = {"_ID", "Usuario_ID", "Tipo", "Nombre", "Fecha",
            "Descripcion", "Leido"};

    private static final String linkTablename = "Anuncio_Enlace";
    public static final String linkColumns[] = {"_ID", "Anuncio_ID", "Tipo", "Enlace"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public AnnouncementsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
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
     * Inserta un anuncio en la base de datos, de acuerdo a los valores de las columnas pasados como
     * parámetros.
     * @param values Valores de las columnas del anuncio a insertar.
     */
    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    /**
     * Actualiza un anuncio en la base de datos de acuerdo a los valores de las columnas (todas
     * menos la columna Leido) pasados como parámetros, siendo el último de estos la ID de la fila a
     * modificar.
     * @param values Valores de las columnas del anuncio a actualizar seguidos de la ID de dicho
     *               anuncio.
     */
    public void update(Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " + TextUtils.join(" = ?, ",
                Arrays.copyOfRange(columns, 0, columns.length - 1)) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    /**
     * Marca como leidos un conjunto de anuncios de la base de datos.
     * @param ids Conjunto de IDs de los anuncios que se desea marcar como leidos.
     */
    void readed(Object... ids) {
        db.execSQL("UPDATE " + tablename + " SET " +
                columns[6] + " = 1 WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Marca como no leidos un conjunto de anuncios de la base de datos.
     * @param ids Conjunto de IDs de los anuncios que se desea marcar como no leidos.
     */
    void unreaded(Object... ids) {
        db.execSQL("UPDATE " + tablename + " SET " +
                columns[6] + " = 0 WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Elimina un conjunto de anuncios de la base de datos.
     * @param ids Conjunto de IDs de los anuncios que se desea eliminar.
     */
    public void delete(Object... ids) {
        db.execSQL("DELETE FROM " + tablename + " WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
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
     * Selecciona un arreglo de anuncios desde la base de datos.
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
        db.execSQL("INSERT INTO " + linkTablename + '(' +
                TextUtils.join(", ", linkColumns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(linkColumns.length, '?')) +
                ')', values);
    }

    /**
     * Actualiza un enlace de anuncio en la base de datos de acuerdo a los valores de las columnas
     * pasados como parámetros, siendo el último de estos la ID de la fila a modificar.
     * @param values Valores de las columnas del plato a actualizar seguidos de la ID de dicho
     *               enlace de anuncio.
     */
    public void updateLink(Object... values) {
        db.execSQL("UPDATE " + linkTablename + " SET " +
                TextUtils.join(" = ?, ", linkColumns) + " = ? WHERE " +
                linkColumns[0] + " = ?", values);
    }

    /**
     * Elimina un conjunto de enlaces de anuncios de la base de datos.
     * @param ids Conjunto de IDs de los enlaces de anuncios que se desea eliminar.
     */
    public void deleteLink(Object... ids) {
        db.execSQL("DELETE FROM " + linkTablename + " WHERE " + linkColumns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Destruye el controlador de la base de datos.
     */
    public void destroy() {
        usdbh.close();
    }

}
