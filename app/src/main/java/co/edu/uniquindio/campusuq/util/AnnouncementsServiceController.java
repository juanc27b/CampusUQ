package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Announcement;
import co.edu.uniquindio.campusuq.vo.AnnouncementLink;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementsServiceController {

    public static ArrayList<Announcement> getAnnouncements(Context context, String category) {
        String url = Utilities.URL_SERVICIO+"/anuncios";
        if (category != null) {
            url += category;
        }
        ArrayList<Announcement> announcements = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String type = StringEscapeUtils.unescapeHtml4(object.getString("Tipo"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String date = StringEscapeUtils.unescapeHtml4(object.getString("Fecha"));
                String description = StringEscapeUtils.unescapeHtml4(object.getString("Descripcion"));
                String read = "N";
                Announcement announcement = new Announcement(_ID, type, name, date, description, read);
                announcements.add(announcement);
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return announcements;
    }

    public static ArrayList<AnnouncementLink> getAnnouncementLinks(Context context, String announcement) {
        String url = Utilities.URL_SERVICIO+"/anuncio_enlaces";
        if (announcement != null) {
            url += "/" + announcement;
        }
        ArrayList<AnnouncementLink> links = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String announcement_ID = StringEscapeUtils.unescapeHtml4(object.getString("Anuncio_ID"));
                String type = StringEscapeUtils.unescapeHtml4(object.getString("Tipo"));
                String link = StringEscapeUtils.unescapeHtml4(object.getString("Enlace"));
                AnnouncementLink announcementLink = new AnnouncementLink(_ID, announcement_ID, type, link);
                links.add(announcementLink);
            }
        } catch (Exception e) {
            Log.e(AnnouncementsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return links;
    }

}
