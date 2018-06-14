package co.edu.uniquindio.campusuq.events;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.edu.uniquindio.campusuq.R;

class CalendarPresenter {

    static ArrayList<CalendarItem> getCalendarItems(String categoryName, Context context) {
        ArrayList<CalendarItem> items = new ArrayList<>();

        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        ArrayList<EventCategory> categories = dbController.selectCategory(null,
                EventsSQLiteController.categoryColumns[2]+" = ?", categoryName);
        String categoryID = categories.get(0).get_ID();

        ArrayList<EventRelation> events = dbController.selectRelation(
                new String[]{EventsSQLiteController.relationColumns[1]},
                EventsSQLiteController.relationColumns[0] + " = ?", categoryID);

        for (EventRelation event : events) {

            ArrayList<EventRelation> periods = dbController.selectRelation(
                    new String[]{EventsSQLiteController.relationColumns[1],
                            EventsSQLiteController.relationColumns[2]},
                    EventsSQLiteController.relationColumns[0] + " = ? AND " +
                            EventsSQLiteController.relationColumns[1] + " = ?",
                    categoryID, event.getEvent_ID());

            String periodIDs = "";
            for (EventRelation period : periods) {
                periodIDs += period.getPeriod_ID() + ",";
            }
            periodIDs = periodIDs.substring(0, periodIDs.length() - 1);

            ArrayList<EventPeriod> calendarPeriods = dbController.selectPeriod(
                    EventsSQLiteController.periodColumns[0] + " IN ("+periodIDs+")");

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
                    EventsSQLiteController.relationColumns,
                    EventsSQLiteController.relationColumns[0]+" = ? AND "+
                            EventsSQLiteController.relationColumns[1]+" = ? AND " +
                            EventsSQLiteController.relationColumns[2]+" = ?",
                    categoryID, event.getEvent_ID(), calendarPeriods.get(i).get_ID());

            String dateIDs = "";
            for (EventRelation date : dates) {
                dateIDs += date.getDate_ID() + ",";
            }
            dateIDs = dateIDs.substring(0, dateIDs.length() - 1);

            ArrayList<EventDate> calendarDates = dbController.selectDate(
                    EventsSQLiteController.dateColumns[0] + " IN ("+dateIDs+")");

            String date = "";
            for (EventDate calendarDate : calendarDates) {
                date += calendarDate.getType()+calendarDate.getDate()+"\n";
            }
            date = date.substring(0, date.length() - 1);

            ArrayList<Event> calendarEvents = dbController.select(
                    EventsSQLiteController.columns[0]+" = ?", event.getEvent_ID());

            CalendarItem item = new CalendarItem(calendarEvents.get(0).getName(), date);
            items.add(item);

        }

        return items;
    }

    static ArrayList<CalendarDetailItem> getCalendarDetailItems(String event, String category,
                                                                Context context) {
        ArrayList<CalendarDetailItem> items = new ArrayList<>();

        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        ArrayList<EventCategory> categories = dbController.selectCategory(null,
                EventsSQLiteController.categoryColumns[2] + " = ?", category);
        String categoryID = categories.get(0).get_ID();

        ArrayList<Event> events = dbController.select(
                EventsSQLiteController.columns[1]+" = ?", event);
        String eventID = events.get(0).get_ID();

        ArrayList<EventRelation> relationPeriods = dbController.selectRelation(
                EventsSQLiteController.relationColumns,
                EventsSQLiteController.relationColumns[0]+" = ? AND "+
                        EventsSQLiteController.relationColumns[1]+" = ?", categoryID, eventID);

        String periodIDs = "";
        for (EventRelation relationPeriod : relationPeriods) {
            periodIDs += relationPeriod.getPeriod_ID() + ",";
        }
        periodIDs = periodIDs.substring(0, periodIDs.length() - 1);

        ArrayList<EventPeriod> periods = dbController.selectPeriod(
                EventsSQLiteController.periodColumns[0] + " IN ("+periodIDs+")");

        for (EventPeriod period : periods) {

            ArrayList<EventRelation> relationDates = dbController.selectRelation(
                    EventsSQLiteController.relationColumns,
                    EventsSQLiteController.relationColumns[0]+" = ? AND "+
                            EventsSQLiteController.relationColumns[1]+" = ? AND "+
                            EventsSQLiteController.relationColumns[2]+" = ?",
                    categoryID, eventID, period.get_ID());

            String dateIDs = "";
            for (EventRelation relationDate : relationDates) {
                dateIDs += relationDate.getDate_ID() + ",";
            }
            dateIDs = dateIDs.substring(0, dateIDs.length() - 1);

            ArrayList<EventDate> dates = dbController.selectDate(
                    EventsSQLiteController.dateColumns[0] + " IN ("+dateIDs+")");

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

        return items;
    }

}
