package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewsServiceController {

    public static ArrayList<New> getNews(Context context, @NonNull String category_date,
                                         ArrayList<String> _IDs, ArrayList<String> images) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+"/noticias"+category_date);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<New> news = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(EntityUtils.toString(HttpClientBuilder.create()
                    .build().execute(request).getEntity()));
            JSONArray array = object.getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                news.add(new New(obj.getString(NewsSQLiteController.columns[0]),
                        StringEscapeUtils
                                .unescapeHtml4(obj.getString(NewsSQLiteController.columns[1])),
                        obj.getString(NewsSQLiteController.columns[2]),
                        obj.getString(NewsSQLiteController.columns[3]),
                        StringEscapeUtils
                                .unescapeHtml4(obj.getString(NewsSQLiteController.columns[4])),
                        StringEscapeUtils
                                .unescapeHtml4(obj.getString(NewsSQLiteController.columns[5])),
                        obj.getString(NewsSQLiteController.columns[6]),
                        obj.getString(NewsSQLiteController.columns[7])));
            }

            if (_IDs != null && images != null) {
                array = object.getJSONArray("_IDs");

                for (int i = 0; i < array.length(); i++) {
                    int index = _IDs.indexOf(array.getString(i));
                    _IDs.remove(index);
                    images.remove(index);
                }
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
        }

        return news;
    }

    public static ArrayList<NewCategory> getNewCategories(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+"/noticia_categorias");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<NewCategory> categories = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories.add(new NewCategory(
                        object.getString(NewsSQLiteController.categoryColumns[0]),
                        object.getString(NewsSQLiteController.categoryColumns[1]),
                        object.getString(NewsSQLiteController.categoryColumns[2])));
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
        }

        return categories;
    }

    public static ArrayList<NewRelation> getNewRelations(Context context, String idNew) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+"/noticia_relaciones"+idNew);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<NewRelation> relations = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                relations.add(new NewRelation(
                        object.getString(NewsSQLiteController.relationColumns[0]),
                        object.getString(NewsSQLiteController.relationColumns[1])));
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
        }

        return relations;
    }

}
