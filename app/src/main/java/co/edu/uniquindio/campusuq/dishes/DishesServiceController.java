package co.edu.uniquindio.campusuq.dishes;

import android.content.Context;
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

public class DishesServiceController {

    private static final String _DISHES = "/platos";

    public static ArrayList<Dish> getDishes(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+_DISHES);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Dish> dishes = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                dishes.add(new Dish(object.getString(DishesSQLiteController.columns[0]),
                        object.getString(DishesSQLiteController.columns[1]),
                        object.getString(DishesSQLiteController.columns[2]),
                        object.getString(DishesSQLiteController.columns[3]),
                        object.isNull(DishesSQLiteController.columns[4]) ?
                                null : object.getString(DishesSQLiteController.columns[4])));
            }
        } catch (Exception e) {
            Log.e(DishesServiceController.class.getSimpleName(), e.getMessage());
        }

        return dishes;
    }

    public static String modifyDish(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+_DISHES);
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(post).getEntity())).getString("mensaje");
        } catch (Exception e) {
            Log.e(DishesServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}