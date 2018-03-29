package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.LostObject;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

class ObjectsServiceController {

    private static final String _OBJECTS = "/objetos";

    static ArrayList<LostObject> getObjects(Context context, String idObject) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+_OBJECTS+idObject);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<LostObject> lostObjects = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                lostObjects.add(new LostObject(
                        object.getInt(ObjectsSQLiteController.columns[0]),
                        object.getInt(ObjectsSQLiteController.columns[1]),
                        object.getString(ObjectsSQLiteController.columns[2]),
                        object.getString(ObjectsSQLiteController.columns[3]),
                        object.getString(ObjectsSQLiteController.columns[4]),
                        object.getString(ObjectsSQLiteController.columns[5]),
                        object.getString(ObjectsSQLiteController.columns[6]),
                        object.isNull(ObjectsSQLiteController.columns[7]) ? null :
                                object.getInt(ObjectsSQLiteController.columns[7]),
                        "N"));
            }
        } catch (Exception e) {
            Log.e(ObjectsServiceController.class.getSimpleName(), e.getMessage());
        }

        return lostObjects;
    }

    static String modifyObject(Context context, String json) {
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
