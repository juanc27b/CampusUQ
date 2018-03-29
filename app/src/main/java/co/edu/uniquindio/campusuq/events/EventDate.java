package co.edu.uniquindio.campusuq.events;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class EventDate {

    private String _ID;
    private String type;
    private String date;

    EventDate(String _ID, String type, String date) {
        this._ID = _ID;
        this.type = type;
        this.date = date;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
