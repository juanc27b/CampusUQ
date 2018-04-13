package co.edu.uniquindio.campusuq.notifications;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Created by Juan Camilo on 22/03/2018.
 */

public class NotificationsPresenter {

    public static ArrayList<Notification> loadNotifications(Context context) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        ArrayList<Notification> notifications = dbController.select(null, null);

        dbController.destroy();
        return notifications;
    }

    public static Notification getNotification(Context context, String _ID) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        Notification notification = dbController.select("1",
                NotificationsSQLiteController.columns[0]+" = ?", _ID).get(0);

        dbController.destroy();
        return notification;
    }

    public static void insertNotifications(Context context) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        for (int i = 0; i < WebService.NOTIFICATIONS.length; i++) {
            dbController.insert(""+i, WebService.NOTIFICATIONS[i], "S");
        }

        dbController.destroy();
    }

    static void updateNotification(Context context, String _ID, String activated) {
        NotificationsSQLiteController dbController =
                new NotificationsSQLiteController(context, 1);

        Notification notification = dbController.select("1",
                NotificationsSQLiteController.columns[0]+" = ?", _ID).get(0);

        dbController.update(notification.get_ID(), notification.getName(), activated,
                notification.get_ID());

        dbController.destroy();
    }

}
