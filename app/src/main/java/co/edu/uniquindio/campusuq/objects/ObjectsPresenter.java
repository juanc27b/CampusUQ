package co.edu.uniquindio.campusuq.objects;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.User;

/**
 * Presentador para manejar lógica de los objetos perdidos.
 * Obtiene objetos perdidos desde la base de datos local.
 * Borra el historial de objetos perdidos.
 */
public class ObjectsPresenter {

    /**
     * Carga desde la base de datos los objetos perdidos permitiendo difinir opcinalmente un maximo
     * numero de platos a cargar.
     * @param context Contexto utilizado para crear una instancia del controlador de la base de
     *                datos.
     * @param user Usuario utilizado para ordenar los objetos perdidos a cargar.
     * @param limit Maximo numero de objetos perdidos a cargar.
     * @return Arreglo de objetos perdidos obtenido de la base de datos.
     */
    static ArrayList<LostObject> loadObjects(Context context, User user, int limit) {
        ObjectsSQLiteController dbController = new ObjectsSQLiteController(context, 1);
        ArrayList<LostObject> objects;

        if (user == null || "campusuq@uniquindio.edu.co".equals(user.getEmail())) {
            objects = dbController.select("" + limit,
                    ObjectsSQLiteController.columns[8] + " IS NULL AND " +
                            ObjectsSQLiteController.columns[9] + " = 0");
        } else {
            objects = dbController.select("" + limit,
                    ObjectsSQLiteController.columns[1] + " = ?", user.get_ID());
            limit = limit - objects.size();
            if (limit > 0) objects.addAll(dbController.select("" + limit,
                    ObjectsSQLiteController.columns[1] + " != ? AND (" +
                            ObjectsSQLiteController.columns[8] + " IS NULL OR " +
                            ObjectsSQLiteController.columns[8] + " = ?) AND " +
                            ObjectsSQLiteController.columns[9] + " = 0",
                    user.get_ID(), user.get_ID()));
        }

        return objects;
    }

    public static ArrayList<LostObject> loadReadedObjects(Context context) {
        return new ObjectsSQLiteController(context, 1)
                .select(null, ObjectsSQLiteController.columns[9] + " = 1");
    }

    /**
     * Borra el historial de objetos perdidos.
     * @param context Contexto utilizado para crear una instancia del controlador de la base de
     *                datos.
     */
    public static void deleteHistory(Context context, Object... ids) {
        new ObjectsSQLiteController(context, 1).unreaded(ids);
    }

}
