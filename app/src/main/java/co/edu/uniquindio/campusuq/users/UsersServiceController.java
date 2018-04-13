package co.edu.uniquindio.campusuq.users;

import android.util.Log;

import org.json.JSONObject;

import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 15/03/2018.
 */

public class UsersServiceController {

    public static User login(String json) {
        User user = null;
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+"/usuarios/login");
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            HttpResponse respose = HttpClientBuilder.create().build().execute(post);
            String respStr = EntityUtils.toString(respose.getEntity());
            JSONObject jsonResp = new JSONObject(respStr);
            JSONObject jsonUser = jsonResp.getJSONObject("usuario");
            String _ID = jsonUser.getString("_ID");
            String name = jsonUser.getString("Nombre");
            String email = jsonUser.getString("Correo");
            String phone = jsonUser.getString("Telefono");
            String address = jsonUser.getString("Direccion");
            String document = jsonUser.getString("Documento");
            String password = jsonUser.getString("Contrasena");
            String apiKey = jsonUser.getString("Clave_Api");
            String administrator = jsonUser.getString("Administrador");
            user = new User(_ID, name, email,
                    !phone.equals("null") ? phone : "",
                    !address.equals("null") ? address : "",
                    !document.equals("null") ? document : "",
                    password, apiKey, administrator);
        } catch (Exception e) {
            Log.e(UsersServiceController.class.getSimpleName(), e.getMessage());
        }
        return user;
    }

    public static String modifyUser(String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO+"/usuarios");
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return EntityUtils
                    .toString(HttpClientBuilder.create().build().execute(post).getEntity());
        } catch (Exception e) {
            Log.e(UsersServiceController.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

}
