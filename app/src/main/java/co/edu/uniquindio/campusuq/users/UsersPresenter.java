package co.edu.uniquindio.campusuq.users;

import android.content.Context;

import java.util.ArrayList;

/**
 * Presentador para manejar lógica de los usuarios.
 * Obtiene usuarios desde la base de datos local.
 */
public class UsersPresenter {

    /**
     * Obtiene el usuario actual desde la base de datos.
     * @param context Contexto con el cual realizar la operacion.
     * @return Usuario.
     */
    public static User loadUser(Context context) {
        User user = null;

        ArrayList<User> users = new UsersSQLiteController(context, 1).select();

        if (users.size() > 1) user = users.get(1);
        else if (users.size() > 0) user = users.get(0);

        return user;
    }

}
