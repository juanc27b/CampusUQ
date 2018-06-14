package co.edu.uniquindio.campusuq.news;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.web.WebService;

class NewsPresenter {

    static ArrayList<New> loadNews(String type, Context context, int limit) {
        NewsSQLiteController dbController = new NewsSQLiteController(context, 1);

        ArrayList<NewCategory> categories = dbController.selectCategory("1",
                NewsSQLiteController.categoryColumns[1] + " = ?", "Eventos");
        ArrayList<New> news = new ArrayList<>();

        if (!categories.isEmpty()) {
            ArrayList<NewRelation> relations = dbController.selectRelation(
                    NewsSQLiteController.relationColumns[0] + " = ?",
                    categories.get(0).get_ID());

            String[] New_IDs = new String[relations.size()];
            for (int i = 0; i < New_IDs.length; i++) New_IDs[i] = relations.get(i).getNew_ID();

            news = dbController.select("" + limit, NewsSQLiteController.columns[0] +
                    (WebService.ACTION_NEWS.equals(type) ? " NOT IN(" : " IN(") +
                    TextUtils.join(", ", Collections.nCopies(New_IDs.length, '?')) +
                    ')', New_IDs);
        }

        return news;
    }

}
