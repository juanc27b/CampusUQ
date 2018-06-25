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

    /**
     * Constructor de un enlace de un anuncio que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID del enlace de anuncio.
     * @param announcement_ID ID del anuncio.
     * @param type Tipo de enlace de anuncio (imagen o video).
     * @param link Enlace.
     */
    AnnouncementLink(String _ID, String announcement_ID, String type, String link) {
        this._ID = _ID;
        this.announcement_ID = announcement_ID;
        this.type = type;
        this.link = link;
    }

    /**
     * Método para obtener la ID del enlace de anuncio.
     * @return ID del enlace de anuncio.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del enlace de anuncio.
     * @param _ID Nueva ID para el enlace de anuncio.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la ID del anuncio.
     * @return ID del anuncio.
     */
    public String getAnnouncement_ID() {
        return announcement_ID;
    }

    /**
     * Método para establecer la ID del anuncio.
     * @param announcement_ID Nueva ID para el anuncio.
     */
    public void setAnnouncement_ID(String announcement_ID) {
        this.announcement_ID = announcement_ID;
    }

    /**
     * Método para obtener el tipo enlace de anuncio.
     * @return Tipo del enlace de anuncio.
     */
    public String getType() {
        return type;
    }

    /**
     * Método para establecer el tipo del enlace de anuncio.
     * @param type Nuevo tipo para el enlace de anuncio.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Método para obtener el enlace.
     * @return Enlace.
     */
    public String getLink() {
        return link;
    }

    /**
     * Método para establecer el enlace.
     * @param link Nuevo enlace.
     */
    public void setLink(String link) {
        this.link = link;
    }

}
