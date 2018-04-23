package co.edu.uniquindio.campusuq.notifications;

public class Notification {

    private String _ID;
    private String name;
    private String activated;

    Notification(String _ID, String name, String activated) {
        this._ID = _ID;
        this.name = name;
        this.activated = activated;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

}
