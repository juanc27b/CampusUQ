package co.edu.uniquindio.campusuq.informations;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para las tablas Informacion e Informacion_Categoria.
 */
public class InformationsSQLiteController extends SQLiteController {

    private static final String tablename = "Informacion";
    public static final String columns[] = {"_ID", "Categoria_ID", "Nombre", "Contenido"};

    private static final String categoryTablename = "Informacion_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace", "Fecha"};

    public InformationsSQLiteController(Context context, int version) {
        super(context, version);
    }

    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, categoryTablename}[index];
    }

    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, categoryColumns}[index];
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Informacion.
     * @return Cadena con las instrucciones SQL para crear la tabla Informacion.
     */
    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " INTEGER NOT NULL REFERENCES " + categoryTablename + '(' +
                categoryColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                columns[2] + " TEXT NOT NULL UNIQUE, " + columns[3] + " TEXT NOT NULL)";
    }

    public ArrayList<Information> select(String selection, String... selectionArgs) {
        ArrayList<Information> informations = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[0] + " ASC");

        if (c.moveToFirst()) do {
            informations.add(new Information(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3)));
        } while (c.moveToNext());

        c.close();
        return informations;
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla
     * Informacion_Categoria.
     * @return Cadena con las instrucciones SQL para crear la tabla Informacion_Categoria.
     */
    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE, " +
                categoryColumns[2] + " TEXT NOT NULL, " +
                categoryColumns[3] + " TEXT NOT NULL)";
    }

    public ArrayList<InformationCategory> selectCategory(String selection,
                                                         String... selectionArgs) {
        ArrayList<InformationCategory> categories = new ArrayList<>();
        Cursor c = db.query(categoryTablename, null, selection, selectionArgs,
                null, null, null);

        if (c.moveToFirst()) do {
            categories.add(new InformationCategory(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3)));
        } while (c.moveToNext());

        c.close();
        return categories;
    }

    public void insertCategory(Object... values) {
        insert(1, values);
    }

    public void updateCategory(Object... values) {
        update(1, values);
    }

}
