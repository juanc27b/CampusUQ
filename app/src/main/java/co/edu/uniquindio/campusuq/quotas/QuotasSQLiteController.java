package co.edu.uniquindio.campusuq.quotas;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para la tabla Cupo.
 */
public class QuotasSQLiteController extends SQLiteController {

    private static final String tablename = "Cupo";
    static final String columns[] = {"_ID", "Tipo", "Nombre", "Cupo"};

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    public QuotasSQLiteController(Context context, int version) {
        super(context, version);
    }

    @Override
    protected String getTablename(int index) {
        return tablename;
    }

    @Override
    protected String[] getColumns(int index) {
        return columns;
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

}
