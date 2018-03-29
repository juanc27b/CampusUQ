package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewsServiceController {

    public static ArrayList<New> getNews(Context context, String idNew) {
        String url = Utilities.URL_SERVICIO+"/noticias";
        if (idNew != null) {
            url += idNew;
        }
        ArrayList<New> news = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String link = StringEscapeUtils.unescapeHtml4(object.getString("Enlace"));
                String image = StringEscapeUtils.unescapeHtml4(object.getString("Imagen"));
                String summary = StringEscapeUtils.unescapeHtml4(object.getString("Resumen"));
                String content = StringEscapeUtils.unescapeHtml4(object.getString("Contenido"));
                String date = StringEscapeUtils.unescapeHtml4(object.getString("Fecha"));
                String author = StringEscapeUtils.unescapeHtml4(object.getString("Autor"));
                New mNew = new New(_ID, name, link, image, summary, content, date, author);
                news.add(mNew);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return news;
    }

    public static ArrayList<NewCategory> getNewCategories(Context context) {
        String url = Utilities.URL_SERVICIO+"/noticia_categorias";
        ArrayList<NewCategory> categories = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String link = StringEscapeUtils.unescapeHtml4(object.getString("Enlace"));
                NewCategory category = new NewCategory(_ID, name, link);
                categories.add(category);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return categories;
    }

    public static ArrayList<NewRelation> getNewRelations(Context context, String idNew) {
        String url = Utilities.URL_SERVICIO+"/noticia_relaciones";
        if (idNew != null) {
            url += "/" + idNew;
        }
        ArrayList<NewRelation> relations = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String category_ID = StringEscapeUtils.unescapeHtml4(object.getString("Categoria_ID"));
                String new_ID = StringEscapeUtils.unescapeHtml4(object.getString("Noticia_ID"));
                NewRelation relation = new NewRelation(category_ID, new_ID);
                relations.add(relation);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return relations;
    }

}
