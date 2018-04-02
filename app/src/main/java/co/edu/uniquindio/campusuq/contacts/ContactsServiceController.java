package co.edu.uniquindio.campusuq.contacts;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

public class ContactsServiceController {

    public static ArrayList<Contact> getContacts(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+"/contactos");
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
        } catch (Exception e) {
            Log.e(ContactsServiceController.class.getSimpleName(), e.getMessage());
        }

        return contacts;
    }

    public static ArrayList<ContactCategory> getContactCategories(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO+"/contacto_categorias");
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
        } catch (Exception e) {
            Log.e(ContactsServiceController.class.getSimpleName(), e.getMessage());
        }

        return categories;
    }

}
