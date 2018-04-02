package co.edu.uniquindio.campusuq.objects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ObjectsServiceController {

    private static final String _OBJECTS = "/objetos";

    public static ArrayList<LostObject> getObjects(Context context, @NonNull String date,
                                                   Utilities.State state, ArrayList<Integer> _IDs) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+_OBJECTS+date);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<LostObject> lostObjects = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(EntityUtils.toString(HttpClientBuilder.create()
                    .build().execute(request).getEntity()));
            if (state != null) state.set(object.getInt("estado"));
            JSONArray array = object.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                lostObjects.add(new LostObject(obj.getInt(ObjectsSQLiteController.columns[0]),
                        obj.getInt(ObjectsSQLiteController.columns[1]),
                        obj.getString(ObjectsSQLiteController.columns[2]),
                        obj.getString(ObjectsSQLiteController.columns[3]),
                        obj.getString(ObjectsSQLiteController.columns[4]),
                        obj.getString(ObjectsSQLiteController.columns[5]),
                        obj.getString(ObjectsSQLiteController.columns[6]),
                        obj.getString(ObjectsSQLiteController.columns[7]),
                        obj.isNull(ObjectsSQLiteController.columns[8]) ?
                                null : obj.getInt(ObjectsSQLiteController.columns[8]),
                        "N"));
            }
            if (_IDs != null) {
                array = object.getJSONArray("_IDs");
                // Se castea a Integer para remover el objeto, no el indice
                for (int i = 0; i < array.length(); i++) _IDs.remove((Integer) array.getInt(i));
            }
        } catch (Exception e) {
            Log.e(ObjectsServiceController.class.getSimpleName(), e.getMessage());
        }

        return lostObjects;
    }

    public static String modifyObject(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+_OBJECTS);
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(post).getEntity())).getString("mensaje");
        } catch (Exception e) {
            Log.e(ObjectsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}
