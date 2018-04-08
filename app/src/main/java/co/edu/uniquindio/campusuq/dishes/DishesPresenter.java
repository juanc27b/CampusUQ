package co.edu.uniquindio.campusuq.dishes;

import android.content.Context;

import java.util.ArrayList;

class DishesPresenter {

    static ArrayList<Dish> loadDishes(Context context, int limit) {
        DishesSQLiteController dbController = new DishesSQLiteController(context, 1);

        ArrayList<Dish> dishes = dbController.select(""+limit);

        dbController.destroy();
        return dishes;
    }

}
