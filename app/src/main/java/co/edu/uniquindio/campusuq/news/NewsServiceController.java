package co.edu.uniquindio.campusuq.news;

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
import co.edu.uniquindio.campusuq.util.State;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Controlador del servicio de noticias que permite recivir noticias desde el servidor.
 */
public class NewsServiceController {

    /**
     * Obtiene del servidor el arreglo de noticias que puede ser total, el correspondiente a la
     * categoria suministrada o ademas de la categoria la mas reciente a la fecha suministrada.
     * @param context Contexto utilizado para obtener la clave de autorizacion del usuario que haya
     *                iniciado sesion.
     * @param category_date Categoria y fecha a partir de la cual buscar nuevas noticias, o una
     *                      cadena vacia para buscarlas todos.
     * @param state Estado de exito o fracaso de la operacion retornado atravez del objeto mutable.
     * @param _IDs Arreglo de IDs presentes en la base de datos local que se remoberan de dicho
     *             arreglo si tambien estan en el servidor.
     * @return Arreglo de noticias.
     */
    public static ArrayList<New> getNews(Context context, @NonNull String category_date,
                                         State state, ArrayList<String> _IDs,
                                         ArrayList<String> images) {
        ArrayList<New> news = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/noticias" + category_date).openConnection();
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

            if (retry >= 10) return news;

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONObject object = new JSONObject(byteArrayOutputStream.toString());
            if (state != null) state.set(object.getInt("estado"));
            JSONArray array = object.getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                news.add(new New(obj.getString(NewsSQLiteController.columns[0]),
                        StringEscapeUtils
                                .unescapeHtml4(obj.getString(NewsSQLiteController.columns[1])),
                        obj.getString(NewsSQLiteController.columns[2]),
                        obj.getString(NewsSQLiteController.columns[3]),
                        StringEscapeUtils
                                .unescapeHtml4(obj.getString(NewsSQLiteController.columns[4])),
                        StringEscapeUtils
                                .unescapeHtml4(obj.getString(NewsSQLiteController.columns[5])),
                        obj.getString(NewsSQLiteController.columns[6]),
                        obj.getString(NewsSQLiteController.columns[7])));
            }

            if (_IDs != null && images != null) {
                array = object.getJSONArray("_IDs");

                for (int i = 0; i < array.length(); i++) {
                    int index = _IDs.indexOf(array.getString(i));

                    if (index != -1) {
                        _IDs.remove(index);
                        images.remove(index);
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return news;
    }

    /**
     * Hace una peticiñon GET al servidor para obtener las categorías de noticia almacenadas en el
     * mismo, y las extrae de la respuesta en formato JSON para retornar un arreglo de categorías de
     * noticia.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de categorías de noticia obtenido desde el servidor.
     */
    public static ArrayList<NewCategory> getNewCategories(Context context) {
        ArrayList<NewCategory> categories = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/noticia_categorias").openConnection();
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
                categories.add(new NewCategory(
                        object.getString(NewsSQLiteController.categoryColumns[0]),
                        object.getString(NewsSQLiteController.categoryColumns[1]),
                        object.getString(NewsSQLiteController.categoryColumns[2])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Hace una peticiñon GET al servidor para obtener las relaciones de noticia de una noticia en
     * particular almacenadas en el mismo, y las extrae de la respuesta en formato JSON para
     * retornar un arreglo de relaciones de noticia.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @param idNew ID de la noticia para la cual buscar sus relaciones.
     * @return Arreglo de relaciones de noticia obtenido desde el servidor.
     */
    public static ArrayList<NewRelation> getNewRelations(Context context, String idNew) {
        ArrayList<NewRelation> relations = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/noticia_relaciones" + idNew).openConnection();
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

            if (retry >= 10) return relations;

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                relations.add(new NewRelation(
                        object.getString(NewsSQLiteController.relationColumns[0]),
                        object.getString(NewsSQLiteController.relationColumns[1])));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return relations;
    }

}
