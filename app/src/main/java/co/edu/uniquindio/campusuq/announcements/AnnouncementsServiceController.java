package co.edu.uniquindio.campusuq.announcements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.State;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Controlador del servicio de anuncios que permite enviar y recivir anuncios desde y hacia el
 * servidor.
 */
public class AnnouncementsServiceController {

    private static final String _ANNOUNCEMENTS      = "/anuncios";
    private static final String _ANNOUNCEMENT_LINKS = "/anuncio_enlaces";

    /**
     * Obtiene del servidor el arreglo de anuncios que puede ser total, el correspondiente a la
     * categoria suministrada o ademas de la categoria el mas reciente a la fecha suministrada.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param category_date Categoria y fecha a partir de la cual buscar nuevos anuncios, o una
     *                      cadena vacia para buscarlos todos.
     * @param state Estado de exito o fracaso de la operacion retornado atravez del objeto mutable.
     * @param _IDs Arreglo de IDs presentes en la base de datos local que se remoberan de dicho
     *             arreglo si tambien estan en el servidor.
     * @return Arreglo de anuncios.
     */
    public static ArrayList<Announcement> getAnnouncements(Context context,
                                                           @NonNull String category_date,
                                                           State state, ArrayList<String> _IDs) {
        ArrayList<Announcement> announcements = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _ANNOUNCEMENTS + category_date).openConnection();
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

                return announcements;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONObject object = new JSONObject(byteArrayOutputStream.toString());
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
                        obj.getString(AnnouncementsSQLiteController.columns[5]), "0"));
            }

            if (_IDs != null) {
                array = object.getJSONArray("_IDs");
                for (int i = 0; i < array.length(); i++) _IDs.remove(array.getString(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return announcements;
    }

    /**
     * Envía una peticion al servidor para insertar, actualizar o eliminar un anuncio.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param json Petición en formato JSON para insertar, actualizar o eliminar un anuncio.
     * @return Respuesta del servidor.
     */
    public static String modifyAnnouncement(Context context, String json) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _ANNOUNCEMENTS).openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes());
            }

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

                return null;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene del servidor el arreglo de enlaces de un anuncio en particular o todos los enlaces de
     * anuncios.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param _announcement ID de anuncio a partir de la cual buscar sus enlaces, o una cadena vacia
     *                      para obtener todos los enlaces de anuncios.
     * @return Arreglo de enlaces de anuncios.
     */
    public static ArrayList<AnnouncementLink> getAnnouncementLinks(Context context,
                                                                   @NonNull String _announcement) {
        ArrayList<AnnouncementLink> links = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _ANNOUNCEMENT_LINKS + _announcement)
                    .openConnection();
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

                return links;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                links.add(new AnnouncementLink(
                        object.getString(AnnouncementsSQLiteController.linkColumns[0]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[1]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[2]),
                        object.getString(AnnouncementsSQLiteController.linkColumns[3])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return links;
    }

    /**
     * Envía una peticion al servidor para insertar, actualizar o eliminar un enlace deanuncio.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param json Petición en formato JSON para insertar, actualizar o eliminar un enlace de
     *             anuncio.
     * @return Respuesta del servidor.
     */
    public static String modifyAnnouncementLink(Context context, String json) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _ANNOUNCEMENT_LINKS).openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes());
            }

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

                return null;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            return new JSONObject(byteArrayOutputStream.toString()).getString("mensaje");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
