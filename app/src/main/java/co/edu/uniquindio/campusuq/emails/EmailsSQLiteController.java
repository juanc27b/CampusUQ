package co.edu.uniquindio.campusuq.emails;

import android.content.Context;
import android.database.Cursor;

import java.math.BigInteger;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para la tabla Correo.
 */
public class EmailsSQLiteController extends SQLiteController {

    private static final String tablename = "Correo";
    public static final String columns[] =
            {"_ID", "Nombre", "De", "Para", "Fecha", "Retazo", "Contenido", "History_ID"};

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public EmailsSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index No utilizado.
     * @return Nombre de la tabla Correo.
     */
    @Override
    protected String getTablename(int index) {
        return tablename;
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index No utilizado.
     * @return Nombre de las columnas de la tabla Correo.
     */
    @Override
    protected String[] getColumns(int index) {
        return columns;
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Correo.
     * @return Cadena con las instrucciones SQL para crear la tabla Correo.
     */
    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " TEXT PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " TEXT NOT NULL, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " TEXT NOT NULL, " +
                columns[7] + " TEXT NOT NULL)";
    }

    /**
     * Selecciona un arreglo de correos desde la base de datos permitiendo definir opcinalmente un
     * maximo numero de correos a seleccionar.
     * @param limit Maximo numero de correos a seleccionar.
     * @return Arreglo de correos de la base de datos.
     */
    public ArrayList<Email> select(String limit) {
        ArrayList<Email> emails = new ArrayList<>();
        Cursor c = db.query(tablename, null, null, null,
                null, null, columns[4] + " DESC", limit);

        if (c.moveToFirst()) do {
            emails.add(new Email(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    new BigInteger(c.getString(7))));
        } while (c.moveToNext());

        c.close();
        return emails;
    }

}
