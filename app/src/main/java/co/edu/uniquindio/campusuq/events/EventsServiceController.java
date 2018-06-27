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
 * Controlador del servicio de eventos que permite descargar eventos, categorías de eventos,
 * fechas de eventos, periodos de eventos y relaciones de evento desde el servidor.
 */
public class EventsServiceController {

    /**
     * Hace una petición GET al servidor para obtener los eventos almacenados en el mismo, y los
     * extrae de la respuesta en formato JSON para retornar un arreglo de eventos.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de eventos obtenido desde el servidor.
     */
    public static ArrayList<Event> getEvents(Context context) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/eventos").openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());

                byteArrayOutputStream = new ByteArrayOutputStream();

                try {
                    inputStream = connection.getInputStream();
                    retry = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ResponseCode", "" + connection.getResponseCode());
                    InputStream errorStream = connection.getErrorStream();

                    if (errorStream != null) {
                        Utilities.copy(errorStream, byteArrayOutputStream);
                        Log.e("ErrorStream", byteArrayOutputStream.toString());
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return events;

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

    /**
     * Hace una petición GET al servidor para obtener las categorias de evento almacenadas en el
     * mismo, y las extrae de la respuesta en formato JSON para retornar un arreglo de categorias de
     * evento.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de categorias de evento obtenido desde el servidor.
     */
    public static ArrayList<EventCategory> getEventCategories(Context context) {
        ArrayList<EventCategory> categories = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/evento_categorias").openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());

                byteArrayOutputStream = new ByteArrayOutputStream();

                try {
                    inputStream = connection.getInputStream();
                    retry = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ResponseCode", "" + connection.getResponseCode());
                    InputStream errorStream = connection.getErrorStream();

                    if (errorStream != null) {
                        Utilities.copy(errorStream, byteArrayOutputStream);
                        Log.e("ErrorStream", byteArrayOutputStream.toString());
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return categories;

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

    /**
     * Hace una petición GET al servidor para obtener los periodos de evento almacenadas en el
     * mismo, y los extrae de la respuesta en formato JSON para retornar un arreglo de periodos de
     * evento.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de periodos de evento obtenido desde el servidor.
     */
    public static ArrayList<EventPeriod> getEventPeriods(Context context) {
        ArrayList<EventPeriod> periods = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/evento_periodos").openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());

                byteArrayOutputStream = new ByteArrayOutputStream();

                try {
                    inputStream = connection.getInputStream();
                    retry = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ResponseCode", "" + connection.getResponseCode());
                    InputStream errorStream = connection.getErrorStream();

                    if (errorStream != null) {
                        Utilities.copy(errorStream, byteArrayOutputStream);
                        Log.e("ErrorStream", byteArrayOutputStream.toString());
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return periods;

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

    /**
     * Hace una petición GET al servidor para obtener las fechas de evento almacenadas en el mismo,
     * y las extrae de la respuesta en formato JSON para retornar un arreglo de fechas de evento.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de fechas de evento obtenido desde el servidor.
     */
    public static ArrayList<EventDate> getEventDates(Context context, @NonNull String type) {
        ArrayList<EventDate> dates = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/evento_fechas" + type).openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());

                byteArrayOutputStream = new ByteArrayOutputStream();

                try {
                    inputStream = connection.getInputStream();
                    retry = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ResponseCode", "" + connection.getResponseCode());
                    InputStream errorStream = connection.getErrorStream();

                    if (errorStream != null) {
                        Utilities.copy(errorStream, byteArrayOutputStream);
                        Log.e("ErrorStream", byteArrayOutputStream.toString());
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return dates;

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

    /**
     * Hace una petición GET al servidor para obtener las relaciones de evento almacenadas en el
     * mismo, y las extrae de la respuesta en formato JSON para retornar un arreglo de relaciones de
     * evento.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de relaciones de evento obtenido desde el servidor.
     */
    public static ArrayList<EventRelation> getEventRelations(Context context) {
        ArrayList<EventRelation> relations = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/evento_relaciones").openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());

                byteArrayOutputStream = new ByteArrayOutputStream();

                try {
                    inputStream = connection.getInputStream();
                    retry = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ResponseCode", "" + connection.getResponseCode());
                    InputStream errorStream = connection.getErrorStream();

                    if (errorStream != null) {
                        Utilities.copy(errorStream, byteArrayOutputStream);
                        Log.e("ErrorStream", byteArrayOutputStream.toString());
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return relations;

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
