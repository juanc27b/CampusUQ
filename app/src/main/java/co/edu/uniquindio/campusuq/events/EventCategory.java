package co.edu.uniquindio.campusuq.events;

/**
 * Clase que almacena la informacion de una categoria de evento (objeto de valor para la
 * funcionalidad de Calendario academico), y permite transmitirla desde el servidor y desde y hacia
 * la base de datos local.
 */
public class EventCategory {

    private String _ID;
    private String abbreviation;
    private String name;

    /**
     * Constructor de una categoria de evento que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID de ña categoria de evento.
     * @param abbreviation Abreviacion de la categoria de evento.
     * @param name Nombre de la categoria de evento.
     */
    EventCategory(String _ID, String abbreviation, String name) {
        this._ID = _ID;
        this.abbreviation = abbreviation;
        this.name = name;
    }

    /**
     * Método para obtener la ID de la categoria de evento.
     * @return ID de la categoria de evento.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la categoria de evento.
     * @param _ID Nueva ID para la categoria de evento.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la abreviacion de la categoria de evento.
     * @return Abreviacion de la categoria de evento.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Método para establecer la abreviacion de la categoria de evento.
     * @param abbreviation Nueva abreviacion para la categoria de evento.
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Método para obtener el nombre de la categoria de evento.
     * @return Nombre de la categoria de evento.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre de la categoria de evento.
     * @param name Nuevo nombre para la categoria de evento.
     */
    public void setName(String name) {
        this.name = name;
    }

}
