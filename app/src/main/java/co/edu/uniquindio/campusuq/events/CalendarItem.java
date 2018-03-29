package co.edu.uniquindio.campusuq.events;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class CalendarItem {

    private String event;
    private String date;

    CalendarItem(String event, String date) {
        this.event = event;
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
