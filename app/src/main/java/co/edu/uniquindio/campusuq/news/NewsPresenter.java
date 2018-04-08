package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

class NewsPresenter {

    static ArrayList<New> loadNews(String type, Context context, int limit) {
        ArrayList<New> news = new ArrayList<>();
        NewsSQLiteController dbController = new NewsSQLiteController(context, 1);

        ArrayList<NewCategory> categories = dbController.selectCategory(null,
                NewsSQLiteController.categoryColumns[1]+" = ?", new String[]{"Eventos"});

        if (!categories.isEmpty()) {
            ArrayList<NewRelation> relations = dbController.selectRelation(null,
                    NewsSQLiteController.relationColumns[0]+" = ?",
                    new String[]{categories.get(0).get_ID()});

            String[] New_IDs = new String[relations.size()];
            for (int i = 0; i < New_IDs.length; i++) New_IDs[i] = relations.get(i).getNew_ID();

            String selection = NewsSQLiteController.columns[0];
            if (WebService.ACTION_NEWS.equals(type)) selection += " NOT";
            selection += " IN("+
                    TextUtils.join(", ", Collections.nCopies(New_IDs.length, '?'))+')';

            news = dbController.select(""+limit, selection, New_IDs);
        }

        dbController.destroy();
        return news;
    }

}