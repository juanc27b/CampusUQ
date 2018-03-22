package co.edu.uniquindio.campusuq.util;

import android.content.Context;

/**
 * Created by Juan Camilo on 22/03/2018.
 */

public class ObjectsPresenter {

    public void deleteHistory(Context context) {
        ObjectsSQLiteController dbController = new ObjectsSQLiteController(context, 1);
        dbController.unreadAll();
        dbController.destroy();
    }

}
