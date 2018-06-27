package co.edu.uniquindio.campusuq.users;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

public class UsersSQLiteController extends SQLiteController {

    private static final String tablename = "Usuario";
    public static final String columns[] = {"_ID", "Nombre", "Correo", "Telefono", "Direccion",
            "Documento", "Contrasena", "Clave_Api", "Administrador"};

    public UsersSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index No utilizado.
     * @return Nombre de la tabla Usuario.
     */
    @Override
    protected String getTablename(int index) {
        return tablename;
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index No utilizado.
     * @return Nombre de las columnas de la tabla Usuario.
     */
    @Override
    protected String[] getColumns(int index) {
        return columns;
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Usuario.
     * @return Cadena con las instrucciones SQL para crear la tabla Usuario.
     */
    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL, " + columns[2] + " TEXT NOT NULL UNIQUE, " +
                columns[3] + " TEXT, " + columns[4] + " TEXT, " + columns[5] + " TEXT, " +
                columns[6] + " TEXT NOT NULL, " + columns[7] + " TEXT NOT NULL, " +
                columns[8] + " TEXT NOT NULL)";
    }

    public ArrayList<User> select() {
        ArrayList<User> users = new ArrayList<>();
        Cursor c = db.query(tablename, null, null, null,
                null, null, columns[0] + " ASC");

        if (c.moveToFirst()) do {
            users.add(new User(c.getString(0), c.getString(1), c.getString(2),
                    c.isNull(3) ? null : c.getString(3),
                    c.isNull(4) ? null : c.getString(4),
                    c.isNull(5) ? null : c.getString(5), c.getString(6), c.getString(7),
                    c.getString(8)));
        } while(c.moveToNext());

        c.close();
        return users;
    }

}
