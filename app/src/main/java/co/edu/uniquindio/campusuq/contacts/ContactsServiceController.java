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
 * Controlador del servicio de contactos que permite descargar contactos y categorías de contactos
 * desde el servidor.
 */
public class ContactsServiceController {

    /**
     * Hace una petición GET al servidor para obtener los contactos almacenados en el mismo, y
     * los extrae de la respuesta en formato JSON para retornar un arreglo de contactos.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de contactos obtenido desde el servidor.
     */
    public static ArrayList<Contact> getContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/contactos").openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());

                byteArrayOutputStream = new ByteArrayOutputStream();

                try {
                    inputStream = connection.getInputStream();
                    retry = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ResponseCode", "" + connection.getResponseCode());
                    InputStream errorStream = connection.getErrorStream();

                    if (errorStream != null) {
                        Utilities.copy(errorStream, byteArrayOutputStream);
                        Log.e("ErrorStream", byteArrayOutputStream.toString());
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return contacts;

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

    /**
     * Hace una peticiñon GET al servidor para obtener las categorías de contactos almacenadas en
     * el mismo, y las extrae de la respuesta en formato JSON para retornar un arreglo de categorías.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de categorías de contactos obtenido desde el servidor.
     */
    public static ArrayList<ContactCategory> getContactCategories(Context context) {
        ArrayList<ContactCategory> categories = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/contacto_categorias").openConnection();
                connection.setRequestProperty("Authorization",
                        UsersPresenter.loadUser(context).getApiKey());

                byteArrayOutputStream = new ByteArrayOutputStream();

                try {
                    inputStream = connection.getInputStream();
                    retry = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ResponseCode", "" + connection.getResponseCode());
                    InputStream errorStream = connection.getErrorStream();

                    if (errorStream != null) {
                        Utilities.copy(errorStream, byteArrayOutputStream);
                        Log.e("ErrorStream", byteArrayOutputStream.toString());
                    }

                    retry++;
                }
            } while (retry > 0 && retry < 10);

            if (retry >= 10) return categories;

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
