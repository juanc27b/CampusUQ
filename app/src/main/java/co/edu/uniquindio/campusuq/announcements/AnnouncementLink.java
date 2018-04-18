package co.edu.uniquindio.campusuq.announcements;

/**
 * Clase que almacena la informacion del enlace (imagen o video) de un anuncio (objeto de valor para
 * las funcionalidades de incidentes y comunicados), y permite transmitirlo desde y hacia el
 * servidor y la base de datos local.
 */
public class AnnouncementLink {

    private String _ID;
    private String announcement_ID;
    private String type;
    private String link;

    AnnouncementLink(String _ID, String announcement_ID, String type, String link) {
        this._ID = _ID;
        this.announcement_ID = announcement_ID;
        this.type = type;
        this.link = link;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getAnnouncement_ID() {
        return announcement_ID;
    }

    public void setAnnouncement_ID(String announcement_ID) {
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
