package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 23/02/2018.
 */

public class ContactCategory {

    private String _ID;
    private String name;
    private String link;

    public ContactCategory(String _ID, String name, String link) {
        this._ID = _ID;
        this.name = name;
        this.link = link;
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

}
