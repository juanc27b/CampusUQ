package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Announcement;
import co.edu.uniquindio.campusuq.vo.AnnouncementLink;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

class AnnouncementsServiceController {

    private static final String _ANNOUNCEMENTS = "/anuncios";

    static ArrayList<Announcement> getAnnouncements(Context context, String category) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+_ANNOUNCEMENTS+category);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Announcement> announcements = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                announcements.add(new Announcement(
                        object.getInt(AnnouncementsSQLiteController.columns[0]),
                        object.getInt(AnnouncementsSQLiteController.columns[1]),
                        object.getString(AnnouncementsSQLiteController.columns[2]),
                        object.getString(AnnouncementsSQLiteController.columns[3]),
                        object.getString(AnnouncementsSQLiteController.columns[4]),
                        object.getString(AnnouncementsSQLiteController.columns[5]),
                        "N"));
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
        }

        return announcements;
    }

    static String modifyAnnouncement(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+_ANNOUNCEMENTS);
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        post.setHeader(HTTP.CONTENT_TYPE, "application/json; Charset=UTF-8");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return EntityUtils
                    .toString(HttpClientBuilder.create().build().execute(post).getEntity());
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

    private static final String _ANNOUNCEMENT_LINKS = "/anuncio_enlaces";

    static ArrayList<AnnouncementLink> getAnnouncementLinks(Context context, String announcement) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+_ANNOUNCEMENT_LINKS+announcement);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<AnnouncementLink> links = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                links.add(new AnnouncementLink(
                        object.getInt(AnnouncementsSQLiteController.linkColumns[0]),
                        object.getInt(AnnouncementsSQLiteController.linkColumns[1]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[2]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[3])));
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
        }

        return links;
    }

    static String modifyAnnouncementLink(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+_ANNOUNCEMENT_LINKS);
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        post.setHeader(HTTP.CONTENT_TYPE, "application/json; Charset=UTF-8");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(post).getEntity())).getString("mensaje");
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}
