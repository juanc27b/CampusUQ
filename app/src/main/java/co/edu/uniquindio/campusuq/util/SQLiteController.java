package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.Collections;

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

    protected abstract String getTablename(int index);
    protected abstract String[] getColumns(int index);

    protected String[] getUpdateColumns(int index) {
        return getColumns(index);
    }

    protected void insert(int index, Object... values) {
        db.execSQL("INSERT INTO " + getTablename(index) + '(' +
                TextUtils.join(", ", getColumns(index)) + ") VALUES(" +
                TextUtils.join(", ", Collections
                        .nCopies(getColumns(index).length, '?')) + ')', values);
    }

    protected void update(int index, Object... values) {
        db.execSQL("UPDATE " + getTablename(index) + " SET " +
                TextUtils.join(" = ?, ", getUpdateColumns(index)) + " = ? WHERE " +
                getUpdateColumns(index)[0] + " = ?", values);
    }

    protected void delete(int index, Object... ids) {
        db.execSQL("DELETE FROM " + getTablename(index) + " WHERE " +
                getColumns(index)[0] + " IN(" +
                TextUtils.join(", ", Collections.nCopies(ids.length, '?')) + ')', ids);
    }

    /**
     * Inserta un ítem en la base de datos, de acuerdo a los valores de las columnas pasados como
     * parámetros.
     * @param values Valores de las columnas del plato a insertar.
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

    /**
     * Destruye el controlador de la base de datos.
     */
    public void destroy() {}

}
