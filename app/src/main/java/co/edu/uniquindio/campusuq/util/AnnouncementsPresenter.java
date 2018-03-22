package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Announcement;
import co.edu.uniquindio.campusuq.vo.AnnouncementLink;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsPresenter {

    public ArrayList<Announcement> loadAnnouncements(String type, Context context, int limit) {
        ArrayList<Announcement> announcements = new ArrayList<>();

        AnnouncementsSQLiteController dbController = new AnnouncementsSQLiteController(context, 1);
        String validRows = null;

        if (WebService.ACTION_INCIDENTS.equals(type)) {
            validRows = "I";
        } else {
            validRows = "C";
        }

        announcements = dbController.select(""+limit,
                AnnouncementsSQLiteController.CAMPOS_TABLA[1] + " = ?", new String[]{validRows});

        dbController.destroy();

        return announcements;
    }

    public Announcement getAnnouncementByID(String _ID, Context context) {
        Announcement announcement = null;

        AnnouncementsSQLiteController dbController = new AnnouncementsSQLiteController(context, 1);

        ArrayList<Announcement> announcements = dbController.select("1",
                AnnouncementsSQLiteController.CAMPOS_TABLA[0] + " = ?", new String[]{_ID});

        announcement = announcements.get(0);

        dbController.destroy();

        return announcement;
    }

    public ArrayList<AnnouncementLink> getAnnouncementLinks(String _ID, Context context) {
        ArrayList<AnnouncementLink>links = new ArrayList<>();

        AnnouncementsSQLiteController dbController = new AnnouncementsSQLiteController(context, 1);

        links = dbController.selectLink(
                AnnouncementsSQLiteController.CAMPOS_ENLACE[1] + " = ?", new String[]{_ID});

        dbController.destroy();

        return links;
    }

    public void deleteHistory(Context context) {
        AnnouncementsSQLiteController dbController = new AnnouncementsSQLiteController(context, 1);
        dbController.unreadAll();
        dbController.destroy();
    }

}
