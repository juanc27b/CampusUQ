package co.edu.uniquindio.campusuq.notifications;

/**
 * Clase que almacena la informacion de un detalle de notificacion (objeto de valor para la
 * funcionalidad de Bandeja de notificaciones), y permite transmitirlo desde y hacia la base de
 * datos local.
 */
public class NotificationDetail {

    private String _ID;
    private int category;
    private String name;
    private String dateTime;
    private String description;

    /**
     * Constructor de un detalle de notificacion que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID de detalle de notificacion.
     * @param category Categoria de detalle de notificacion.
     * @param name Nombre de detalle de notificacion.
     * @param dateTime Fecha y hora de detalle de notificacion.
     * @param description Descripcion de detalle de notificacion.
     */
    public NotificationDetail(String _ID, int category, String name, String dateTime,
                              String description) {
        this._ID = _ID;
        this.category = category;
        this.name = name;
        this.dateTime = dateTime;
        this.description = description;
    }

    /**
     * Método para obtener la ID del detalle de notificacion.
     * @return ID del detalle de notificacion.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del detalle de notificacion.
     * @param _ID Nueva ID para el detalle de notificacion.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la categoria del detalle de notificacion.
     * @return Categoria del detalle de notificacion.
     */
    public int getCategory() {
        return category;
    }

    /**
     * Método para establecer la categoria del detalle de notificacion.
     * @param category Nueva categoria para el detalle de notificacion.
     */
    public void setCategory(int category) {
        this.category = category;
    }

    /**
     * Método para obtener el nombre del detalle de notificacion.
     * @return Nombre del detalle de notificacion.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del detalle de notificacion.
     * @param name Nuevo nombre para el detalle de notificacion.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener la fecha y hora del detalle de notificacion.
     * @return Fecha y hora del detalle de notificacion.
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Método para establecer la fecha y hora del detalle de notificacion.
     * @param dateTime Nueva fecha y hora para el detalle de notificacion.
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Método para obtener la descripcion del detalle de notificacion.
     * @return Descripcion del detalle de notificacion.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método para establecer la descripcion del detalle de notificacion.
     * @param description Nueva descripcion para el detalle de notificacion.
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
