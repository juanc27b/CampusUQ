package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.vo.CalendarItem;
import co.edu.uniquindio.campusuq.vo.EventRelation;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class CalendarItemsPresenter {

    public ArrayList<CalendarItem> getCalendarItems(String categoryName, Context context) {
        ArrayList<CalendarItem> items = new ArrayList<>();

        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        ArrayList<EventRelation> relations = dbController.selectRelation(
                new String[]{EventsSQLiteController.CAMPOS_RELACION[1]},
                EventsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{categoryName});

        /*
        for (EventCategory category : categories) {
            ArrayList<EventRelation> relations = dbController.selectRelation(
                    EventsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{category.get_ID()});
            String description = context.getString(R.string.category)+": "+category.getAbbreviation()+", "+
                    context.getString(R.string.events)+": "+relations.size();
            Item item = new Item(category.getName(), description);
            items.add(item);
        }
        */

        dbController.destroy();

        return items;
    }

}
