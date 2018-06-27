package co.edu.uniquindio.campusuq.events;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para las tablas Evento, Evento_Categoria, Evento_Periodo,
 * Evento_Fecha y Evento_Relacion, que hacen parte de la funcionalidad de Calendario academico.
 */
public class EventsSQLiteController extends SQLiteController {

    private static final String tablename = "Evento";
    static final String columns[] = {"_ID", "Nombre"};

    private static final String categoryTablename = "Evento_Categoria";
    static final String categoryColumns[] = {"_ID", "Abreviacion", "Nombre"};

    private static final String periodTablename = "Evento_Periodo";
    static final String periodColumns[] = {"_ID", "Nombre"};

    private static final String dateTablename = "Evento_Fecha";
    public static final String dateColumns[] = {"_ID", "Tipo", "Fecha"};

    private static final String relationTablename = "Evento_Relacion";
    public static final String relationColumns[] =
            {"Categoria_ID", "Evento_ID", "Periodo_ID", "Fecha_ID"};

    /**
     * Constructor del controlador de la base de datos, el cual utiliza un SQLiteHelper para
     * asegurar que las tablas se creen al crear la base de datos, y obtiene una instancia de la
     * base de datos con permisos de escritura.
     * @param context Contexto necesario para crear un SQLiteHelper.
     * @param version Versión de la base de datos.
     */
    public EventsSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index Parametro que permite elegir entre la tabla Evento, la tabla Evento_Categoria,
     *              la tabla Evento_Periodo, la tabla Evento_Fecha y la tabla Evento_Relacion.
     * @return Nombre de la tabla elegida.
     */
    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, categoryTablename, periodTablename, dateTablename,
                relationTablename}[index];
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index Parametro que permite elegir entre la tabla Evento, la tabla Evento_Categoria,
     *              la tabla Evento_Periodo, la tabla Evento_Fecha y la tabla Evento_Relacion.
     * @return Nombre de las columnas elegidas.
     */
    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, categoryColumns, periodColumns, dateColumns,
                relationColumns}[index];
    }

    /**
     * Crea una cadena válida para crear la tabla Evento en la base de datos.
     * @return Cadena SQL para crear la tabla Evento.
     */
    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL UNIQUE)";
    }

    /**
     * Obtiene de la base de datos un arreglo de eventos de acuerdo a los filtros pasados como
     * parámetro a la función, y los ordena por ID en orden ascendente.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de eventos extraído de la base de datos.
     */
    public ArrayList<Event> select(String selection, String... selectionArgs) {
        ArrayList<Event> events = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[0] + " ASC");

        if (c.moveToFirst()) do {
            events.add(new Event(c.getString(0), c.getString(1)));
        } while (c.moveToNext());

        c.close();
        return events;
    }

    /**
     * Elimina todas las filas de la tabla Evento.
     */
    public void delete() {
        db.execSQL("DELETE FROM " + tablename);
    }

    /**
     * Crea una cadena válida para crear la tabla Evento_Categoria en la base de datos.
     * @return Cadena SQL para crear la tabla Evento_Categoria.
     */
    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE, " +
                categoryColumns[2] + " TEXT NOT NULL UNIQUE)";
    }

    /**
     * Obtiene de la base de datos un arreglo de categorias de evento de acuerdo al limite y los
     * filtros pasados como parámetro a la función, y los ordena por ID en orden ascendente.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de categorias de evento extraído de la base de datos.
     */
    public ArrayList<EventCategory> selectCategory(String limit, String selection,
                                                   String... selectionArgs) {
        ArrayList<EventCategory> categories = new ArrayList<>();
        Cursor c = db.query(categoryTablename, null, selection, selectionArgs,
                null, null, categoryColumns[0] + " ASC", limit);

        if (c.moveToFirst()) do {
            categories.add(new EventCategory(c.getString(0), c.getString(1),
                    c.getString(2)));
        } while (c.moveToNext());

        c.close();
        return categories;
    }

    /**
     * Inserta en la base de datos una categoria de evento con los valores de las columnas pasados
     * como parámetro a la función
     * @param values Valores de las columnas de la categoria de evento.
     */
    public void insertCategory(Object... values) {
        insert(1, values);
    }

    /**
     * Elimina todas las filas de la tabla Evento_Categoria.
     */
    public void deleteCategory() {
        db.execSQL("DELETE FROM " + categoryTablename);
    }

    /**
     * Crea una cadena válida para crear la tabla Evento_Periodo en la base de datos.
     * @return Cadena SQL para crear la tabla Evento_Periodo.
     */
    public static String createPeriodTable(){
        return "CREATE TABLE " + periodTablename + '(' +
                periodColumns[0] + " INTEGER PRIMARY KEY, " +
                periodColumns[1] + " TEXT NOT NULL UNIQUE)";
    }

    /**
     * Obtiene de la base de datos un arreglo de periodos de evento de acuerdo a los filtros pasados
     * como parámetro a la función, y los ordena por ID en orden ascendente.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de periodos de evento extraído de la base de datos.
     */
    ArrayList<EventPeriod> selectPeriod(String selection, String... selectionArgs) {
        ArrayList<EventPeriod> periods = new ArrayList<>();
        Cursor c = db.query(periodTablename, null, selection, selectionArgs, null,
                null, periodColumns[0] + " ASC");

        if (c.moveToFirst()) do {
            periods.add(new EventPeriod(c.getString(0), c.getString(1)));
        } while (c.moveToNext());

        c.close();
        return periods;
    }

    /**
     * Inserta en la base de datos un periodo de evento con los valores de las columnas pasados como
     * parámetro a la función
     * @param values Valores de las columnas del periodo de evento.
     */
    public void insertPeriod(Object... values) {
        insert(2, values);
    }

    /**
     * Elimina todas las filas de la tabla Evento_Periodo.
     */
    public void deletePeriod() {
        db.execSQL("DELETE FROM " + periodTablename);
    }

    /**
     * Crea una cadena válida para crear la tabla Evento_Fecha en la base de datos.
     * @return Cadena SQL para crear la tabla Evento_Fecha.
     */
    public static String createDateTable(){
        return "CREATE TABLE " + dateTablename + '(' + dateColumns[0] + " INTEGER PRIMARY KEY, " +
                dateColumns[1] + " TEXT NOT NULL, " + dateColumns[2] + " TEXT NOT NULL, " +
                "UNIQUE(" + dateColumns[1] + ", " + dateColumns[2] + "))";
    }

    /**
     * Obtiene de la base de datos un arreglo de fechas de evento de acuerdo a los filtros pasados
     * como parámetro a la función, y las ordena por ID en orden ascendente.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de fechas de evento extraído de la base de datos.
     */
    public ArrayList<EventDate> selectDate(String selection, String... selectionArgs) {
        ArrayList<EventDate> dates = new ArrayList<>();
        Cursor c = db.query(dateTablename, null, selection, selectionArgs, null,
                null, dateColumns[0] + " ASC");

        if (c.moveToFirst()) do {
            dates.add(new EventDate(c.getString(0), c.getString(1), c.getString(2)));
        } while (c.moveToNext());

        c.close();
        return dates;
    }

    /**
     * Inserta en la base de datos una fecha de evento con los valores de las columnas pasados como
     * parámetro a la función
     * @param values Valores de las columnas de la fecha de evento.
     */
    public void insertDate(Object... values) {
        insert(3, values);
    }

    /**
     * Elimina todas las filas de la tabla Evento_Fecha.
     */
    public void deleteDate() {
        db.execSQL("DELETE FROM " + dateTablename);
    }

    /**
     * Crea una cadena válida para crear la tabla Evento_Relacion en la base de datos.
     * @return Cadena SQL para crear la tabla Evento_Relacion.
     */
    public static String createRelationTable(){
        return "CREATE TABLE " + relationTablename + '(' +
                relationColumns[0] + " INTEGER NOT NULL REFERENCES " + categoryTablename + '(' +
                categoryColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                relationColumns[1] + " INTEGER NOT NULL REFERENCES " + tablename + '(' +
                columns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                relationColumns[2] + " INTEGER NOT NULL REFERENCES " + periodTablename + '(' +
                periodColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                relationColumns[3] + " INTEGER NOT NULL REFERENCES " + dateTablename + '(' +
                dateColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                "PRIMARY KEY(" + relationColumns[0] + ", " + relationColumns[1] + ", " +
                relationColumns[2] + ", " + relationColumns[3] + "))";
    }

    /**
     * Obtiene de la base de datos un arreglo de relaciones de evento de acuerdo a las columnas y
     * los filtros pasados como parámetro a la función, y las ordena de forma ascendente.
     * @param columns Columnas a consultar.
     * @param selection Cláusula SQL WHERE para filtrar los resultados.
     * @param selectionArgs Parámetros de la cláusula SQL WHERE.
     * @return Arreglo de relaciones de evento extraído de la base de datos.
     */
    public ArrayList<EventRelation> selectRelation(String[] columns, String selection,
                                                   String... selectionArgs) {
        ArrayList<EventRelation> relations = new ArrayList<>();
        Cursor c = db.query(true, relationTablename, columns, selection, selectionArgs,
                null, null, relationColumns[0] + " ASC, " +
                        relationColumns[1] + " ASC, " + relationColumns[2] + " ASC, " +
                        relationColumns[3] + " ASC", null);

        if (c.moveToFirst()) do {
            String category_ID = null;
            String event_ID = null;
            String period_ID = null;
            String date_ID = null;
            int i = 0;
            switch (columns.length) {
                case 4:
                    category_ID = c.getString(i++);
                case 3:
                    date_ID = c.getString(columns.length - 1);
                case 2:
                    period_ID = c.getString(i + 1);
                case 1:
                    event_ID = c.getString(i);
                default:
                    break;
            }
            relations.add(new EventRelation(category_ID, event_ID, period_ID, date_ID));
        } while (c.moveToNext());

        c.close();
        return relations;
    }

    /**
     * Inserta en la base de datos una relacion de evento con los valores de las columnas pasados
     * como parámetro a la función
     * @param values Valores de las columnas de la relacion de evento.
     */
    public void insertRelation(Object... values) {
        insert(4, values);
    }

}
