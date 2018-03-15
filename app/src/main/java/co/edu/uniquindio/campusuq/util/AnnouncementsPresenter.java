package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Announcement;

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

}
