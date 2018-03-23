package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Announcement;
import co.edu.uniquindio.campusuq.vo.AnnouncementLink;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsServiceController {

    public static ArrayList<Announcement> getAnnouncements(Context context, String category) {
        String url = Utilities.URL_SERVICIO+"/anuncios";
        if (category != null)  url += category;
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Announcement> announcements = new ArrayList<>();
        try {
            JSONArray array = (new JSONObject(
                    EntityUtils.toString(HttpClientBuilder.create().build().execute(request).getEntity(), "UTF-8")))
                    .getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                announcements.add(new Announcement(
                        object.getInt(AnnouncementsSQLiteController.columns[0]),
                        object.getInt(AnnouncementsSQLiteController.columns[1]),
                        object.getString(AnnouncementsSQLiteController.columns[2]),
                        object.getString(AnnouncementsSQLiteController.columns[3]),
                        object.getString(AnnouncementsSQLiteController.columns[4]),
                        object.getString(AnnouncementsSQLiteController.columns[5]),
                        "N"
                ));
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return announcements;
    }

    public static ArrayList<AnnouncementLink> getAnnouncementLinks(Context context, String announcement) {
        String url = Utilities.URL_SERVICIO+"/anuncio_enlaces";
        if (announcement != null)  url += "/" + announcement;
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<AnnouncementLink> links = new ArrayList<>();
        try {
            JSONArray array = (new JSONObject(
                    EntityUtils.toString(HttpClientBuilder.create().build().execute(request).getEntity(), "UTF-8")))
                    .getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                links.add(new AnnouncementLink(
                        object.getInt(AnnouncementsSQLiteController.linkColumns[0]),
                        object.getInt(AnnouncementsSQLiteController.linkColumns[1]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[2]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[3])
                ));
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return links;
    }

}
