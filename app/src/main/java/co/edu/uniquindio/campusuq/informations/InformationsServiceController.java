package co.edu.uniquindio.campusuq.informations;

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
 * Controlador del servicio de informaciones que permite recivir informaciones y categorias de
 * informaciones desde el servidor.
 */
public class InformationsServiceController {

    /**
     * Obtiene del servidor el arreglo de informaciones de la categoria especificada, o el arreglo
     * total si la categoria es una cadena vacia.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param category Categoria de informacion.
     * @return Arreglo de informaciones.
     */
    public static ArrayList<Information> getInformations(Context context,
                                                         @NonNull String category) {
        ArrayList<Information> informations = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/informaciones" + category).openConnection();
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

            if (retry >= 10) return informations;

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                informations.add(new Information(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[2])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[3]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return informations;
    }

    /**
     * Obtiene el arreglo total de categorias de informaciones.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @return Arreglo de categorias de informaciones.
     */
    public static ArrayList<InformationCategory> getInformationCategories(Context context) {
        ArrayList<InformationCategory> categories = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/informacion_categorias").openConnection();
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
                categories.add(new InformationCategory(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[2])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[3]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

}
