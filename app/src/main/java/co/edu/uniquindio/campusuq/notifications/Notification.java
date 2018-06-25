package co.edu.uniquindio.campusuq.notifications;

/**
 * Clase que almacena la informacion de una notificacion (objeto de valor para la funcionalidad de
 * Ajustar notificaciones), y permite transmitirlo desde y hacia la base de datos local.
 */
public class Notification {

    private String _ID;
    private String name;
    private String activated;


    /**
     * Constructor de una notificacion que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID de la notificacion.
     * @param name Nombre de la notificacion.
     * @param activated Estado de activacion de la notificacion.
     */
    Notification(String _ID, String name, String activated) {
        this._ID = _ID;
        this.name = name;
        this.activated = activated;
    }

    /**
     * Método para obtener la ID de la notificacion.
     * @return ID de la notificacion.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la notificacion.
     * @param _ID Nueva ID para la notificacion.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre de la notificacion.
     * @return Nombre de la notificacion.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre de la notificacion.
     * @param name Nuevo nombre para la notificacion.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el estado de activacion de la notificacion.
     * @return Estado de activacion de la notificacion.
     */
    public String getActivated() {
        return activated;
    }

    /**
     * Método para establecer el estado de activacion de la notificacion.
     * @param activated Nuevo estado de activacion para la notificacion.
     */
    public void setActivated(String activated) {
        this.activated = activated;
    }

}
