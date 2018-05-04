package co.edu.uniquindio.campusuq.quotas;

import android.content.Context;
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
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Controlador del servicio de cupos que permite enviar y recivir cupos desde y hacia el servidor.
 */
public class QuotasServiceController {

    private static final String _QUOTAS = "/cupos";

    /**
     * Obtiene del servidor el arreglo total de cupos.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @return Arreglo de cupos.
     */
    public static ArrayList<Quota> getQuotas(Context context) {
        ArrayList<Quota> quotas = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + _QUOTAS).openConnection();
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

            if (retry >= 10) return quotas;

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                quotas.add(new Quota(object.getString(QuotasSQLiteController.columns[0]),
                        object.getString(QuotasSQLiteController.columns[1]),
                        object.getString(QuotasSQLiteController.columns[2]),
                        object.getString(QuotasSQLiteController.columns[3])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return quotas;
    }

    /**
     * Envía una peticion al servidor para insertar, actualizar o eliminar un cupo.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param json Petición en formato JSON para insertar, actualizar o eliminar un cupo.
     * @return Respuesta del servidor.
     */
    public static String modifyQuota(Context context, String json) {
        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + _QUOTAS).openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(json.getBytes());
                }

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
                        String error = byteArrayOutputStream.toString();
                        Log.e("ErrorStream", error);
                        return new JSONObject(error).getString("mensaje");
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return null;

            Utilities.copy(inputStream, byteArrayOutputStream);
            return new JSONObject(byteArrayOutputStream.toString()).getString("mensaje");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
