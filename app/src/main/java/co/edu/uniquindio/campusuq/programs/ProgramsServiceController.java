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
 * Created by Juan Camilo on 28/02/2018.
 */

public class ProgramsServiceController {

    /*public static ArrayList<Program> getPrograms(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/programas");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<Program> programs = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

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
    }*/
    public static ArrayList<Program> getPrograms(Context context) {
        ArrayList<Program> programs = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/programas").openConnection();
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

                return programs;
            }

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

    /*public static ArrayList<ProgramCategory> getProgramCategories(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/programa_categorias");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<ProgramCategory> categories = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

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
    }*/
    public static ArrayList<ProgramCategory> getProgramCategories(Context context) {
        ArrayList<ProgramCategory> categories = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/programa_categorias").openConnection();
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

    /*public static ArrayList<ProgramFaculty> getProgramFaculties(Context context) {
        HttpGet request = new HttpGet(Utilities.URL_SERVICIO + "/programa_facultades");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        ArrayList<ProgramFaculty> faculties = new ArrayList<>();

        try {
            JSONArray array = new JSONObject(EntityUtils.toString(HttpClientBuilder.create().build()
                    .execute(request).getEntity())).getJSONArray("datos");

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
    }*/
    public static ArrayList<ProgramFaculty> getProgramFaculties(Context context) {
        ArrayList<ProgramFaculty> faculties = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    Utilities.URL_SERVICIO + "/programa_facultades").openConnection();
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

                return faculties;
            }

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
