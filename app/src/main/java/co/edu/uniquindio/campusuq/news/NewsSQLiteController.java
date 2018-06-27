package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para las tablas Noticia, Noticia_Categoria y Noticia_Relacion,
 * que hacen parte de las funcionalidades de Noticias y Eventos.
 */
public class NewsSQLiteController extends SQLiteController {

    private static final String tablename = "Noticia";
    public static final String columns[] =
            {"_ID", "Nombre", "Enlace", "Imagen", "Resumen", "Contenido", "Fecha", "Autor"};

    private static final String categoryTablename = "Noticia_Categoria";
    public static final String categoryColumns[] = {"_ID", "Nombre", "Enlace"};

    private static final String relationTablename = "Noticia_Relacion";
    public static final String relationColumns[] = {"Categoria_ID", "Noticia_ID"};

    /**
     * Constructor del controlador de la base de datos, el cual utiliza un SQLiteHelper para
     * asegurar que las tablas se creen al crear la base de datos, y obtiene una instancia de la
     * base de datos con permisos de escritura.
     * @param context Contexto necesario para crear un SQLiteHelper.
     * @param version Versión de la base de datos.
     */
    public NewsSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index Parametro que permite elegir entre la tabla Noticia, la tabla Noticia_Categoria
     *              y la tabla Noticia_Relacion.
     * @return Nombre de la tabla elegida.
     */
    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, categoryTablename, relationTablename}[index];
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index Parametro que permite elegir entre la tabla Noticia, la tabla Noticia_Categoria
     *              y la tabla Noticia_Relacion.
     * @return Nombre de las columnas elegidas.
     */
    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, categoryColumns, relationColumns}[index];
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Noticia.
     * @return Cadena con las instrucciones SQL para crear la tabla Noticia.
     */
    public static String createTable(){
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL UNIQUE, " + columns[2] + " TEXT NOT NULL, " +
                columns[3] + " TEXT NOT NULL, " + columns[4] + " TEXT NOT NULL, " +
                columns[5] + " TEXT NOT NULL, " + columns[6] + " TEXT NOT NULL, " +
                columns[7] + " TEXT NOT NULL)";
    }

    /**
     * Selecciona un arreglo de noticias desde la base de datos permitiendo definir opcinalmente un
     * maximo numero de noticias a seleccionar.
     * @param limit Maximo numero de noticias a seleccionar.
     * @param selection Sentencia WHERE para filtrar las noticias que se obtendran de la base de
     *                  datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de noticias de la base de datos.
     */
    public ArrayList<New> select(String limit, String selection, String... selectionArgs) {
        ArrayList<New> news = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs, null,
                null, columns[6] + " DESC", limit);

        if (c.moveToFirst()) do {
            news.add(new New(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6),
                    c.getString(7)));
        } while (c.moveToNext());

        c.close();
        return news;
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Noticia_Categoria.
     * @return Cadena con las instrucciones SQL para crear la tabla Noticia_Categoria.
     */
    public static String createCategoryTable(){
        return "CREATE TABLE " + categoryTablename + '(' +
                categoryColumns[0] + " INTEGER PRIMARY KEY, " +
                categoryColumns[1] + " TEXT NOT NULL UNIQUE, " +
                categoryColumns[2] + " TEXT NOT NULL)";
    }

    /**
     * Selecciona un arreglo de categorias de noticia desde la base de datos permitiendo definir
     * opcinalmente un maximo numero de categorias de noticia a seleccionar.
     * @param limit Maximo numero de categorias de noticia a seleccionar.
     * @param selection Sentencia WHERE para filtrar las categorias de noticia que se obtendran de
     *                  la base de datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de categorias de noticia de la base de datos.
     */
    public ArrayList<NewCategory> selectCategory(String limit, String selection,
                                                 String... selectionArgs) {
        ArrayList<NewCategory> categories = new ArrayList<>();
        Cursor c = db.query(categoryTablename, null, selection, selectionArgs,
                null, null, null, limit);

        if (c.moveToFirst()) do {
            categories.add(new NewCategory(c.getString(0), c.getString(1),
                    c.getString(2)));
        } while (c.moveToNext());

        c.close();
        return categories;
    }

    /**
     * Inserta en la base de datos una categoria de noticia con los valores de las columnas pasados
     * como parámetro a la función
     * @param values Valores de las columnas de la categoria de noticia.
     */
    public void insertCategory(Object... values) {
        insert(1, values);
    }

    /**
     * Actualiza en la base de datos una categoria de noticia con los valores de las columnas
     * pasados como parámetro a la función
     * @param values Valores de las columnas de la categoria de noticia.
     */
    public void updateCategory(Object... values) {
        update(1, values);
    }

    /**
     * Elimina de la base de datos las categorías de noticia cuyas IDs se encuentren dentro del
     * arreglo de IDs pasado como parámetro.
     * @param ids Arreglo de IDs de las categorías de noticia que se quiere eliminar.
     */
    public void deleteCategory(Object... ids) {
        delete(1, ids);
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Noticia_Relacion.
     * @return Cadena con las instrucciones SQL para crear la tabla Noticia_Relacion.
     */
    public static String createRelationTable(){
        return "CREATE TABLE " + relationTablename + '(' +
                relationColumns[0] + " INTEGER NOT NULL REFERENCES " + categoryTablename + '(' +
                categoryColumns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                relationColumns[1] + " INTEGER NOT NULL REFERENCES " + tablename + '(' +
                columns[0] + ") ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (" +
                relationColumns[0] + ", " + relationColumns[1] + "))";
    }

    /**
     * Selecciona un arreglo de relaciones de noticia desde la base de datos.
     * @param selection Sentencia WHERE para filtrar las relaciones de noticia que se obtendran de
     *                  la base de datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de relaciones de noticia de la base de datos.
     */
    public ArrayList<NewRelation> selectRelation(String selection, String... selectionArgs) {
        ArrayList<NewRelation> relations = new ArrayList<>();
        Cursor c = db.query(relationTablename, null, selection, selectionArgs,
                null, null, null);

        if (c.moveToFirst()) do {
            relations.add(new NewRelation(c.getString(0), c.getString(1)));
        } while (c.moveToNext());

        c.close();
        return relations;
    }

    /**
     * Inserta en la base de datos una relacion de noticia con los valores de las columnas pasados
     * como parámetro a la función
     * @param values Valores de las columnas de la relacion de noticia.
     */
    public void insertRelation(Object... values) {
        insert(2, values);
    }

}
