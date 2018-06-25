package co.edu.uniquindio.campusuq.informations;

/**
 * Clase que almacena una categoria de informacion y permite transmitirlo desde y hacia el servidor
 * y la base de datos local.
 */
public class InformationCategory {

    private String _ID;
    private String name;
    private String link;
    private String date;

    /**
     * Constructor de una categoria de informacion que se encarga de asignar valores a cada una de
     * sus variables.
     * @param _ID ID de la categoria de informacion.
     * @param name Nombre de la categoria de informacion.
     * @param link Enlace de la categoria de informacion.
     * @param date Fecha de la categoria de informacion.
     */
    InformationCategory(String _ID, String name, String link, String date) {
        this._ID = _ID;
        this.name = name;
        this.link = link;
        this.date = date;
    }

    /**
     * Método para obtener la ID de la categoria de informacion.
     * @return ID de la categoria de informacion.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la categoria de informacion.
     * @param _ID Nueva ID para la categoria de informacion.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre de la categoria de informacion.
     * @return Nombre de la categoria de informacion.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre de la categoria de informacion.
     * @param name Nuevo nombre para la categoria de informacion.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el enlace de la categoria de informacion.
     * @return Enlace de la categoria de informacion.
     */
    public String getLink() {
        return link;
    }

    /**
     * Método para establecer el enlace de la categoria de informacion.
     * @param link Nuevo enlace para la categoria de informacion.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Método para obtener la fecha de la categoria de informacion.
     * @return Fecha de la categoria de informacion.
     */
    public String getDate() {
        return date;
    }

    /**
     * Método para establecer la fecha de la categoria de informacion.
     * @param date Nueva fecha para la categoria de informacion.
     */
    public void setDate(String date) {
        this.date = date;
    }

}
