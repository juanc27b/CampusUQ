package co.edu.uniquindio.campusuq.dishes;

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
 * Controlador del servicio de platos que permite enviar y recivir platos desde y hacia el servidor.
 */
public class DishesServiceController {

    private static final String _DISHES = "/platos";

    /**
     * Obtiene del servidor el arreglo total de platos.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @return Arreglo de platos.
     */
    public static ArrayList<Dish> getDishes(Context context) {
        ArrayList<Dish> dishes = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _DISHES).openConnection();
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

                return dishes;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                dishes.add(new Dish(object.getString(DishesSQLiteController.columns[0]),
                        object.getString(DishesSQLiteController.columns[1]),
                        object.getString(DishesSQLiteController.columns[2]),
                        object.getString(DishesSQLiteController.columns[3]),
                        object.isNull(DishesSQLiteController.columns[4]) ?
                                null : object.getString(DishesSQLiteController.columns[4])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return dishes;
    }

    /**
     * Envía una peticion al servidor para insertar, actualizar o eliminar un plato.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param json Petición en formato JSON para insertar, actualizar o eliminar un plato.
     * @return Respuesta del servidor.
     */
    public static String modifyDish(Context context, String json) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _DISHES).openConnection();
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
                    String error = byteArrayOutputStream.toString();
                    Log.e("ErrorStream", error);
                    return new JSONObject(error).getString("mensaje");
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
