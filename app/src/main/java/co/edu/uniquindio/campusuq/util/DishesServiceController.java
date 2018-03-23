package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

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

    static ArrayList<Dish> getDishes(Context context) {
        String url = Utilities.URL_SERVICIO+"/platos";
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Dish> dishes = new ArrayList<>();
        try {
            JSONArray array = (new JSONObject(
                    EntityUtils.toString(HttpClientBuilder.create().build().execute(request).getEntity(), "UTF-8")))
                    .getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                dishes.add(new Dish(
                    object.getInt(DishesSQLiteController.columns[0]),
                    object.getString(DishesSQLiteController.columns[1]),
                    object.getString(DishesSQLiteController.columns[2]),
                    object.getInt(DishesSQLiteController.columns[3]),
                    object.getString(DishesSQLiteController.columns[4])
                ));
            }
        } catch (Exception e) {
            Log.e(DishesServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return dishes;
    }

    static String modifyDish(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+"/platos");
        post.setHeader("Content-Type", "application/json; Charset=UTF-8");
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            post.setEntity(new StringEntity(json));
            return EntityUtils.toString(HttpClientBuilder.create().build().execute(post).getEntity());
        } catch (Exception e) {
            Log.e(DishesServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}
