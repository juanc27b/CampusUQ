package co.edu.uniquindio.campusuq.util;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Dish;
import co.edu.uniquindio.campusuq.vo.Quota;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class DishesServiceController {
    public static ArrayList<Dish> getDishes(String idDish) {
        String url = Utilities.URL_SERVICIO+"/platos";
        if(idDish != null) url += idDish;
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        ArrayList<Dish> dishes = new ArrayList<>();
        try {
            JSONArray array = (new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build().execute(request).getEntity(), "UTF-8"))).getJSONArray("datos");
            for(int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                dishes.add(new Dish(
                    StringEscapeUtils.unescapeHtml4(object.getString("_ID")),
                    StringEscapeUtils.unescapeHtml4(object.getString("Nombre")),
                    StringEscapeUtils.unescapeHtml4(object.getString("Descripcion")),
                    StringEscapeUtils.unescapeHtml4(object.getString("Precio")),
                    StringEscapeUtils.unescapeHtml4(object.getString("Imagen"))
                ));
            }
        } catch(Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return dishes;
    }
}
