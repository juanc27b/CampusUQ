package co.edu.uniquindio.campusuq.users;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 15/03/2018.
 */

public class UsersServiceController {

    /*public static User login(String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO + "/usuarios/login");
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        User user = null;

        try {
            post.setEntity(new StringEntity(json));
            JSONObject jsonUser = new JSONObject(EntityUtils.toString(HttpClientBuilder.create()
                    .build().execute(post).getEntity())).getJSONObject("usuario");
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return user;
    }*/
    public static User login(String json) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/usuarios/login").openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes());
            }

            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ResponseCode", "" + connection.getResponseCode());
                InputStream errorStream = connection.getErrorStream();

                if (errorStream != null) {
                    Utilities.copy(errorStream, byteArrayOutputStream);
                    Log.e("ErrorStream", byteArrayOutputStream.toString());
                }

                return null;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONObject object =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONObject("usuario");

            return new User(object.getString(UsersSQLiteController.columns[0]),
                    object.getString(UsersSQLiteController.columns[1]),
                    object.getString(UsersSQLiteController.columns[2]),
                    object.isNull(UsersSQLiteController.columns[3]) ?
                            null : object.getString(UsersSQLiteController.columns[3]),
                    object.isNull(UsersSQLiteController.columns[4]) ?
                            null : object.getString(UsersSQLiteController.columns[4]),
                    object.isNull(UsersSQLiteController.columns[5]) ?
                            null : object.getString(UsersSQLiteController.columns[5]),
                    object.getString(UsersSQLiteController.columns[6]),
                    object.getString(UsersSQLiteController.columns[7]),
                    object.getString(UsersSQLiteController.columns[8]));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public static String modifyUser(String json) {
        HttpPost post = new HttpPost(Utilities.URL_SERVICIO + "/usuarios");
        post.setHeader(HTTP.CONTENT_TYPE, "application/json");
        post.setEntity(new StringEntity(json, "UTF-8"));

        try {
            return EntityUtils
                    .toString(HttpClientBuilder.create().build().execute(post).getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/
    public static String modifyUser(String json) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/usuarios").openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes());
            }

            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ResponseCode", "" + connection.getResponseCode());
                InputStream errorStream = connection.getErrorStream();

                if (errorStream != null) {
                    Utilities.copy(errorStream, byteArrayOutputStream);
                    Log.e("ErrorStream", byteArrayOutputStream.toString());
                }

                return null;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
