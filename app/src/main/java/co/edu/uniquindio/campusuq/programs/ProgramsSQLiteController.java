package co.edu.uniquindio.campusuq.programs;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.util.SQLiteController;

public class ProgramsSQLiteController extends SQLiteController {

    private static final String tablename = "Programa";
    public static final String columns[] = {"_ID", "Categoria_ID", "Facultad_ID", "Nombre",
            "Historia", "Historia_Enlace", "Historia_Fecha", "Mision_Vision",
            "Mision_Vision_Enlace", "Mision_Vision_Fecha", "Plan_Estudios", "Plan_Estudios_Enlace",
            "Plan_Estudios_Fecha", "Perfiles", "Perfiles_Enlace", "Perfiles_Fecha", "Contacto"};

    private static final String categoryTablename = "Programa_Categoria";
    static final String categoryColumns[] = {"_ID", "Nombre"};

    private static final String facultyTablename = "Programa_Facultad";
    static final String facultyColumns[] = {"_ID", "Nombre"};

    public ProgramsSQLiteController(Context context, int version) {
        super(context, version);
    }

    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, categoryTablename, facultyTablename}[index];
    }

    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, categoryColumns, facultyColumns}[index];
    }

    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " INTEGER NOT NULL REFERENCES " + categoryTablename + '(' +
                categoryColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                columns[2] + " INTEGER NOT NULL REFERENCES " + facultyTablename + '(' +
                facultyColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                columns[3] + " TEXT NOT NULL UNIQUE, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " TEXT NOT NULL, " +
                columns[7] + " TEXT NOT NULL, " + columns[8] + " TEXT NOT NULL, " +
                columns[9] + " TEXT NOT NULL, " + columns[10] + " TEXT NOT NULL, " +
                columns[11] + " TEXT NOT NULL, " + columns[12] + " TEXT NOT NULL, " +
                columns[13] + " TEXT NOT NULL, " + columns[14] + " TEXT NOT NULL, " +
                columns[15] + " TEXT NOT NULL, " + columns[16] + " TEXT NOT NULL)";
    }

    public ArrayList<Program> select(String selection, String... selectionArgs) {
        ArrayList<Program> programs = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[3] + " ASC");

        if (c.moveToFirst()) do {
            programs.add(new Program(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.getString(7), c.getString(8), c.getString(9), c.getString(10),
                    c.getString(11), c.getString(12), c.getString(13), c.getString(14),
                    c.getString(15), c.getString(16)));
        } while (c.moveToNext());

        c.close();
        return programs;
    }

    public void partialUpdate(int column, Object... values) {
        db.execSQL("UPDATE " + tablename + " SET " + TextUtils.join(" = ?, ", Arrays
                .copyOfRange(columns, column, column + values.length - 1)) + " = ? WHERE " +
                columns[0] + " = ?", values);
    }

    public void delete() {
        db.execSQL("DELETE FROM " + tablename);
    }

    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE)";
    }

    public ArrayList<ProgramCategory> selectCategory() {
        ArrayList<ProgramCategory> categories = new ArrayList<>();
        Cursor c = db.query(categoryTablename, null, null, null,
                null, null, categoryColumns[0] + " ASC");

        if (c.moveToFirst()) do {
            categories.add(new ProgramCategory(c.getString(0), c.getString(1)));
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

    public static String createFacultyTable(){
        return "CREATE TABLE " + facultyTablename + '(' +
                facultyColumns[0] + " INTEGER PRIMARY KEY, " +
                facultyColumns[1] + " TEXT NOT NULL UNIQUE)";
    }

    public ArrayList<ProgramFaculty> selectFaculty() {
        ArrayList<ProgramFaculty> faculties = new ArrayList<>();
        Cursor c = db.query(facultyTablename, null, null, null,
                null, null, facultyColumns[0]+" ASC");

        if (c.moveToFirst()) do {
            faculties.add(new ProgramFaculty(c.getString(0), c.getString(1)));
        } while (c.moveToNext());

        c.close();
        return faculties;
    }

    public void insertFaculty(Object... values) {
        insert(2, values);
    }

    public void deleteFaculty() {
        db.execSQL("DELETE FROM " + facultyTablename);
    }

}
