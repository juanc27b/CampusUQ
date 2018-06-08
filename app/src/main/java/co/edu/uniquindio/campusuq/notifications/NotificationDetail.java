package co.edu.uniquindio.campusuq.notifications;

public class NotificationDetail {

    private String _ID;
    private int category;
    private String name;
    private String dateTime;
    private String description;

    public NotificationDetail(String _ID, int category, String name, String dateTime,
                              String description) {
        this._ID = _ID;
        this.category = category;
        this.name = name;
        this.dateTime = dateTime;
        this.description = description;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
