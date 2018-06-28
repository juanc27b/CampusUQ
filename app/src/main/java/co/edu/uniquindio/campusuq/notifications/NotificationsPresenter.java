package co.edu.uniquindio.campusuq.notifications;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Presentador para manejar lógica de las notificaciones.
 * Obtiene notificaciones y detalles de notificaciones desde la base de datos local.
 */
public class NotificationsPresenter {

    /**
     * Carga el arreglo de notificaciones desde la base de datos local.
     * @param context Contexto con el cual realizar la operacion.
     * @return Arreglo de notificaciones.
     */
    public static ArrayList<Notification> loadNotifications(Context context) {
        return new NotificationsSQLiteController(context, 1)
                .select(null, null);
    }

    /**
     * Obtiene una notificacion desde la base de datos local.
     * @param context Contexto con el cual realizar la operacion.
     * @param _ID ID de la notificacion a obtener.
     * @return Notificacion.
     */
    public static Notification getNotification(Context context, String _ID) {
        return new NotificationsSQLiteController(context, 1).select("1",
                NotificationsSQLiteController.columns[0] + " = ?", _ID).get(0);
    }

    /**
     * Inserta las notificaciones en la base de datos local a partir de un arreglo predefinido.
     * @param context Contexto con el cual realizar la operacion.
     */
    public static void insertNotifications(Context context) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        for (int i = 0; i < WebService.NOTIFICATIONS.length; i++) {
            dbController.insert("" + i, WebService.NOTIFICATIONS[i], "S");
        }
    }

    /**
     * Actualiza una notificacion en la base de datos local.
     * @param context Contexto con el cual realizar la operacion.
     * @param _ID ID de la notificacion a actualizar.
     * @param activated Estado de activacion o desactivacion de la notificacion.
     */
    static void updateNotification(Context context, String _ID, String activated) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        Notification notification = dbController.select("1",
                NotificationsSQLiteController.columns[0]+" = ?", _ID).get(0);

        dbController.update(notification.get_ID(), notification.getName(), activated,
                notification.get_ID());
    }

    /**
     * Obtiene el arreglo de detalles de notificacion desde la base de datos local.
     * @param context Contexto con el caul realizar la operacion.
     * @return Arreglo de detalles de notificaicon.
     */
    static ArrayList<NotificationDetail> loadNotificationDetails(Context context) {
        return new NotificationsSQLiteController(context, 1).selectDetail();
    }

    /**
     * Inserta un detalle de notificacion en la base de datos local.
     * @param context Contexto con el cual realizar la operacion.
     * @param values Valores de las columnas del detalle de notificacion a insertar.
     */
    public static void insertNotificationDetail(Context context, Object... values) {
        new NotificationsSQLiteController(context, 1).insertDetail(values);
    }

    /**
     * Elimina de la base de datos local los detalles de notificacion con las IDs suministradas.
     * @param context Contexto con el cual realizar la operacion.
     * @param ids IDs de las notificacions a eliminar.
     */
    static void deleteNotificationDetail(Context context, Object... ids) {
        new NotificationsSQLiteController(context, 1).deleteDetail(ids);
    }

}
