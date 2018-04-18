package co.edu.uniquindio.campusuq.dishes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Presentador para manejar lógica de los platos.
 * Obtiene platos desde la base de datos local.
 */
class DishesPresenter {

    /**
     * Carga desde la base de datos los platos permitiendo difinir opcinalmente un maximo numero de
     * platos a cargar.
     * @param context Contexto utilizado para crear una instancia del controlador de la base de
     *                datos.
     * @param limit Maximo numero de platos a cargar.
     * @return Arreglo de platos obtenido de la base de datos.
     */
    static ArrayList<Dish> loadDishes(Context context, int limit) {
        DishesSQLiteController dbController = new DishesSQLiteController(context, 1);

        ArrayList<Dish> dishes = dbController.select(""+limit);

        dbController.destroy();
        return dishes;
    }

}
