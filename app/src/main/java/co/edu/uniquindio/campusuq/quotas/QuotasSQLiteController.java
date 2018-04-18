package co.edu.uniquindio.campusuq.quotas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Controlador de la base de datos para la tabla Cupo.
 */
public class QuotasSQLiteController {

    private static final String tablename = "Cupo";
    static final String columns[] = {"_ID", "Tipo", "Nombre", "Cupo"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public QuotasSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Cupo.
     * @return Cadena con las instrucciones SQL para crear la tabla Cupo.
     */
    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL, " + columns[2] + " TEXT NOT NULL UNIQUE, " +
                columns[3] + " INTEGER NOT NULL)";
    }

    /**
     * Selecciona un arreglo de cupos desde la base de datos.
     * @param selection Sentencia WHERE para filtrar los cupos que se obtendran de la base de datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de cupos de la base de datos filtrados de acuerdo con la selección.
     */
    public ArrayList<Quota> select(String selection, String... selectionArgs) {
        ArrayList<Quota> quotas = new ArrayList<>();

        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[0] + " ASC");
        if (c.moveToFirst()) do {
            quotas.add(new Quota(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3)));
        } while (c.moveToNext());
        c.close();

        return quotas;
    }

    /**
     * Inserta un cupo en la base de datos, de acuerdo a los valores de las columnas pasados como
     * parámetros.
     * @param values Valores de las columnas del cupo a insertar.
     */
    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    /**
     * Actualiza un cupo en la base de datos de acuerdo a los valores de las columnas pasados como
     * parámetros, siendo el último de estos la ID de la fila a modificar.
     * @param values Valores de las columnas del cupo a actualizar seguidos de la ID de dicho cupo.
     */
    public void update(Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " +
                TextUtils.join(" = ?, ", columns) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    /**
     * Elimina un conjunto de cupos de la base de datos.
     * @param ids Conjunto de IDs de los cupos que se desea eliminar.
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
