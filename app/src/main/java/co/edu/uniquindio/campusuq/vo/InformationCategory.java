package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 20/02/2018.
 */

public class InformationCategory {

    private String _ID;
    private String name;
    private String link;
    private String date;

    public InformationCategory(String _ID, String name, String link, String date) {
        this._ID = _ID;
        this.name = name;
        this.link = link;
        this.date = date;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
