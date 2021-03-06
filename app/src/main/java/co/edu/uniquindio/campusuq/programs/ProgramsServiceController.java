package co.edu.uniquindio.campusuq.programs;

import android.content.Context;
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
 * Controlador del servicio de programas que permite descargar programas, categorías de programa y
 * facultades de programa desde el servidor.
 */
public class ProgramsServiceController {

    /**
     * Hace una petición GET al servidor para obtener los programas almacenados en el mismo, y los
     * extrae de la respuesta en formato JSON para retornar un arreglo de programas.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de programas obtenido desde el servidor.
     */
    public static ArrayList<Program> getPrograms(Context context) {
        ArrayList<Program> programs = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/programas").openConnection();
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

            if (retry >= 10) return programs;

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                programs.add(new Program(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[1])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[2])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[3])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[4])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[5])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[6])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[7])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[8])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[9])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[10])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[11])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[12])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[13])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[14])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[15])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.columns[16]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return programs;
    }

    /**
     * Hace una petición GET al servidor para obtener las categorias de programa almacenados en el
     * mismo, y las extrae de la respuesta en formato JSON para retornar un arreglo de categorias de
     * programa.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de categorias de programa obtenido desde el servidor.
     */
    public static ArrayList<ProgramCategory> getProgramCategories(Context context) {
        ArrayList<ProgramCategory> categories = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/programa_categorias").openConnection();
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
                categories.add(new ProgramCategory(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.categoryColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.categoryColumns[1]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Hace una petición GET al servidor para obtener las facultades de programa almacenados en el
     * mismo, y las extrae de la respuesta en formato JSON para retornar un arreglo de facultades de
     * programa.
     * @param context Contexto necesario para obtener la clave de API para el servicio.
     * @return Arreglo de facultades de programa obtenido desde el servidor.
     */
    public static ArrayList<ProgramFaculty> getProgramFaculties(Context context) {
        ArrayList<ProgramFaculty> faculties = new ArrayList<>();

        try {
            int retry = 0;
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream ;

            do {
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        Utilities.URL_SERVICIO + "/programa_facultades").openConnection();
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

            if (retry >= 10) return faculties;

            Utilities.copy(inputStream, byteArrayOutputStream);
            JSONArray array =
                    new JSONObject(byteArrayOutputStream.toString()).getJSONArray("datos");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                faculties.add(new ProgramFaculty(
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.facultyColumns[0])),
                        StringEscapeUtils.unescapeHtml4(
                                object.getString(ProgramsSQLiteController.facultyColumns[1]))));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return faculties;
    }

}
