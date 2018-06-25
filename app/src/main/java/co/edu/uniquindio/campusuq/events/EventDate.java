package co.edu.uniquindio.campusuq.events;

/**
 * Clase que almacena la informacion de una fecha de evento (objeto de valor para la funcionalidad
 * de Calendario academico), y permite transmitirla desde el servidor y desde y hacia la base de
 * datos local.
 */
public class EventDate {

    private String _ID;
    private String type;
    private String date;

    /**
     * Constructor de una fecha de evento que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID de la fecha de evento.
     * @param type Tipo de fecha de evento.
     * @param date Fecha.
     */
    EventDate(String _ID, String type, String date) {
        this._ID = _ID;
        this.type = type;
        this.date = date;
    }

    /**
     * Método para obtener la ID de la fecha de evento.
     * @return ID de la fecha de evento.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la fecha de evento.
     * @param _ID Nueva ID para la fecha de evento.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el tipo de la fecha de evento.
     * @return Tipo de la fecha de evento.
     */
    public String getType() {
        return type;
    }

    /**
     * Método para establecer el tipo de la fecha de evento.
     * @param type Nuevo tipo para la fecha de evento.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Método para obtener la fecha.
     * @return Fecha.
     */
    public String getDate() {
        return date;
    }

    /**
     * Método para establecer la fecha.
     * @param date Nueva fecha.
     */
    public void setDate(String date) {
        this.date = date;
    }

}
