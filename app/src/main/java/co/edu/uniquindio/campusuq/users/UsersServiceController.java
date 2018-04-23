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
