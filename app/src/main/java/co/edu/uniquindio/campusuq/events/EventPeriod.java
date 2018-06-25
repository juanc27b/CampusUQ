package co.edu.uniquindio.campusuq.events;

/**
 * Clase que almacena la informacion de un periodo de evento (objeto de valor para la funcionalidad
 * de Calendario academico), y permite transmitirlo desde el servidor y desde y hacia la base de
 * datos local.
 */
public class EventPeriod {

    private String _ID;
    private String name;

    /**
     * Constructor de un periodo de evento que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID del periodo de evento.
     * @param name Nombre del periodo de evento.
     */
    EventPeriod(String _ID, String name) {
        this._ID = _ID;
        this.name = name;
    }

    /**
     * Método para obtener la ID del periodo de evento.
     * @return ID del periodo de evento.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del periodo de evento.
     * @param _ID Nueva ID para el periodo de evento.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre del periodo de evento.
     * @return Nombre del periodo de evento.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del periodo de evento.
     * @param name Nuevo nombre para el periodo de evento.
     */
    public void setName(String name) {
        this.name = name;
    }

}
