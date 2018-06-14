package co.edu.uniquindio.campusuq.announcements;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Presentador para manejar lógica de los anuncios.
 * Obtiene anuncios y enlaces de anuncios desde la base de datos local.
 * Borra el historial de anuncios.
 */
public class AnnouncementsPresenter {

    /**
     * Carga desde la base de datos local los anuncios permitiendo definir opcinalmente un maximo
     * numero de platos a cargar.
     * @param action Accion que determina si los anuncios a obtener corresponden a la funcionalidad
     *               de incidentes o de comunicados.
     * @param context Contexto utilizado para crear una instancia del controlador de la base de
     *                datos.
     * @param limit Maximo numero de objetos perdidos a cargar.
     * @return Arreglo de anuncios obtenido de la base de datos.
     */
    static ArrayList<Announcement> loadAnnouncements(String action, Context context, int limit) {
        return new AnnouncementsSQLiteController(context, 1).select("" + limit,
                AnnouncementsSQLiteController.columns[2] + " = ? AND " +
                        AnnouncementsSQLiteController.columns[6] + " = 0",
                WebService.ACTION_INCIDENTS.equals(action) ? "I" : "C");
    }

    public static Announcement getAnnouncementByID(String _ID, Context context) {
        return new AnnouncementsSQLiteController(context, 1).select("1",
                AnnouncementsSQLiteController.columns[0] + " = ?", _ID).get(0);
    }

    /**
     * Carga desde la base de datos local los enlaces de anuncios  correspondientes al arreglo de
     * IDs de anuncios suministrado.
     * @param context Contexto utilizado para crear una instancia del controlador de la base de
     *                datos.
     * @param _IDs Identificadores de los anuncios pala los cuales se desea obtener sus enlaces.
     * @return Arreglo de enlaces de anuncios obtenido de la base de datos.
     */
    static ArrayList<AnnouncementLink> getAnnouncementsLinks(Context context, String... _IDs) {
        return new AnnouncementsSQLiteController(context, 1).selectLink(
                AnnouncementsSQLiteController.linkColumns[1] + " IN(" +
                        TextUtils.join(", ", Collections.nCopies(_IDs.length, "?")) +
                        ')', _IDs);
    }

    static void readed(Context context, Object... ids) {
        new AnnouncementsSQLiteController(context, 1).readed(ids);
    }

    public static ArrayList<Announcement> loadReadedAnnouncements(Context context, String type) {
        return new AnnouncementsSQLiteController(context, 1).select(null,
                AnnouncementsSQLiteController.columns[2] + " = ? AND " +
                        AnnouncementsSQLiteController.columns[6] + " = 1", type);
    }

    /**
     * Borra el historial de anuncios.
     * @param context Contexto utilizado para crear una instancia del controlador de la base de
     *                datos.
     */
    public static void deleteHistory(Context context, Object... ids) {
        new AnnouncementsSQLiteController(context, 1).unreaded(ids);
    }

}
