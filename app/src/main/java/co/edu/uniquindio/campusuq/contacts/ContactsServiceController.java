package co.edu.uniquindio.campusuq.contacts;

import android.content.Context;
import android.util.Log;

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
 * Created by Juan Camilo on 23/02/2018.
 */

public class ContactsServiceController {

    /*public static ArrayList<Contact> getContacts(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/contactos");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Contact> contacts = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                contacts.add(new Contact(object.getString(ContactsSQLiteController.columns[0]),
                        object.getString(ContactsSQLiteController.columns[1]),
                        object.getString(ContactsSQLiteController.columns[2]),
                        object.getString(ContactsSQLiteController.columns[3]),
                        object.getString(ContactsSQLiteController.columns[4]),
                        object.getString(ContactsSQLiteController.columns[5]),
                        object.getString(ContactsSQLiteController.columns[6]),
                        object.getString(ContactsSQLiteController.columns[7])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return contacts;
    }*/
    public static ArrayList<Contact> getContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/contactos").openConnection();
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

                return contacts;
            }

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                contacts.add(new Contact(object.getString(ContactsSQLiteController.columns[0]),
                        object.getString(ContactsSQLiteController.columns[1]),
                        object.getString(ContactsSQLiteController.columns[2]),
                        object.getString(ContactsSQLiteController.columns[3]),
                        object.getString(ContactsSQLiteController.columns[4]),
                        object.getString(ContactsSQLiteController.columns[5]),
                        object.getString(ContactsSQLiteController.columns[6]),
                        object.getString(ContactsSQLiteController.columns[7])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return contacts;
    }

    /*public static ArrayList<ContactCategory> getContactCategories(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/contacto_categorias");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<ContactCategory> categories = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories.add(new ContactCategory(
                        object.getString(ContactsSQLiteController.categoryColumns[0]),
                        object.getString(ContactsSQLiteController.categoryColumns[1]),
                        object.getString(ContactsSQLiteController.categoryColumns[2])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }*/
    public static ArrayList<ContactCategory> getContactCategories(Context context) {
        ArrayList<ContactCategory> categories = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/contacto_categorias").openConnection();
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
                categories.add(new ContactCategory(
                        object.getString(ContactsSQLiteController.categoryColumns[0]),
                        object.getString(ContactsSQLiteController.categoryColumns[1]),
                        object.getString(ContactsSQLiteController.categoryColumns[2])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

}
