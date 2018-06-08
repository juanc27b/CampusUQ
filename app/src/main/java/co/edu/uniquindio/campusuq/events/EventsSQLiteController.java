package co.edu.uniquindio.campusuq.events;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.util.SQLiteController;
import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

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

    public EventsSQLiteController(Context context, int version) {
        super(context, version);
    }

    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, categoryTablename, periodTablename, dateTablename,
                relationTablename}[index];
    }

    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, categoryColumns, periodColumns, dateColumns,
                relationColumns}[index];
    }

    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL UNIQUE)";
    }

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

    public void delete() {
        db.execSQL("DELETE FROM " + tablename);
    }

    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE, " +
                categoryColumns[2] + " TEXT NOT NULL UNIQUE)";
    }

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

    public void insertCategory(Object... values) {
        insert(1, values);
    }

    public void deleteCategory() {
        db.execSQL("DELETE FROM " + categoryTablename);
    }

    public static String createPeriodTable(){
        return "CREATE TABLE " + periodTablename + '(' +
                periodColumns[0] + " INTEGER PRIMARY KEY, " +
                periodColumns[1] + " TEXT NOT NULL UNIQUE)";
    }

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

    public void insertPeriod(Object... values) {
        insert(2, values);
    }

    public void deletePeriod() {
        db.execSQL("DELETE FROM " + periodTablename);
    }

    public static String createDateTable(){
        return "CREATE TABLE " + dateTablename + '(' + dateColumns[0] + " INTEGER PRIMARY KEY, " +
                dateColumns[1] + " TEXT NOT NULL, " + dateColumns[2] + " TEXT NOT NULL, " +
                "UNIQUE(" + dateColumns[1] + ", " + dateColumns[2] + "))";
    }

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

    public void insertDate(Object... values) {
        insert(3, values);
    }

    public void deleteDate() {
        db.execSQL("DELETE FROM "+dateTablename);
    }

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

    public void insertRelation(Object... values) {
        insert(4, values);
    }

}
