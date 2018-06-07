package co.edu.uniquindio.campusuq.objects;

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
 * Controlador de la base de datos para la tabla Objeto.
 */
public class ObjectsSQLiteController {

    private static final String tablename = "Objeto";
    static final String columns[] = {"_ID", "Usuario_Perdio_ID", "Nombre", "Lugar", "Fecha_Perdio",
            "Fecha", "Descripcion", "Imagen", "Usuario_Encontro_ID", "Leido"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public ObjectsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
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

    /**
     * Inserta un objeto perdido en la base de datos, de acuerdo a los valores de las columnas
     * pasados como parámetros.
     * @param values Valores de las columnas del objeto perdido a insertar.
     */
    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    /**
     * Actualiza un objeto perdido en la base de datos de acuerdo a los valores de las columnas
     * (todas menos la columna Leido) pasados como parámetros, siendo el último de estos la ID de la
     * fila a modificar.
     * @param values Valores de las columnas del objeto perdido a actualizar seguidos de la ID de
     *               dicho objeto perdido.
     */
    public void update(Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " + TextUtils.join(" = ?, ",
                Arrays.copyOfRange(columns, 0, columns.length - 1)) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    /**
     * Marca como leidos un conjunto de objetos perdidos de la base de datos.
     * @param ids Conjunto de IDs de los objetos perdidos que se desea marcar como leidos.
     */
    void readed(Object... ids) {
        db.execSQL("UPDATE " + tablename + " SET " +
                columns[9] + " = 1 WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Marca como no leidos un conjunto de objetos perdidos de la base de datos.
     * @param ids Conjunto de IDs de los objetos perdidos que se desea marcar como no leidos.
     */
    void unreaded(Object... ids) {
        db.execSQL("UPDATE " + tablename + " SET " +
                columns[9] + " = 0 WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Elimina un conjunto de objetos perdidos de la base de datos.
     * @param ids Conjunto de IDs de los objetos perdidos que se desea eliminar.
     */
    public void delete(Object... ids) {
        db.execSQL("DELETE FROM " + tablename + " WHERE " + columns[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Destruye el controlador de la base de datos.
     */
    public void destroy() {
        usdbh.close();
    }

}
