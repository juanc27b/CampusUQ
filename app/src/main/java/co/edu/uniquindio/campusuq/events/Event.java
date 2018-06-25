package co.edu.uniquindio.campusuq.events;

/**
 * Clase que almacena la informacion de un evento (objeto de valor para la funcionalidad de
 * Calendario academico), y permite transmitirlo desde el servidor y desde y hacia la base de datos
 * local.
 */
public class Event {

    private String _ID;
    private String name;

    /**
     * Constructor de un evento que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del evento.
     * @param name Nombre del evento.
     */
    Event(String _ID, String name) {
        this._ID = _ID;
        this.name = name;
    }

    /**
     * Método para obtener la ID del evento.
     * @return ID del evento.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del evento.
     * @param _ID Nueva ID para el evento.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre del evento.
     * @return Nombre del evento.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del evento.
     * @param name Nuevo nombre para el evento.
     */
    public void setName(String name) {
        this.name = name;
    }

}
