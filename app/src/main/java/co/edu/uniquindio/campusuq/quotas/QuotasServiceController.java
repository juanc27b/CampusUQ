package co.edu.uniquindio.campusuq.quotas;

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

public class QuotasServiceController {

    private static final String _QUOTAS = "/cupos";

    public static ArrayList<Quota> getQuotas(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+_QUOTAS);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Quota> quotas = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                quotas.add(new Quota(object.getString(QuotasSQLiteController.columns[0]),
                        object.getString(QuotasSQLiteController.columns[1]),
                        object.getString(QuotasSQLiteController.columns[2]),
                        object.getString(QuotasSQLiteController.columns[3])));
            }
        } catch (Exception e) {
            Log.e(QuotasServiceController.class.getSimpleName(), e.getMessage());
        }

        return quotas;
    }

    public static String modifyQuota(Context context, String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+_QUOTAS);
        post.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(post).getEntity())).getString("mensaje");
        } catch (Exception e) {
            Log.e(QuotasServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}
