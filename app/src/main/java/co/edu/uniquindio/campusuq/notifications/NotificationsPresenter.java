package co.edu.uniquindio.campusuq.notifications;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Presentador para manejar lógica de las notificaciones.
 * Obtiene notificaciones y detalles de notificaciones desde la base de datos local.
 */
public class NotificationsPresenter {

    public static ArrayList<Notification> loadNotifications(Context context) {
        return new NotificationsSQLiteController(context, 1)
                .select(null, null);
    }

    public static Notification getNotification(Context context, String _ID) {
        return new NotificationsSQLiteController(context, 1).select("1",
                NotificationsSQLiteController.columns[0]+" = ?", _ID).get(0);
    }

    public static void insertNotifications(Context context) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        for (int i = 0; i < WebService.NOTIFICATIONS.length; i++) {
            dbController.insert(""+i, WebService.NOTIFICATIONS[i], "S");
        }
    }

    static void updateNotification(Context context, String _ID, String activated) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        Notification notification = dbController.select("1",
                NotificationsSQLiteController.columns[0]+" = ?", _ID).get(0);

        dbController.update(notification.get_ID(), notification.getName(), activated,
                notification.get_ID());
    }

    static ArrayList<NotificationDetail> loadNotificationDetails(Context context) {
        return new NotificationsSQLiteController(context, 1).selectDetail();
    }

    public static void insertNotificationDetail(Context context, Object... values) {
        new NotificationsSQLiteController(context, 1).insertDetail(values);
    }

    static void deleteNotificationDetail(Context context, Object... values) {
        new NotificationsSQLiteController(context, 1).deleteDetail(values);
    }

}
