package co.edu.uniquindio.campusuq.objects;

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
 * Controlador del servicio de objetos perdidos que permite enviar y recivir objetos perdidos desde
 * y hacia el servidor.
 */
public class ObjectsServiceController {

    private static final String _OBJECTS = "/objetos";

    /**
     * Obtiene del servidor el arreglo de objetos perdidos que puede ser total o el mas reciente a
     * la fecha suministrada.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param date Fecha a partir de la cual buscar nuevos objetos perdidos, o una cadena vacia para
     *             buscarlos todos.
     * @param state Estado de exito o fracaso de la operacion retornado atravez del objeto mutable.
     * @param _IDs Arreglo de IDs presentes en la base de datos local que se remoberan de dicho
     *             arreglo si tambien estan en el servidor.
     * @param images  Arreglo de rutas de imagenes presentes en la base de datos local que se
     *                remoberan de dicho arreglo si tambien estan en el servidor.
     * @return Arreglo de objetos perdidos.
     */
    /*public static ArrayList<LostObject> getObjects(Context context, @NonNull String date,
                                                   State state, ArrayList<String> _IDs,
                                                   ArrayList<String> images) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + _OBJECTS + date);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<LostObject> objects = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(EntityUtils.toString(HttpClientBuilder.create()
                    .build().execute(request).getEntity()));
            if (state != null) state.set(object.getInt("estado"));
            JSONArray array = object.getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                objects.add(new LostObject(obj.getString(ObjectsSQLiteController.columns[0]),
                        obj.getString(ObjectsSQLiteController.columns[1]),
                        obj.getString(ObjectsSQLiteController.columns[2]),
                        obj.getString(ObjectsSQLiteController.columns[3]),
                        obj.getString(ObjectsSQLiteController.columns[4]),
                        obj.getString(ObjectsSQLiteController.columns[5]),
                        obj.getString(ObjectsSQLiteController.columns[6]),
                        obj.isNull(ObjectsSQLiteController.columns[7]) ?
                                null : obj.getString(ObjectsSQLiteController.columns[7]),
                        obj.isNull(ObjectsSQLiteController.columns[8]) ?
                                null : obj.getString(ObjectsSQLiteController.columns[8]),
                        "0"));
            }

            if (_IDs != null && images != null) {
                array = object.getJSONArray("_IDs");

                for (int i = 0; i < array.length(); i++) {
                    int index = _IDs.indexOf(array.getString(i));

                    if (index != -1) {
                        _IDs.remove(index);
                        images.remove(index);
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return objects;
    }*/
    public static ArrayList<LostObject> getObjects(Context context, @NonNull String date,
                                                   State state, ArrayList<String> _IDs,
                                                   ArrayList<String> images) {
        ArrayList<LostObject> objects = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _OBJECTS + date).openConnection();
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

                return objects;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONObject object = new JSONObject(byteArrayOutputStream.toString());
            if (state != null) state.set(object.getInt("estado"));
            JSONArray array = object.getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                objects.add(new LostObject(obj.getString(ObjectsSQLiteController.columns[0]),
                        obj.getString(ObjectsSQLiteController.columns[1]),
                        obj.getString(ObjectsSQLiteController.columns[2]),
                        obj.getString(ObjectsSQLiteController.columns[3]),
                        obj.getString(ObjectsSQLiteController.columns[4]),
                        obj.getString(ObjectsSQLiteController.columns[5]),
                        obj.getString(ObjectsSQLiteController.columns[6]),
                        obj.isNull(ObjectsSQLiteController.columns[7]) ?
                                null : obj.getString(ObjectsSQLiteController.columns[7]),
                        obj.isNull(ObjectsSQLiteController.columns[8]) ?
                                null : obj.getString(ObjectsSQLiteController.columns[8]),
                        "0"));
            }

            if (_IDs != null && images != null) {
                array = object.getJSONArray("_IDs");

                for (int i = 0; i < array.length(); i++) {
                    int index = _IDs.indexOf(array.getString(i));

                    if (index != -1) {
                        _IDs.remove(index);
                        images.remove(index);
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return objects;
    }

    /**
     * Envía una peticion al servidor para insertar, actualizar o eliminar un objeto perdido.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param json Petición en formato JSON para insertar, actualizar o eliminar un objeto perdido.
     * @return Respuesta del servidor.
     */
    /*public static String modifyObject(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO + _OBJECTS);
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(post).getEntity())).getString("mensaje");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }*/
    public static String modifyObject(Context context, String json) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + _OBJECTS).openConnection();
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
