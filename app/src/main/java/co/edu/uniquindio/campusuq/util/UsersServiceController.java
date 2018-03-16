package co.edu.uniquindio.campusuq.util;

import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.vo.User;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 15/03/2018.
 */

public class UsersServiceController {

    public static User login(String json) {
        String url = Utilities.URL_SERVICIO+"/usuarios/login";
        User user = null;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; Charset=UTF-8");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            HttpResponse respose = httpClient.execute(post);
            String respStr = EntityUtils.toString(respose.getEntity());
            JSONObject jsonResp = new JSONObject(respStr);
            JSONObject jsonUser = jsonResp.getJSONObject("usuario");
            String _ID = StringEscapeUtils.unescapeHtml4(jsonUser.getString("_ID"));
            String name = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Nombre"));
            String email = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Correo"));
            String phone = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Telefono"));
            String address = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Direccion"));
            String document = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Documento"));
            String password = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Contrasena"));
            String apiKey = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Clave_Api"));
            String administrator = StringEscapeUtils.unescapeHtml4(jsonUser.getString("Administrador"));
            user = new User(_ID, name, email, phone, address, document, password, apiKey, administrator);
        } catch (Exception e) {
            Log.e(UsersServiceController.class.getSimpleName(), e.getMessage());
        }
        return user;
    }

    public static String modifyUser(String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+"/usuarios");
        post.setHeader("Content-Type", "application/json; Charset=UTF-8");
        try {
            post.setEntity(new StringEntity(json));
            return EntityUtils.toString(HttpClientBuilder.create().build().execute(post).getEntity());
        } catch (Exception e) {
            Log.e(UsersServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}
