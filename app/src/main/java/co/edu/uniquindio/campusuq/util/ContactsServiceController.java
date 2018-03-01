package co.edu.uniquindio.campusuq.util;

import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Contact;
import co.edu.uniquindio.campusuq.vo.ContactCategory;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

public class ContactsServiceController {

    public static ArrayList<Contact> getContacts() {
        String url = Utilities.URL_SERVICIO+"/contactos";
        ArrayList<Contact> contacts = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String category_ID = StringEscapeUtils.unescapeHtml4(object.getString("Categoria_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String address = StringEscapeUtils.unescapeHtml4(object.getString("Direccion"));
                String phone = StringEscapeUtils.unescapeHtml4(object.getString("Telefono"));
                String email = StringEscapeUtils.unescapeHtml4(object.getString("Email"));
                String charge = StringEscapeUtils.unescapeHtml4(object.getString("Cargo"));
                String additionalInformation = StringEscapeUtils.unescapeHtml4(object.getString("Informacion_Adicional"));
                Contact contact = new Contact(_ID, category_ID, name, address, phone, email, charge, additionalInformation);
                contacts.add(contact);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return contacts;
    }

    public static ArrayList<ContactCategory> getContactCategories() {
        String url = Utilities.URL_SERVICIO+"/contacto_categorias";
        ArrayList<ContactCategory> categories = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", "6f8fd504c413e0d3845700c26dc6714f");
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String link = StringEscapeUtils.unescapeHtml4(object.getString("Enlace"));
                ContactCategory category = new ContactCategory(_ID, name, link);
                categories.add(category);
            }
        } catch (Exception e) {
            Log.e(NewsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return categories;
    }

}
