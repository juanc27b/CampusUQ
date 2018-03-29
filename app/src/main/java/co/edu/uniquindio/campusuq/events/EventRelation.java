package co.edu.uniquindio.campusuq.events;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class EventRelation {

    private String category_ID;
    private String event_ID;
    private String period_ID;
    private String date_ID;

    EventRelation(String category_ID, String event_ID, String period_ID, String date_ID) {
        this.category_ID = category_ID;
        this.event_ID = event_ID;
        this.period_ID = period_ID;
        this.date_ID = date_ID;
    }

    public String getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    public String getEvent_ID() {
        return event_ID;
    }

    public void setEvent_ID(String event_ID) {
        this.event_ID = event_ID;
    }

    public String getPeriod_ID() {
        return period_ID;
    }

    public void setPeriod_ID(String period_ID) {
        this.period_ID = period_ID;
    }

    public String getDate_ID() {
        return date_ID;
    }

    public void setDate_ID(String date_ID) {
        this.date_ID = date_ID;
    }

}
