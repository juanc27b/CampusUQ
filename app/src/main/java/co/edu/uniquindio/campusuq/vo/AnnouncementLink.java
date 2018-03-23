package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 2/03/2018.
 */

public class AnnouncementLink {

    private int _ID;
    private int announcement_ID;
    private String type;
    private String link;

    public AnnouncementLink(int _ID, int announcement_ID, String type, String link) {
        this._ID = _ID;
        this.announcement_ID = announcement_ID;
        this.type = type;
        this.link = link;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public int getAnnouncement_ID() {
        return announcement_ID;
    }

    public void setAnnouncement_ID(int announcement_ID) {
        this.announcement_ID = announcement_ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
