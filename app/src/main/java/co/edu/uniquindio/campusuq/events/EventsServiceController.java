package co.edu.uniquindio.campusuq.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class EventsServiceController {

    /*public static ArrayList<Event> getEvents(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/eventos");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Event> events = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                events.add(new Event(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.columns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.columns[1]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return events;
    }*/
    public static ArrayList<Event> getEvents(Context context) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/eventos").openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());

            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ResponseCode", "" + connection.getResponseCode());
                InputStream errorStream = connection.getErrorStream();

                if (errorStream != null) {
                    Utilities.copy(errorStream, byteArrayOutputStream);
                    Log.e("ErrorStream", byteArrayOutputStream.toString());
                }

                return events;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                events.add(new Event(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.columns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.columns[1]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    /*public static ArrayList<EventCategory> getEventCategories(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/evento_categorias");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<EventCategory> categories = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories.add(new EventCategory(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.categoryColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.categoryColumns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.categoryColumns[2]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }*/
    public static ArrayList<EventCategory> getEventCategories(Context context) {
        ArrayList<EventCategory> categories = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/evento_categorias").openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());

            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ResponseCode", "" + connection.getResponseCode());
                InputStream errorStream = connection.getErrorStream();

                if (errorStream != null) {
                    Utilities.copy(errorStream, byteArrayOutputStream);
                    Log.e("ErrorStream", byteArrayOutputStream.toString());
                }

                return categories;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories.add(new EventCategory(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.categoryColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.categoryColumns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.categoryColumns[2]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /*public static ArrayList<EventPeriod> getEventPeriods(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/evento_periodos");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<EventPeriod> periods = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                periods.add(new EventPeriod(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.periodColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.periodColumns[1]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return periods;
    }*/
    public static ArrayList<EventPeriod> getEventPeriods(Context context) {
        ArrayList<EventPeriod> periods = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/evento_periodos").openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());

            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ResponseCode", "" + connection.getResponseCode());
                InputStream errorStream = connection.getErrorStream();

                if (errorStream != null) {
                    Utilities.copy(errorStream, byteArrayOutputStream);
                    Log.e("ErrorStream", byteArrayOutputStream.toString());
                }

                return periods;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                periods.add(new EventPeriod(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.periodColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(EventsSQLiteController.periodColumns[1]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return periods;
    }

    /*public static ArrayList<EventDate> getEventDates(Context context, @NonNull String type) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/evento_fechas" + type);
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return dates;
    }*/
    public static ArrayList<EventDate> getEventDates(Context context, @NonNull String type) {
        ArrayList<EventDate> dates = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/evento_fechas" + type).openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());

            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ResponseCode", "" + connection.getResponseCode());
                InputStream errorStream = connection.getErrorStream();

                if (errorStream != null) {
                    Utilities.copy(errorStream, byteArrayOutputStream);
                    Log.e("ErrorStream", byteArrayOutputStream.toString());
                }

                return dates;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                dates.add(new EventDate(object.getString(EventsSQLiteController.dateColumns[0]),
                        object.getString(EventsSQLiteController.dateColumns[1]),
                        object.getString(EventsSQLiteController.dateColumns[2])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return dates;
    }

    /*public static ArrayList<EventRelation> getEventRelations(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/evento_relaciones");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<EventRelation> relations = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                relations.add(new EventRelation(
                        object.getString(EventsSQLiteController.relationColumns[0]),
                        object.getString(EventsSQLiteController.relationColumns[1]),
                        object.getString(EventsSQLiteController.relationColumns[2]),
                        object.getString(EventsSQLiteController.relationColumns[3])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return relations;
    }*/
    public static ArrayList<EventRelation> getEventRelations(Context context) {
        ArrayList<EventRelation> relations = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/evento_relaciones").openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());

            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ResponseCode", "" + connection.getResponseCode());
                InputStream errorStream = connection.getErrorStream();

                if (errorStream != null) {
                    Utilities.copy(errorStream, byteArrayOutputStream);
                    Log.e("ErrorStream", byteArrayOutputStream.toString());
                }

                return relations;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                relations.add(new EventRelation(
                        object.getString(EventsSQLiteController.relationColumns[0]),
                        object.getString(EventsSQLiteController.relationColumns[1]),
                        object.getString(EventsSQLiteController.relationColumns[2]),
                        object.getString(EventsSQLiteController.relationColumns[3])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return relations;
    }

}
