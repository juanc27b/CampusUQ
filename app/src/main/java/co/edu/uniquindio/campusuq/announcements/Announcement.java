package co.edu.uniquindio.campusuq.announcements;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class Announcement {

    private String _ID;
    private String user_ID;
    private String type;
    private String name;
    private String date;
    private String description;
    private String read;

    Announcement(String _ID, String user_ID, String type, String name, String date,
                 String description, String read) {
        this._ID = _ID;
        this.user_ID = user_ID;
        this.type = type;
        this.name = name;
        this.date = date;
        this.description = description;
        this.read = read;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

}
