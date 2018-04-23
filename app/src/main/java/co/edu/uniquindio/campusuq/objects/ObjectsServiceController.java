package co.edu.uniquindio.campusuq.objects;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.State;
import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

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
    public static ArrayList<LostObject> getObjects(Context context, @NonNull String date,
                                                   State state, ArrayList<String> _IDs,
                                                   ArrayList<String> images) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + _OBJECTS + date);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<LostObject> lostObjects = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(EntityUtils.toString(HttpClientBuilder.create()
                    .build().execute(request).getEntity()));
            if (state != null) state.set(object.getInt("estado"));
            JSONArray array = object.getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                lostObjects.add(new LostObject(obj.getString(ObjectsSQLiteController.columns[0]),
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
                    _IDs.remove(index);
                    images.remove(index);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return lostObjects;
    }

    /**
     * Envía una peticion al servidor para insertar, actualizar o eliminar un objeto perdido.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param json Petición en formato JSON para insertar, actualizar o eliminar un objeto perdido.
     * @return Respuesta del servidor.
     */
    public static String modifyObject(Context context, String json) {
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
    }

}
