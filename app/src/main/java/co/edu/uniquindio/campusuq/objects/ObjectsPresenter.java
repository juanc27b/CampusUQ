package co.edu.uniquindio.campusuq.objects;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.users.User;

/**
 * Created by Juan Camilo on 22/03/2018.
 */

public class ObjectsPresenter {

    static ArrayList<LostObject> loadObjects(Context context, User user, int limit) {
        ObjectsSQLiteController dbController = new ObjectsSQLiteController(context, 1);
        ArrayList<LostObject> objects;

        if (user == null || user.getEmail().equals("campusuq@uniquindio.edu.co")) {
            objects = dbController.select(""+limit,
                    ObjectsSQLiteController.columns[8]+" IS NULL AND "+
                            ObjectsSQLiteController.columns[9]+" = 0");
        } else {
            objects = dbController.select(""+limit,
                    ObjectsSQLiteController.columns[1]+" = ?", user.get_ID());
            limit = limit-objects.size();
            if (limit > 0) objects.addAll(dbController.select(""+limit,
                    ObjectsSQLiteController.columns[1]+" != ? AND "+
                            ObjectsSQLiteController.columns[8]+" IS NULL AND "+
                            ObjectsSQLiteController.columns[9]+" = 0", user.get_ID()));
        }

        dbController.destroy();
        return objects;
    }

    public static void deleteHistory(Context context) {
        ObjectsSQLiteController dbController = new ObjectsSQLiteController(context, 1);
        dbController.unreadAll();
        dbController.destroy();
    }

}
