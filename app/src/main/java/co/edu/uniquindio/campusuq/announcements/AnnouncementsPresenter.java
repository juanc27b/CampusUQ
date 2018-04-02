package co.edu.uniquindio.campusuq.announcements;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsPresenter {

    static ArrayList<Announcement> loadAnnouncements(String action, Context context,
                                                            int limit) {
        String type = WebService.ACTION_INCIDENTS.equals(action) ? "I" : "C";

        AnnouncementsSQLiteController dbController =
                new AnnouncementsSQLiteController(context, 1);

        ArrayList<Announcement> announcements = dbController.select(String.valueOf(limit),
                AnnouncementsSQLiteController.columns[2]+" = ? AND "+
                        AnnouncementsSQLiteController.columns[6]+" = 'N'", new String[]{type});

        dbController.destroy();

        return announcements;
    }

    public static Announcement getAnnouncementByID(String _ID, Context context) {
        AnnouncementsSQLiteController dbController =
                new AnnouncementsSQLiteController(context, 1);

        ArrayList<Announcement> announcements = dbController.select("1",
                AnnouncementsSQLiteController.columns[0]+" = ?", new String[]{_ID});

        dbController.destroy();

        return announcements.get(0);
    }

    static ArrayList<AnnouncementLink> getAnnouncementsLinks(Context context,
                                                                    String... _IDs) {
        AnnouncementsSQLiteController dbController =
                new AnnouncementsSQLiteController(context, 1);

        ArrayList<AnnouncementLink> links = dbController.selectLink(
                AnnouncementsSQLiteController.linkColumns[1]+" IN("+
                        TextUtils.join(", ", Collections.nCopies(_IDs.length, "?"))+
                        ')', _IDs);

        dbController.destroy();

        return links;
    }

    static void readed(Context context, Object... ids) {
        AnnouncementsSQLiteController dbController =
                new AnnouncementsSQLiteController(context, 1);
        dbController.readed(ids);
        dbController.destroy();
    }

    public static void deleteHistory(Context context) {
        AnnouncementsSQLiteController dbController =
                new AnnouncementsSQLiteController(context, 1);
        dbController.unreadAll();
        dbController.destroy();
    }

}
