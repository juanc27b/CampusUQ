package co.edu.uniquindio.campusuq.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.New;
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

    public static ArrayList<New> getNews(){
        ArrayList<New> news = new ArrayList<New>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO);
        request.setHeader("content-type", "application/json");
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity());
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<New>>(){}.getType();
            news = gson.fromJson(respStr, listType);
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
        return news;
    }

    public static New addNew(String json) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO);
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
        HttpDelete delete = new HttpDelete(Utilities.URL_SERVICIO + "/" + id);
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
