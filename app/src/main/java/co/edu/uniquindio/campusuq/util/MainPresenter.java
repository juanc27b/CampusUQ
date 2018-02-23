package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.Information;
import co.edu.uniquindio.campusuq.vo.InformationCategory;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

public class MainPresenter {

    public static String[] getInformation(String categoryName, Context context) {
        String[] info = new String[2];
        String link = null;
        String content = "";

        InformationsSQLiteController dbController = new InformationsSQLiteController(context, 1);

        ArrayList<InformationCategory> categories = dbController.selectCategory(
                InformationsSQLiteController.CAMPOS_CATEGORIA[1] + " = ?", new String[]{categoryName});
        ArrayList<Information> informations;
        if (categories.size() > 0) {
            link = categories.get(0).getLink();
            informations = dbController.select(
                    InformationsSQLiteController.CAMPOS_TABLA[1] + " = ?", new String[]{categories.get(0).get_ID()});
        } else {
            informations = new ArrayList<>();
        }

        for (Information information : informations) {
            content += information.getContent();
        }

        info[0] = link;
        info[1] = content;
        return info;
    }

}
