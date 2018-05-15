package co.edu.uniquindio.campusuq.users;

import android.content.Context;

import java.util.ArrayList;

public class UsersPresenter {

    public static User loadUser(Context context) {
        User user = null;

        UsersSQLiteController dbController = new UsersSQLiteController(context, 1);
        ArrayList<User> users = dbController.select();
        dbController.destroy();

        if (users.size() > 1) user = users.get(1);
        else if (users.size() > 0) user = users.get(0);

        return user;
    }

}
