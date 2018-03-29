package co.edu.uniquindio.campusuq.events;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.edu.uniquindio.campusuq.R;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

class CalendarPresenter {

    static ArrayList<CalendarItem> getCalendarItems(String categoryName, Context context) {
        ArrayList<CalendarItem> items = new ArrayList<>();

        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        ArrayList<EventCategory> categories = dbController.selectCategory(
                EventsSQLiteController.CAMPOS_CATEGORIA[2] + " = ?", new String[]{categoryName});
        String categoryID = categories.get(0).get_ID();

        ArrayList<EventRelation> events = dbController.selectRelation(
                new String[]{EventsSQLiteController.CAMPOS_RELACION[1]},
                EventsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{categoryID});

        for (EventRelation event : events) {

            ArrayList<EventRelation> periods = dbController.selectRelation(
                    new String[]{EventsSQLiteController.CAMPOS_RELACION[1], EventsSQLiteController.CAMPOS_RELACION[2]},
                    EventsSQLiteController.CAMPOS_RELACION[0] + " = ? AND " +
                            EventsSQLiteController.CAMPOS_RELACION[1] + " = ?", new String[]{categoryID, event.getEvent_ID()});

            String periodIDs = "";
            for (EventRelation period : periods) {
                periodIDs += period.getPeriod_ID() + ",";
            }
            periodIDs = periodIDs.substring(0, periodIDs.length() - 1);

            ArrayList<EventPeriod> calendarPeriods = dbController.selectPeriod(
                    EventsSQLiteController.CAMPOS_PERIODO[0] + " IN ("+periodIDs+")", null);

            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            String period = simpleDateFormat.format(currentDate);
            period = period.compareTo(period.substring(0, 5)+"07") < 0 ?
                    period.substring(0, 5)+"1" : period.substring(0, 5)+"2";

            int i = 0;
            for (; i < calendarPeriods.size(); i++) {
                if (calendarPeriods.get(i).getName().equals(period)) {
                    break;
                }
            }
            i = i == calendarPeriods.size() ? i-1 : i;

            ArrayList<EventRelation> dates = dbController.selectRelation(
                    EventsSQLiteController.CAMPOS_RELACION, EventsSQLiteController.CAMPOS_RELACION[0] + " = ? AND " +
                            EventsSQLiteController.CAMPOS_RELACION[1] + " = ? AND " +
                            EventsSQLiteController.CAMPOS_RELACION[2] + " = ?",
                    new String[]{categoryID, event.getEvent_ID(), calendarPeriods.get(i).get_ID()});

            String dateIDs = "";
            for (EventRelation date : dates) {
                dateIDs += date.getDate_ID() + ",";
            }
            dateIDs = dateIDs.substring(0, dateIDs.length() - 1);

            ArrayList<EventDate> calendarDates = dbController.selectDate(
                    EventsSQLiteController.CAMPOS_FECHA[0] + " IN ("+dateIDs+")", null);

            String date = "";
            for (EventDate calendarDate : calendarDates) {
                date += calendarDate.getType()+calendarDate.getDate()+"\n";
            }
            date = date.substring(0, date.length() - 1);

            ArrayList<Event> calendarEvents = dbController.select(
                    EventsSQLiteController.CAMPOS_TABLA[0] + " = ?", new String[]{event.getEvent_ID()});

            CalendarItem item = new CalendarItem(calendarEvents.get(0).getName(), date);
            items.add(item);

        }

        dbController.destroy();

        return items;
    }

    static ArrayList<CalendarDetailItem> getCalendarDetailItems(String event, String category, Context context) {
        ArrayList<CalendarDetailItem> items = new ArrayList<>();

        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        ArrayList<EventCategory> categories = dbController.selectCategory(
                EventsSQLiteController.CAMPOS_CATEGORIA[2] + " = ?", new String[]{category});
        String categoryID = categories.get(0).get_ID();

        ArrayList<Event> events = dbController.select(
                EventsSQLiteController.CAMPOS_TABLA[1] + " = ?", new String[]{event});
        String eventID = events.get(0).get_ID();

        ArrayList<EventRelation> relationPeriods = dbController.selectRelation(
                EventsSQLiteController.CAMPOS_RELACION, EventsSQLiteController.CAMPOS_RELACION[0] + " = ? AND " +
                        EventsSQLiteController.CAMPOS_RELACION[1] + " = ?", new String[]{categoryID, eventID});

        String periodIDs = "";
        for (EventRelation relationPeriod : relationPeriods) {
            periodIDs += relationPeriod.getPeriod_ID() + ",";
        }
        periodIDs = periodIDs.substring(0, periodIDs.length() - 1);

        ArrayList<EventPeriod> periods = dbController.selectPeriod(
                EventsSQLiteController.CAMPOS_PERIODO[0] + " IN ("+periodIDs+")", null);

        for (EventPeriod period : periods) {

            ArrayList<EventRelation> relationDates = dbController.selectRelation(
                    EventsSQLiteController.CAMPOS_RELACION, EventsSQLiteController.CAMPOS_RELACION[0] + " = ? AND " +
                            EventsSQLiteController.CAMPOS_RELACION[1] + " = ? AND " +
                            EventsSQLiteController.CAMPOS_RELACION[2] + " = ?",
                    new String[]{categoryID, eventID, period.get_ID()});

            String dateIDs = "";
            for (EventRelation relationDate : relationDates) {
                dateIDs += relationDate.getDate_ID() + ",";
            }
            dateIDs = dateIDs.substring(0, dateIDs.length() - 1);

            ArrayList<EventDate> dates = dbController.selectDate(
                    EventsSQLiteController.CAMPOS_FECHA[0] + " IN ("+dateIDs+")", null);

            String periodText = context.getString(R.string.period)+": "+period.getName();

            String start = "";
            String end = "";

            for (int i = 0; i < dates.size(); i++) {
                if (end != null) {
                    start = dates.get(i).getType() + dates.get(i).getDate();
                    end = null;
                    if (i == dates.size()-1) {
                        CalendarDetailItem item = new CalendarDetailItem(periodText, start, end);
                        items.add(item);
                    }
                } else {
                    end = dates.get(i).getType() + dates.get(i).getDate();
                    CalendarDetailItem item = new CalendarDetailItem(periodText, start, end);
                    items.add(item);
                }
            }

        }

        dbController.destroy();

        return items;
    }

}
