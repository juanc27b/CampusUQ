package co.edu.uniquindio.campusuq.notifications;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.util.SQLiteController;

/**
 * Controlador de la base de datos para las tablas Notificacion y Notificacion_Detalle.
 */
public class NotificationsSQLiteController extends SQLiteController {

    private static final String tablename = "Notificacion";
    public static final String columns[] = {"_ID", "Nombre", "Activada"};

    private static final String detailTablename = "Notificacion_Detalle";
    private static final String detailColumns[] =
            {"_ID", "Categoria", "Nombre", "Fecha_Hora", "Descripcion"};

    /**
     * Construye el controlador de la base de datos.
     * @param context Contexto usado para la construccion.
     * @param version Versión del controlador.
     */
    NotificationsSQLiteController(Context context, int version) {
        super(context, version);
    }

    /**
     * Funcion por medio de la cual se le pasa el nombre de la tabla a la clase base.
     * @param index Parametro que permite elegir entre la tabla Notificacion y la tabla
     *              NotificacionDetail.
     * @return Nombre de la tabla elegida.
     */
    @Override
    protected String getTablename(int index) {
        return new String[]{tablename, detailTablename}[index];
    }

    /**
     * Funcion por medio de la cual se le pasan los nombres de las columnas a la clase base.
     * @param index Parametro que permite elegir entre la tabla Notificacion y la tabla
     *              NotificacionDetail.
     * @return Nombre de las columnas elegidas.
     */
    @Override
    protected String[] getColumns(int index) {
        return new String[][]{columns, detailColumns}[index];
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Notificacion.
     * @return Cadena con las instrucciones SQL para crear la tabla Notificacion.
     */
    public static String createTable() {
        return "CREATE TABLE " + tablename + '(' + columns[0] + " INTEGER PRIMARY KEY, " +
                columns[1] + " TEXT NOT NULL UNIQUE, " + columns[2] + " TEXT NOT NULL)";
    }

    /**
     * Selecciona un arreglo de notificaciones desde la base de datos permitiendo definir
     * opcinalmente un maximo numero de notificaciones a seleccionar.
     * @param limit Maximo numero de notificaciones a seleccionar.
     * @param selection Sentencia WHERE para filtrar las notificaciones que se obtendran de la base
     *                  de datos.
     * @param selectionArgs Valores a reemplasar en el filtro de selección.
     * @return Arreglo de notificaciones de la base de datos.
     */
    public ArrayList<Notification> select(String limit, String selection, String... selectionArgs) {
        ArrayList<Notification> notifications = new ArrayList<>();
        Cursor c = db.query(tablename, null, selection, selectionArgs,
                null, null, columns[0] + " ASC", limit);

        if (c.moveToFirst()) do {
            notifications.add(new Notification(c.getString(0), c.getString(1),
                    c.getString(2)));
        } while (c.moveToNext());

        c.close();
        return notifications;
    }

    /**
     * Crea la cadena con las instrucciones SQL nesesarias para crear la tabla Notificacion_Detalle.
     * @return Cadena con las instrucciones SQL para crear la tabla Notificacion_Detalle.
     */
    public static String createDetailTable() {
        return "CREATE TABLE " + detailTablename + '(' +
                detailColumns[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                detailColumns[1] + " INTEGER NOT NULL, " + detailColumns[2] + " TEXT NOT NULL, " +
                detailColumns[3] + " TEXT NOT NULL, " + detailColumns[4] + " TEXT)";
    }

    /**
     * Selecciona un arreglo de detalles de notificacion desde la base de datos.
     * @return Arreglo de detalles de notificacion de la base de datos.
     */
    ArrayList<NotificationDetail> selectDetail() {
        ArrayList<NotificationDetail> notificationDetails = new ArrayList<>();
        Cursor c = db.query(detailTablename, null, null, null,
                null, null, detailColumns[0] + " ASC");

        if (c.moveToFirst()) do {
            notificationDetails.add(new NotificationDetail(c.getString(0), c.getInt(1),
                    c.getString(2), c.getString(3), c.getString(4)));
        } while (c.moveToNext());

        c.close();
        return notificationDetails;
    }

    /**
     * Inserta en la base de datos un detalle de notificacion con los valores de las columnas
     * pasados como parámetros a la función
     * @param values Valores de las columnas del detalle de notificacion.
     */
    void insertDetail(Object... values) {
        insert(1, values);
    }

    /**
     * Elimina de la base de datos los detalles de notificacion cuyas IDs se encuentren dentro del
     * arreglo de IDs pasado como parámetro.
     * @param ids Arreglo de IDs de los detalles de notificacion que se quieren eliminar.
     */
    void deleteDetail(Object... ids) {
        delete(1, ids);
    }

}
