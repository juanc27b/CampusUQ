package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

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
        String validRows = null;

        String events = "";
        ArrayList<NewCategory> categories = dbController.selectCategory(null,
                NewsSQLiteController.CAMPOS_CATEGORIA[1] + " = ?", new String[]{"Eventos"});
        ArrayList<NewRelation> relations;
        if (categories.size() > 0) {
            relations = dbController.selectRelation(null,
                    NewsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{categories.get(0).get_ID()});
            for (NewRelation relation : relations) {
                events += relation.getNew_ID() + ",";
            }
            events = events.substring(0, events.length() - 1);
            if (WebService.ACTION_NEWS.equals(type)) {
                validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" NOT IN ("+events+")";
            } else {
                validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" IN ("+events+")";
            }
        }

        news = dbController.select(""+limit,
                validRows, null);

        dbController.destroy();

        return news;
    }

}
