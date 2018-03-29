package co.edu.uniquindio.campusuq.informations;

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
 * Created by Juan Camilo on 20/02/2018.
 */

public class InformationsServiceController {

    public static ArrayList<Information> getInformations(Context context, String category) {
        String url = Utilities.URL_SERVICIO+"/informaciones";
        if (category != null) {
            url += "/" + category;
        }
        ArrayList<Information> informations = new ArrayList<>();
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
                String category_ID = StringEscapeUtils.unescapeHtml4(object.getString("Categoria_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String content = StringEscapeUtils.unescapeHtml4(object.getString("Contenido"));
                Information information = new Information(_ID, category_ID, name, content);
                informations.add(information);
            }
        } catch (Exception e) {
            Log.e(InformationsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return informations;
    }

    public static ArrayList<InformationCategory> getInformationCategories(Context context) {
        String url = Utilities.URL_SERVICIO+"/informacion_categorias";
        ArrayList<InformationCategory> categories = new ArrayList<>();
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
                String date = StringEscapeUtils.unescapeHtml4(object.getString("Fecha"));
                InformationCategory category = new InformationCategory(_ID, name, link, date);
                categories.add(category);
            }
        } catch (Exception e) {
            Log.e(InformationsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return categories;
    }

}
