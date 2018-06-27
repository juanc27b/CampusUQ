package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.Collections;

/**
 * Controlador de la base de datos del cual se derivan los controladores espesificos para la tabla o
 * tablas de cada funcionalidad.
 */
public abstract class SQLiteController {

    protected SQLiteDatabase db;

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public SQLiteController(Context context, int version) {
        db = SQLiteHelper.getDatabaseInstance(context, version);
    }

    /**
     * Funcion que debe ser sobreescrita por la clase derivada para que la clase base pueda obtener
     * el nombre de la tabla.
     * @param index Parametro opcional que permite elegir el nombre de la tabla cuando hay mas de
     *              una.
     * @return Nombre de la tabla.
     */
    protected abstract String getTablename(int index);

    /**
     * Funcion que debe ser sobreescrita por la clase derivada para que la clase base pueda obtener
     * las columnas de la tabla.
     * @param index Parametro opcional que permite elegir la columnas de la tabla cuando hay mas de
     *              una tabla.
     * @return Array de cadenas con los nombres de las columnas de la tabla.
     */
    protected abstract String[] getColumns(int index);

    /**
     * Funcion que puede ser sobreescrita por la clase derivada y permite definir un conjunto
     * distinto de columnas para la funcion update cuando hay una columna con la marca de lectura,
     * si no se sobreescribe se utilizaran las mismas columnas de la funcion getColumns.
     * @param index Parametro opcional que permite elegir la columnas de la tabla cuando hay mas de
     *              una tabla.
     * @return Array de cadenas con los nombres de las columnas de la tabla.
     */
    protected String[] getUpdateColumns(int index) {
        return getColumns(index);
    }

    /**
     * Inserta un ítem en la base de datos, de acuerdo al indice de la tabla y a los valores de las
     * columnas pasados como parámetros.
     * @param index Indice de la tabla.
     * @param values Valores de las columnas del ítem a insertar.
     */
    protected void insert(int index, Object... values) {
        db.execSQL("INSERT INTO " + getTablename(index) + '(' +
                TextUtils.join(", ", getColumns(index)) + ") VALUES(" +
                TextUtils.join(", ", Collections
                        .nCopies(getColumns(index).length, '?')) + ')', values);
    }

    /**
     * Actualiza un ítem en la base de datos de acuerdo al indice de la tabla y a los valores de las
     * columnas pasados como parámetros, siendo el último de estos la ID de la fila a modificar.
     * @param index Indice de la tabla.
     * @param values Valores de las columnas del ítem a actualizar seguidos de la ID de dicho ítem.
     */
    protected void update(int index, Object... values) {
        db.execSQL("UPDATE " + getTablename(index) + " SET " +
                TextUtils.join(" = ?, ", getUpdateColumns(index)) + " = ? WHERE " +
                getUpdateColumns(index)[0] + " = ?", values);
    }

    /**
     * Elimina un conjunto de ítems de la base de datos.
     * @param index Indice de la tabla.
     * @param ids Conjunto de IDs de los ítems que se desea eliminar.
     */
    protected void delete(int index, Object... ids) {
        db.execSQL("DELETE FROM " + getTablename(index) + " WHERE " +
                getColumns(index)[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Inserta un ítem en la base de datos, de acuerdo a los valores de las columnas pasados como
     * parámetros.
     * @param values Valores de las columnas del ítem a insertar.
     */
    public void insert(Object... values) {
        insert( 0, values);
    }

    /**
     * Actualiza un ítem en la base de datos de acuerdo a los valores de las columnas pasados como
     * parámetros, siendo el último de estos la ID de la fila a modificar.
     * @param values Valores de las columnas del ítem a actualizar seguidos de la ID de dicho ítem.
     */
    public void update(Object... values) {
        update( 0, values);
    }

    /**
     * Elimina un conjunto de ítems de la base de datos.
     * @param ids Conjunto de IDs de los ítems que se desea eliminar.
     */
    public void delete(Object... ids) {
        delete( 0, ids);
    }

    /**
     * Marca como leidos un conjunto de ítems de la base de datos.
     * @param ids Conjunto de IDs de los ítems que se desea marcar como leidos.
     */
    public void readed(Object... ids) {
        if (getColumns(0) != getUpdateColumns(0)) {
            db.execSQL("UPDATE " + getTablename(0) + " SET " +
                    getColumns(0)[getColumns(0).length - 1] + " = 1 WHERE " +
                    getColumns(0)[0] + " IN(" +
                    TextUtils.join(", ", Collections
                            .nCopies(ids.length, '?')) + ')', ids);
        }
    }

    /**
     * Marca como no leidos un conjunto de ítems de la base de datos.
     * @param ids Conjunto de IDs de los ítems que se desea marcar como no leidos.
     */
    public void unreaded(Object... ids) {
        if (getColumns(0) != getUpdateColumns(0)) {
            db.execSQL("UPDATE " + getTablename(0) + " SET " +
                    getColumns(0)[getColumns(0).length - 1] + " = 0 WHERE " +
                    getColumns(0)[0] + " IN(" +
                    TextUtils.join(", ", Collections
                            .nCopies(ids.length, '?')) + ')', ids);
        }
    }

}
