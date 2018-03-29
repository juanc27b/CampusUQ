package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

public class NewsPresenter {

    public ArrayList<New> loadNews(String type, Context context, int limit) {
        ArrayList<New> news = new ArrayList<>();
        NewsSQLiteController dbController = new NewsSQLiteController(context, 1);

        ArrayList<NewCategory> categories = dbController.selectCategory(null,
                NewsSQLiteController.CAMPOS_CATEGORIA[1]+" = ?", new String[]{"Eventos"});

        if (!categories.isEmpty()) {
            ArrayList<NewRelation> relations = dbController.selectRelation(null,
                    NewsSQLiteController.CAMPOS_RELACION[0]+" = ?",
                    new String[]{categories.get(0).get_ID()});

            String[] New_IDs = new String[relations.size()];
            for (int i = 0; i < New_IDs.length; i++) New_IDs[i] = relations.get(i).getNew_ID();

            String selection = NewsSQLiteController.CAMPOS_TABLA[0];
            if (WebService.ACTION_NEWS.equals(type)) selection += " NOT";
            selection += " IN("+
                    TextUtils.join(", ", Collections.nCopies(New_IDs.length, '?'))+')';

            news = dbController.select(""+limit, selection, New_IDs);
        }

        dbController.destroy();
        return news;
    }

}
