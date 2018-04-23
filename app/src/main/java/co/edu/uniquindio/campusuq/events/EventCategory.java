package co.edu.uniquindio.campusuq.events;

public class EventCategory {

    private String _ID;
    private String abbreviation;
    private String name;

    EventCategory(String _ID, String abbreviation, String name) {
        this._ID = _ID;
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
