package co.edu.uniquindio.campusuq.informations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Created by Juan Camilo on 20/02/2018.
 */

public class InformationsServiceController {

    /*public static ArrayList<Information> getInformations(Context context,
                                                         @NonNull String category) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/informaciones" + category);
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Information> informations = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                informations.add(new Information(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[2])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[3]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return informations;
    }*/
    public static ArrayList<Information> getInformations(Context context,
                                                         @NonNull String category) {
        ArrayList<Information> informations = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/informaciones" + category).openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());

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

                return informations;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                informations.add(new Information(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[2])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.columns[3]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return informations;
    }

    /*public static ArrayList<InformationCategory> getInformationCategories(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/informacion_categorias");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<InformationCategory> categories = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories.add(new InformationCategory(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[2])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[3]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }*/
    public static ArrayList<InformationCategory> getInformationCategories(Context context) {
        ArrayList<InformationCategory> categories = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/informacion_categorias").openConnection();
            connection.setRequestProperty("Authorization",
                    UsersPresenter.loadUser(context).getApiKey());

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

                return categories;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories.add(new InformationCategory(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[2])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(InformationsSQLiteController.categoryColumns[3]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

}
