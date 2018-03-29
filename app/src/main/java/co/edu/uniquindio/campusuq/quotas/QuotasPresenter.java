package co.edu.uniquindio.campusuq.quotas;

import android.content.Context;

import java.util.ArrayList;

class QuotasPresenter {

    static ArrayList<Quota> loadQuotas(Context context, String type) {
        QuotasSQLiteController dbController = new QuotasSQLiteController(context, 1);

        ArrayList<Quota> quotas = dbController.select(
                '`'+QuotasSQLiteController.columns[1]+"` = ?", new String[]{type});

        dbController.destroy();

        return quotas;
    }

}
