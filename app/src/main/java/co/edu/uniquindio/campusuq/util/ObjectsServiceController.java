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
import cz.msebera.android.httpclient.util.EntityUtils;

class ObjectsServiceController {

    static ArrayList<LostObject> getObjects(Context context, String idObject) {
        String url = Utilities.URL_SERVICIO+"/objetos";
        if (idObject != null) url += idObject;
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<LostObject> lostObjects = new ArrayList<>();
        try {
            JSONArray array = (new JSONObject(
                    EntityUtils.toString(HttpClientBuilder.create().build().execute(request).getEntity(), "UTF-8")))
                    .getJSONArray("datos");
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
                        object.isNull(ObjectsSQLiteController.columns[7]) ? null : object.getInt(ObjectsSQLiteController.columns[7]),
                        "N"
                ));
            }
        } catch (Exception e) {
            Log.e(ObjectsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return lostObjects;
    }

    static String modifyObject(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+"/objetos");
        post.setHeader("Content-Type", "application/json; Charset=UTF-8");
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            post.setEntity(new StringEntity(json));
            return EntityUtils.toString(HttpClientBuilder.create().build().execute(post).getEntity());
        } catch (Exception e) {
            Log.e(ObjectsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}
