package co.edu.uniquindio.campusuq.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.news.NewsServiceController;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class EventsServiceController {

    public static ArrayList<Event> getEvents(Context context) {
        String url = Utilities.URL_SERVICIO+"/eventos";
        ArrayList<Event> events = new ArrayList<>();
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
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                Event event = new Event(_ID, name);
                events.add(event);
            }
        } catch (Exception e) {
            Log.e(EventsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return events;
    }

    public static ArrayList<EventCategory> getEventCategories(Context context) {
        String url = Utilities.URL_SERVICIO+"/evento_categorias";
        ArrayList<EventCategory> categories = new ArrayList<>();
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
                String abbreviation = StringEscapeUtils.unescapeHtml4(object.getString("Abreviacion"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                EventCategory category = new EventCategory(_ID, abbreviation, name);
                categories.add(category);
            }
        } catch (Exception e) {
            Log.e(EventsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return categories;
    }

    public static ArrayList<EventPeriod> getEventPeriods(Context context) {
        String url = Utilities.URL_SERVICIO+"/evento_periodos";
        ArrayList<EventPeriod> periods = new ArrayList<>();
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
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                EventPeriod period = new EventPeriod(_ID, name);
                periods.add(period);
            }
        } catch (Exception e) {
            Log.e(EventsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return periods;
    }

    public static ArrayList<EventDate> getEventDates(Context context, @NonNull String type) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+"/evento_fechas"+type);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<EventDate> dates = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                dates.add(new EventDate(object.getString(EventsSQLiteController.dateColumns[0]),
                        object.getString(EventsSQLiteController.dateColumns[1]),
                        object.getString(EventsSQLiteController.dateColumns[2])));
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
        }

        return dates;
    }

    public static ArrayList<EventRelation> getEventRelations(Context context) {
        String url = Utilities.URL_SERVICIO+"/evento_relaciones";
        ArrayList<EventRelation> relations = new ArrayList<>();
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
                String category_ID = StringEscapeUtils.unescapeHtml4(object.getString("Categoria_ID"));
                String event_ID = StringEscapeUtils.unescapeHtml4(object.getString("Evento_ID"));
                String period_ID = StringEscapeUtils.unescapeHtml4(object.getString("Periodo_ID"));
                String date_ID = StringEscapeUtils.unescapeHtml4(object.getString("Fecha_ID"));
                EventRelation relation = new EventRelation(category_ID, event_ID, period_ID, date_ID);
                relations.add(relation);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return relations;
    }

}
