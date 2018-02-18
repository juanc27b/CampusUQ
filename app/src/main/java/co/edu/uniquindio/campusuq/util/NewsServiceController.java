package co.edu.uniquindio.campusuq.util;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewsServiceController {

    public final static String URL_SERVICIO = "https://campus-uq.000webhostapp.com";

    public static ArrayList<New> getNews(String idNew) {
        String url = URL_SERVICIO+"/noticias";
        if (idNew != null) {
            url += idNew;
        }
        ArrayList<New> news = new ArrayList<New>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String id = StringEscapeUtils.unescapeHtml4(object.getString("ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String link = StringEscapeUtils.unescapeHtml4(object.getString("Enlace"));
                String image = StringEscapeUtils.unescapeHtml4(object.getString("Imagen"));
                String summary = StringEscapeUtils.unescapeHtml4(object.getString("Resumen"));
                String content = StringEscapeUtils.unescapeHtml4(object.getString("Contenido"));
                String date = StringEscapeUtils.unescapeHtml4(object.getString("Fecha"));
                String author = StringEscapeUtils.unescapeHtml4(object.getString("Autor"));
                New mNew = new New(id, name, link, image, summary, content, date, author);
                news.add(mNew);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
        return news;
    }

    public static ArrayList<NewCategory> getNewCategories() {
        String url = URL_SERVICIO+"/noticia_categorias";
        ArrayList<NewCategory> categories = new ArrayList<NewCategory>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String id = StringEscapeUtils.unescapeHtml4(object.getString("ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String link = StringEscapeUtils.unescapeHtml4(object.getString("Enlace"));
                NewCategory category = new NewCategory(id, name, link);
                categories.add(category);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
        return categories;
    }

    public static ArrayList<NewRelation> getNewRelations(String idNew) {
        String url = URL_SERVICIO+"/noticia_relaciones";
        if (idNew != null) {
            url += "/" + idNew;
        }
        ArrayList<NewRelation> relations = new ArrayList<NewRelation>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String categoria_id = StringEscapeUtils.unescapeHtml4(object.getString("Categoria_ID"));
                String noticia_id = StringEscapeUtils.unescapeHtml4(object.getString("Noticia_ID"));
                NewRelation relation = new NewRelation(categoria_id, noticia_id);
                relations.add(relation);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
        return relations;
    }

    public static New addNew(String json) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(URL_SERVICIO);
        post.setHeader("content-type", "application/json");
        New mNew = null;
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            HttpResponse respose = httpClient.execute(post);
            String resp = EntityUtils.toString(respose.getEntity());
            mNew = jsonToNew(resp);
        } catch (Exception e) {
            Log.e("ServicioRest", "Error! insercion de noticia " + e.getMessage());
            return null;
        }
        return mNew;
    }

    public static New deleteNew(String id) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete delete = new HttpDelete(URL_SERVICIO + "/" + id);
        delete.setHeader("content-type", "application/json");
        try {
            HttpResponse response = client.execute(delete);
            String res = EntityUtils.toString(response.getEntity());
            return jsonToNew(res);
        } catch (Exception e) {
            Log.e("ServicioRest", "Error! eliminando la noticia " + e.getMessage());
            return null;
        }
    }

    /**
     * Se encarga de converir un String formato JSON a una Noticia
     * @param jsonPelicula string en formato JSON
     * @return noticia resultante de la conversión
     */
    public static New jsonToNew(String jsonPelicula) {
        Gson gson = new Gson();
        New mNew = gson.fromJson(jsonPelicula, New.class);
        return mNew;
    }

    /**
     * Se encarga de convertir una noticia en un JSON
     * @param mNew noticia que se desea transformar
     * @return cadena en formato de json de noticia
     */
    public static String newToJson(New mNew) {
        Gson gson = new Gson();
        String json = gson.toJson(mNew);
        return json;
    }

}
