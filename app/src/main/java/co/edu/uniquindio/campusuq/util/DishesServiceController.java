package co.edu.uniquindio.campusuq.util;

import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Dish;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

class DishesServiceController {

    static ArrayList<Dish> getDishes() {
        String url = Utilities.URL_SERVICIO+"/platos";
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        ArrayList<Dish> dishes = new ArrayList<>();
        try {
            JSONArray array = (new JSONObject(
                    EntityUtils.toString(HttpClientBuilder.create().build().execute(request).getEntity(), "UTF-8")))
                    .getJSONArray("datos");
            for(int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                dishes.add(new Dish(
                    StringEscapeUtils.unescapeHtml4(object.getString(DishesSQLiteController.columns[0])),
                    StringEscapeUtils.unescapeHtml4(object.getString(DishesSQLiteController.columns[1])),
                    StringEscapeUtils.unescapeHtml4(object.getString(DishesSQLiteController.columns[2])),
                    StringEscapeUtils.unescapeHtml4(object.getString(DishesSQLiteController.columns[3])),
                    StringEscapeUtils.unescapeHtml4(object.getString(DishesSQLiteController.columns[4]))
                ));
            }
        } catch(Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return dishes;
    }

    static String modifyDish(String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+"/platos");
        post.setHeader("Content-Type", "application/json; Charset=UTF-8");
        post.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        try {
            post.setEntity(new StringEntity(json));
            return EntityUtils.toString(HttpClientBuilder.create().build().execute(post).getEntity());
        } catch (Exception e) {
            Log.e("ServicioRest", "Error! insercion de cupo "+e.getMessage());
            return null;
        }
    }

}
