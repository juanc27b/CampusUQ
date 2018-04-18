package co.edu.uniquindio.campusuq.quotas;

import android.content.Context;

import java.util.ArrayList;

/**
 * Presentador para manejar lógica de los cupos.
 * Obtiene cupos desde la base de datos local.
 */
class QuotasPresenter {

    /**
     * Obtiene de la base de datos los cupos del tipo especificado.
     * @param context Contexto utilizado para crear una instancia del controlador de la base de
     *                datos.
     * @param type Tipo de cupos que se desea obtener.
     * @return Arreglo de cupos del tipo especificado obtenido de la base de datos.
     */
    static ArrayList<Quota> loadQuotas(Context context, String type) {
        QuotasSQLiteController dbController = new QuotasSQLiteController(context, 1);

        ArrayList<Quota> quotas =
                dbController.select(QuotasSQLiteController.columns[1]+" = ?", type);

        dbController.destroy();
        return quotas;
    }

}
