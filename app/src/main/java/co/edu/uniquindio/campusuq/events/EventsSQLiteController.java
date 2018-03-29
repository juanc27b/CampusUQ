package co.edu.uniquindio.campusuq.events;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class EventsSQLiteController {

    private static final String NOMBRE_TABLA = "Evento";
    static final String CAMPOS_TABLA[] = new String[]{"_ID", "Nombre"};

    private static final String NOMBRE_CATEGORIA = "Evento_Categoria";
    static final String CAMPOS_CATEGORIA[] = new String[]{"_ID", "Abreviacion", "Nombre"};

    private static final String NOMBRE_PERIODO = "Evento_Periodo";
    static final String CAMPOS_PERIODO[] = new String[]{"_ID", "Nombre"};

    private static final String NOMBRE_FECHA = "Evento_Fecha";
    public static final String CAMPOS_FECHA[] = new String[]{"_ID", "Tipo", "Fecha"};

    private static final String NOMBRE_RELACION = "Evento_Relacion";
    public static final String CAMPOS_RELACION[] = new String[]{"Categoria_ID", "Evento_ID", "Periodo_ID", "Fecha_ID"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public EventsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? TEXT NOT NULL UNIQUE )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        return builder.toString();
    }

    public ArrayList<Event> select(String selection, String[] selectionArgs) {
        ArrayList<Event> events = new ArrayList<>();
        Cursor c = db.query(NOMBRE_TABLA, CAMPOS_TABLA, selection, selectionArgs,
                null, null, CAMPOS_TABLA[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String _ID = c.getString(0);
                String name = c.getString(1);
                Event event = new Event(_ID, name);
                events.add(event);
            } while (c.moveToNext());
        }
        c.close();
        return events;
    }

    public void insert(String... campos) {
        String insertar = "INSERT INTO ? (?,?) VALUES (?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1]
        });
    }

    public void delete() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        db.execSQL(builder.toString());
    }

    public static String createCategoryTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL UNIQUE )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[2]);
        return builder.toString();
    }

    public ArrayList<EventCategory> selectCategory(String selection, String[] selectionArgs) {
        ArrayList<EventCategory> categories = new ArrayList<>();
        Cursor c = db.query(NOMBRE_CATEGORIA, CAMPOS_CATEGORIA, selection, selectionArgs,
                null, null, CAMPOS_CATEGORIA[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String abbreviation = c.getString(1);
                String name = c.getString(2);
                EventCategory category = new EventCategory(id, abbreviation, name);
                categories.add(category);
            } while (c.moveToNext());
        }
        c.close();
        return categories;
    }

    public void insertCategory(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?) VALUES (?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[2]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2]
        });
    }

    public void deleteCategory() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        db.execSQL(builder.toString());
    }

    public static String createPeriodTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? TEXT NOT NULL UNIQUE )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_PERIODO);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_PERIODO[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_PERIODO[1]);
        return builder.toString();
    }

    ArrayList<EventPeriod> selectPeriod(String selection, String[] selectionArgs) {
        ArrayList<EventPeriod> periods = new ArrayList<>();
        Cursor c = db.query(NOMBRE_PERIODO, CAMPOS_PERIODO, selection, selectionArgs,
                null, null, CAMPOS_PERIODO[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String name = c.getString(1);
                EventPeriod period = new EventPeriod(id, name);
                periods.add(period);
            } while (c.moveToNext());
        }
        c.close();
        return periods;
    }

    public void insertPeriod(String... campos) {
        String insertar = "INSERT INTO ? (?,?) VALUES (?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_PERIODO);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_PERIODO[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_PERIODO[1]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1]
        });
    }

    public void deletePeriod() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_PERIODO);
        db.execSQL(builder.toString());
    }

    public static String createDateTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? TEXT NOT NULL, " +
                "? TEXT NOT NULL, UNIQUE (?, ?) )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_FECHA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[2]);
        return builder.toString();
    }

    public ArrayList<EventDate> selectDate(String selection, String[] selectionArgs) {
        ArrayList<EventDate> dates = new ArrayList<>();
        Cursor c = db.query(NOMBRE_FECHA, CAMPOS_FECHA, selection, selectionArgs,
                null, null, CAMPOS_FECHA[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String type = c.getString(1);
                String date = c.getString(2);
                EventDate eventDate = new EventDate(id, type, date);
                dates.add(eventDate);
            } while (c.moveToNext());
        }
        c.close();
        return dates;
    }

    public void insertDate(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?) VALUES (?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_FECHA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[2]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2]
        });
    }

    public void deleteDate() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_FECHA);
        db.execSQL(builder.toString());
    }

    public static String createRelationTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER NOT NULL, ? INTEGER NOT NULL, ? INTEGER NOT NULL, " +
                "? INTEGER NOT NULL, PRIMARY KEY (?, ?, ?, ?), FOREIGN KEY (?) REFERENCES ? (?), " +
                "FOREIGN KEY (?) REFERENCES ? (?), FOREIGN KEY (?) REFERENCES ? (?), FOREIGN KEY (?) REFERENCES ? (?) )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_RELACION);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_PERIODO);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_PERIODO[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_FECHA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FECHA[0]);
        return builder.toString();
    }

    public ArrayList<EventRelation> selectRelation(String[] columns, String selection, String[] selectionArgs) {
        ArrayList<EventRelation> relations = new ArrayList<>();
        String orderBy = CAMPOS_RELACION[0]+" ASC, "+CAMPOS_RELACION[1]+" ASC, "+
                CAMPOS_RELACION[2]+" ASC, "+CAMPOS_RELACION[3]+" ASC";
        Cursor c = db.query(true, NOMBRE_RELACION, columns, selection, selectionArgs,
                null, null, orderBy, null);
        if (c.moveToFirst()) {
            do {
                String category_ID = null;
                String event_ID = null;
                String period_ID = null;
                String date_ID = null;
                int i = 0;
                switch (columns.length) {
                    case 4:
                        category_ID = c.getString(i++);
                    case 3:
                        date_ID = c.getString(columns.length-1);
                    case 2:
                        period_ID = c.getString(i+1);
                    case 1:
                        event_ID = c.getString(i);
                    default:
                        break;
                }
                EventRelation relation = new EventRelation(category_ID, event_ID, period_ID, date_ID);
                relations.add(relation);
            } while (c.moveToNext());
        }
        c.close();
        return relations;
    }

    public void insertRelation(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?,?) VALUES (?,?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_RELACION);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_RELACION[3]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2],
                campos[3]
        });
    }

    public void deleteRelation() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_RELACION);
        db.execSQL(builder.toString());
    }

    public void destroy() {
        usdbh.close();
    }

}
