package co.edu.uniquindio.campusuq.programs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteHelper;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class ProgramsSQLiteController {

    private static final String NOMBRE_TABLA = "Programa";
    public static final String CAMPOS_TABLA[] = new String[]{"_ID", "Categoria_ID", "Facultad_ID", "Nombre",
            "Historia", "Historia_Enlace", "Historia_Fecha", "Mision_Vision", "Mision_Vision_Enlace", "Mision_Vision_Fecha",
            "Plan_Estudios", "Plan_Estudios_Enlace", "Plan_Estudios_Fecha", "Perfiles", "Perfiles_Enlace", "Perfiles_Fecha",
            "Contacto"};

    private static final String NOMBRE_CATEGORIA = "Programa_Categoria";
    private static final String CAMPOS_CATEGORIA[] = new String[]{"_ID", "Nombre"};

    private static final String NOMBRE_FACULTAD = "Programa_Facultad";
    private static final String CAMPOS_FACULTAD[] = new String[]{"_ID", "Nombre"};

    private SQLiteHelper usdbh;
    private SQLiteDatabase db;

    public ProgramsSQLiteController(Context context, int version) {
        usdbh = new SQLiteHelper(context, Utilities.NOMBRE_BD , null, version);
        db = usdbh.getWritableDatabase();
    }

    public static String createTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? INTEGER NOT NULL, ? INTEGER NOT NULL, " +
                "? TEXT NOT NULL UNIQUE, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, " +
                "? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, " +
                "? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, ? TEXT NOT NULL, " +
                "FOREIGN KEY (?) REFERENCES ? (?), FOREIGN KEY (?) REFERENCES ? (?) )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[4]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[5]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[6]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[7]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[8]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[9]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[10]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[11]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[12]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[13]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[14]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[15]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[16]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_FACULTAD);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FACULTAD[0]);
        return builder.toString();
    }

    public ArrayList<Program> select(String selection, String[] selectionArgs) {
        ArrayList<Program> programs = new ArrayList<>();
        Cursor c = db.query(NOMBRE_TABLA, CAMPOS_TABLA, selection, selectionArgs,
                null, null, CAMPOS_TABLA[3]+" ASC");
        if (c.moveToFirst()) {
            do {
                String _ID = c.getString(0);
                String category_ID = c.getString(1);
                String faculty_ID = c.getString(2);
                String name = c.getString(3);
                String history = c.getString(4);
                String historyLink = c.getString(5);
                String historyDate = c.getString(6);
                String missionVision = c.getString(7);
                String missionVisionLink = c.getString(8);
                String missionVisionDate = c.getString(9);
                String curriculum = c.getString(10);
                String curriculumLink = c.getString(1);
                String curriculumDate = c.getString(12);
                String profiles = c.getString(13);
                String profilesLink = c.getString(14);
                String profilesDate = c.getString(15);
                String contact = c.getString(16);
                Program program = new Program(_ID, category_ID, faculty_ID, name, history, historyLink, historyDate,
                        missionVision, missionVisionLink, missionVisionDate, curriculum, curriculumLink, curriculumDate,
                        profiles, profilesLink, profilesDate, contact);
                programs.add(program);
            } while (c.moveToNext());
        }
        c.close();
        return programs;
    }

    public void insert(String... campos) {
        String insertar = "INSERT INTO ? (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[1]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[2]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[3]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[4]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[5]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[6]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[7]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[8]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[9]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[10]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[11]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[12]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[13]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[14]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[15]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_TABLA[16]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1],
                campos[2],
                campos[3],
                campos[4],
                campos[5],
                campos[6],
                campos[7],
                campos[8],
                campos[9],
                campos[10],
                campos[11],
                campos[12],
                campos[13],
                campos[14],
                campos[15],
                campos[16]
        });
    }

    public void update(int column, String... campos) {
        String update = "UPDATE "+NOMBRE_TABLA+" SET ?=?,?=?,?=? WHERE ? = ?";
        StringBuilder builder = new StringBuilder(update);
        int offset = builder.indexOf("?");
        for (int i = column; i < column+3; i++) {
            builder.replace(offset, offset+1, CAMPOS_TABLA[i]);
            offset = builder.indexOf("?", offset)+2;
        }
        offset = offset+6;
        builder.replace(offset, offset+1, CAMPOS_TABLA[0]);
        db.execSQL(builder.toString(), new String[] {
                campos[1],
                campos[2],
                campos[3],
                campos[0]
        });
    }

    public void delete() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_TABLA);
        db.execSQL(builder.toString());
    }

    public static String createCategoryTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? TEXT NOT NULL UNIQUE )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        return builder.toString();
    }

    public ArrayList<ProgramCategory> selectCategory(String selection, String[] selectionArgs) {
        ArrayList<ProgramCategory> categories = new ArrayList<>();
        Cursor c = db.query(NOMBRE_CATEGORIA, CAMPOS_CATEGORIA, selection, selectionArgs,
                null, null, CAMPOS_CATEGORIA[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String name = c.getString(1);
                ProgramCategory category = new ProgramCategory(id, name);
                categories.add(category);
            } while (c.moveToNext());
        }
        c.close();
        return categories;
    }

    public void insertCategory(String... campos) {
        String insertar = "INSERT INTO ? (?,?) VALUES (?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_CATEGORIA[1]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1]
        });
    }

    public void deleteCategory() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_CATEGORIA);
        db.execSQL(builder.toString());
    }

    public static String createFacultyTable(){
        String crearTabla = "CREATE TABLE ? ( ? INTEGER PRIMARY KEY, ? TEXT NOT NULL UNIQUE )";
        StringBuilder builder = new StringBuilder(crearTabla);
        builder.replace(builder.indexOf("?"), crearTabla.indexOf("?") + 1, NOMBRE_FACULTAD);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FACULTAD[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FACULTAD[1]);
        return builder.toString();
    }

    public ArrayList<ProgramFaculty> selectFaculty(String selection, String[] selectionArgs) {
        ArrayList<ProgramFaculty> faculties = new ArrayList<>();
        Cursor c = db.query(NOMBRE_FACULTAD, CAMPOS_FACULTAD, selection, selectionArgs,
                null, null, CAMPOS_FACULTAD[0]+" ASC");
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String name = c.getString(1);
                ProgramFaculty faculty = new ProgramFaculty(id, name);
                faculties.add(faculty);
            } while (c.moveToNext());
        }
        c.close();
        return faculties;
    }

    public void insertFaculty(String... campos) {
        String insertar = "INSERT INTO ? (?,?) VALUES (?,?)";
        StringBuilder builder = new StringBuilder(insertar);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_FACULTAD);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FACULTAD[0]);
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, CAMPOS_FACULTAD[1]);
        db.execSQL(builder.toString(), new String[] {
                campos[0],
                campos[1]
        });
    }

    public void deleteFaculty() {
        StringBuilder builder = new StringBuilder("DELETE FROM ?");
        builder.replace(builder.indexOf("?"), builder.indexOf("?") + 1, NOMBRE_FACULTAD);
        db.execSQL(builder.toString());
    }

    public void destroy() {
        usdbh.close();
    }

}
