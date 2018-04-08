package co.edu.uniquindio.campusuq.announcements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsServiceController {

    private static final String _ANNOUNCEMENTS = "/anuncios";

    public static ArrayList<Announcement> getAnnouncements(Context context,
                                                           @NonNull String category_date,
                                                           Utilities.State state,
                                                           ArrayList<String> _IDs) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+_ANNOUNCEMENTS+category_date);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Announcement> announcements = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(EntityUtils.toString(HttpClientBuilder.create()
                    .build().execute(request).getEntity()));
            if (state != null) state.set(object.getInt("estado"));
            JSONArray array = object.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                announcements.add(new Announcement(
                        obj.getString(AnnouncementsSQLiteController.columns[0]),
                        obj.getString(AnnouncementsSQLiteController.columns[1]),
                        obj.getString(AnnouncementsSQLiteController.columns[2]),
                        obj.getString(AnnouncementsSQLiteController.columns[3]),
                        obj.getString(AnnouncementsSQLiteController.columns[4]),
                        obj.getString(AnnouncementsSQLiteController.columns[5]), "N"));
            }
            if (_IDs != null) {
                array = object.getJSONArray("_IDs");
                for (int i = 0; i < array.length(); i++) _IDs.remove(array.getString(i));
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
        }

        return announcements;
    }

    public static String modifyAnnouncement(Context context, String json) {
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

    public static ArrayList<AnnouncementLink> getAnnouncementLinks(Context context,
                                                                   @NonNull String _announcement) {
        HttpGet request =
                new HttpGet(Utilities.URL_SERVICIO+_ANNOUNCEMENT_LINKS+_announcement);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<AnnouncementLink> links = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                links.add(new AnnouncementLink(
                        object.getString(AnnouncementsSQLiteController.linkColumns[0]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[1]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[2]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[3])));
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
        }

        return links;
    }

    public static String modifyAnnouncementLink(Context context, String json) {
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
