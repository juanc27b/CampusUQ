package co.edu.uniquindio.campusuq.util;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Quota;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by JuanCamilo on 24/02/2018.
 */

public class QuotasServiceController {
    public static ArrayList<Quota> getQuotas(String idQuota) {
        String url = Utilities.URL_SERVICIO+"/cupos";
        if(idQuota != null) url += idQuota;
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        ArrayList<Quota> quotas = new ArrayList<>();
        try {
            JSONArray array = (new JSONObject(EntityUtils.toString(
                HttpClientBuilder.create().build().execute(request).getEntity(),
                "UTF-8"
            ))).getJSONArray("datos");
            for(int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                quotas.add(new Quota(
                    StringEscapeUtils.unescapeHtml4(object.getString("_ID")),
                    StringEscapeUtils.unescapeHtml4(object.getString("Tipo")),
                    StringEscapeUtils.unescapeHtml4(object.getString("Nombre")),
                    StringEscapeUtils.unescapeHtml4(object.getString("Cupo"))
                ));
            }
        } catch(Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return quotas;
    }

    public static Quota addQuota(String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO);
        post.setHeader("content-type", "application/json");
        try {
            post.setEntity(new StringEntity(json));
            return (new Gson()).fromJson(
                EntityUtils.toString(HttpClientBuilder.create().build().execute(post).getEntity()),
                Quota.class
            );
        } catch (Exception e) {
            Log.e("ServicioRest", "Error! insercion de noticia " + e.getMessage());
            return null;
        }
    }

    public static Quota deleteQuota(String id) {
        HttpDelete delete = new HttpDelete(Utilities.URL_SERVICIO + "/" + id);
        delete.setHeader("content-type", "application/json");
        try {
            return (new Gson()).fromJson(
                EntityUtils.toString(HttpClientBuilder.create().build().execute(delete).getEntity()),
                Quota.class
            );
        } catch (Exception e) {
            Log.e("ServicioRest", "Error! eliminando la noticia " + e.getMessage());
            return null;
        }
    }
}
