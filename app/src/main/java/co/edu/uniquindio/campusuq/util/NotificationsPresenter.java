package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Notification;

/**
 * Created by Juan Camilo on 22/03/2018.
 */

public class NotificationsPresenter {

    public static ArrayList<Notification> loadNotifications(Context context) {
        ArrayList<Notification> notifications = new ArrayList<>();

        NotificationsSQLiteController dbController = new NotificationsSQLiteController(context, 1);
        notifications = dbController.select(null, null, null);
        dbController.destroy();

        return notifications;
    }

    public static Notification getNotification(Context context, String _ID) {
        Notification notification = null;
        NotificationsSQLiteController dbController = new NotificationsSQLiteController(context, 1);
        notification = dbController.select("1",
                NotificationsSQLiteController.columns[0] + " = ?", new String[]{_ID}).get(0);
        dbController.destroy();
        return notification;
    }

    public static void insertNotifications(Context context) {
        NotificationsSQLiteController dbController = new NotificationsSQLiteController(context, 1);
        for (int i = 0; i < WebService.NOTIFICATIONS.length; i++) {
            dbController.insert(""+i, WebService.NOTIFICATIONS[i], "S");
        }
        dbController.destroy();
    }

    public static void updateNotification(Context context, String _ID, String activated) {
        NotificationsSQLiteController dbController = new NotificationsSQLiteController(context, 1);
        Notification notification = dbController.select("1",
                NotificationsSQLiteController.columns[0] + " = ?", new String[]{_ID}).get(0);
        dbController.update(notification.get_ID(), notification.getName(), activated, notification.get_ID());
        dbController.destroy();
    }

}
