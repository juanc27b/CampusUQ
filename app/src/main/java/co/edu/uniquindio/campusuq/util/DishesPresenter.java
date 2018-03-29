package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Dish;

public class DishesPresenter {

    public static ArrayList<Dish> loadDishes(Context context, int limit) {
        DishesSQLiteController dbController = new DishesSQLiteController(context, 1);

        ArrayList<Dish> dishes = dbController.select(String.valueOf(limit), null,
                null);

        dbController.destroy();

        return dishes;
    }

}
