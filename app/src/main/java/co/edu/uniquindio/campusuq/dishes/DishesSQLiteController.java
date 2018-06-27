package co.edu.uniquindio.campusuq.dishes;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para la tabla Plato.
 */
public class DishesSQLiteController extends SQLiteController {

    private static final String tablename = "Plato";
    static final String columns[] = {"_ID", "Nombre", "Descripcion", "Precio", "Imagen"};

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public DishesSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index No utilizado.
     * @return Nombre de la tabla Plato.
     */
    @Override
    protected String getTablename(int index) {
        return tablename;
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index No utilizado.
     * @return Nombre de las columnas de la tabla Plato.
     */
    @Override
    protected String[] getColumns(int index) {
        return columns;
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

}