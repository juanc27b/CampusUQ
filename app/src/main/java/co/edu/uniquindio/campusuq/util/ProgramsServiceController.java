package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Program;
import co.edu.uniquindio.campusuq.vo.ProgramCategory;
import co.edu.uniquindio.campusuq.vo.ProgramFaculty;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class ProgramsServiceController {

    public static ArrayList<Program> getPrograms(Context context) {
        String url = Utilities.URL_SERVICIO+"/programas";
        ArrayList<Program> programs = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String category_ID = StringEscapeUtils.unescapeHtml4(object.getString("Categoria_ID"));
                String faculty_ID = StringEscapeUtils.unescapeHtml4(object.getString("Facultad_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                String history = StringEscapeUtils.unescapeHtml4(object.getString("Historia"));
                String historyLink = StringEscapeUtils.unescapeHtml4(object.getString("Historia_Enlace"));
                String historyDate = StringEscapeUtils.unescapeHtml4(object.getString("Historia_Fecha"));
                String missionVision = StringEscapeUtils.unescapeHtml4(object.getString("Mision_Vision"));
                String missionVisionLink = StringEscapeUtils.unescapeHtml4(object.getString("Mision_Vision_Enlace"));
                String missionVisionDate = StringEscapeUtils.unescapeHtml4(object.getString("Mision_Vision_Fecha"));
                String curriculum = StringEscapeUtils.unescapeHtml4(object.getString("Plan_Estudios"));
                String curriculumLink = StringEscapeUtils.unescapeHtml4(object.getString("Plan_Estudios_Enlace"));
                String curriculumDate = StringEscapeUtils.unescapeHtml4(object.getString("Plan_Estudios_Fecha"));
                String profiles = StringEscapeUtils.unescapeHtml4(object.getString("Perfiles"));
                String profilesLink = StringEscapeUtils.unescapeHtml4(object.getString("Perfiles_Enlace"));
                String profilesDate = StringEscapeUtils.unescapeHtml4(object.getString("Perfiles_Fecha"));
                String contact = StringEscapeUtils.unescapeHtml4(object.getString("Contacto"));
                Program program = new Program(_ID, category_ID, faculty_ID, name, history, historyLink, historyDate,
                        missionVision, missionVisionLink, missionVisionDate, curriculum, curriculumLink, curriculumDate,
                        profiles, profilesLink, profilesDate, contact);
                programs.add(program);
            }
        } catch (Exception e) {
            Log.e(ProgramsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return programs;
    }

    public static ArrayList<ProgramCategory> getProgramCategories(Context context) {
        String url = Utilities.URL_SERVICIO+"/programa_categorias";
        ArrayList<ProgramCategory> categories = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                ProgramCategory category = new ProgramCategory(_ID, name);
                categories.add(category);
            }
        } catch (Exception e) {
            Log.e(ProgramsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return categories;
    }

    public static ArrayList<ProgramFaculty> getProgramFaculties(Context context) {
        String url = Utilities.URL_SERVICIO+"/programa_facultades";
        ArrayList<ProgramFaculty> faculties = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json; Charset=UTF-8");
        request.setHeader("Authorization", UsersPresenter.loadUser(context).getApiKey());
        try {
            HttpResponse resp = httpClient.execute(request);
            String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(respStr);
            JSONArray array = json.getJSONArray("datos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String _ID = StringEscapeUtils.unescapeHtml4(object.getString("_ID"));
                String name = StringEscapeUtils.unescapeHtml4(object.getString("Nombre"));
                ProgramFaculty faculty = new ProgramFaculty(_ID, name);
                faculties.add(faculty);
            }
        } catch (Exception e) {
            Log.e(ProgramsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return faculties;
    }

}
