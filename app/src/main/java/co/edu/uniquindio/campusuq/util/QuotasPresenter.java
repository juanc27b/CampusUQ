package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasPresenter {

    public static ArrayList<Quota> loadQuotas(Context context, String type) {
        QuotasSQLiteController dbController = new QuotasSQLiteController(context, 1);

        ArrayList<Quota> quotas = dbController.select(
                '`'+QuotasSQLiteController.columns[1]+"` = ?", new String[]{type});

        dbController.destroy();

        return quotas;
    }

}
