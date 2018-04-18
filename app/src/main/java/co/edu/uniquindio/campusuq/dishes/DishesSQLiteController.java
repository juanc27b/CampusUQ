package co.edu.uniquindio.campusuq.dishes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Controlador de la base de datos para la tabla Plato.
 */
public class DishesSQLiteController {

    private static final String tablename = "Plato";
    static final String columns[] = {"_ID", "Nombre", "Descripcion", "Precio", "Imagen"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public DishesSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Plato.
     * @return Cadena con las instrucciones SQL para crear la tabla Plato.
     */
    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL UNIQUE, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " INTEGER NOT NULL, " + columns[4] + " TEXT UNIQUE)";
    }

    /**
     * Selecciona un arreglo de platos desde la base de datos permitiendo definir opcinalmente un
     * maximo numero de platos a seleccionar.
     * @param limit Maximo numero de platos a seleccionar.
     * @return Arreglo de platos de la base de datos.
     */
    public ArrayList<Dish> select(String limit) {
        ArrayList<Dish> dishes = new ArrayList<>();

        Cursor c = db.query(tablename, null, null, null,
                null, null, columns[0] + " DESC", limit);
        if (c.moveToFirst()) do {
            dishes.add(new Dish(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.isNull(4) ? null : c.getString(4)));
        } while (c.moveToNext());
        c.close();

        return dishes;
    }

    /**
     * Inserta un plato en la base de datos, de acuerdo a los valores de las columnas pasados como
     * parámetros.
     * @param values Valores de las columnas del plato a insertar.
     */
    public void insert(Object... values) {
        db.execSQL("INSERT INTO " + tablename + '(' +
                TextUtils.join(", ", columns) + ") VALUES(" +
                TextUtils.join(", ", Collections.nCopies(columns.length, '?')) +
                ')', values);
    }

    /**
     * Actualiza un plato en la base de datos de acuerdo a los valores de las columnas pasados como
     * parámetros, siendo el último de estos la ID de la fila a modificar.
     * @param values Valores de las columnas del plato a actualizar seguidos de la ID de dicho
     *               plato.
     */
    public void update(Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " +
                TextUtils.join(" = ?, ", columns) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    /**
     * Elimina un conjunto de platos de la base de datos.
     * @param ids Conjunto de IDs de los platos que se desea eliminar.
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