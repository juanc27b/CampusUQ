package co.edu.uniquindio.campusuq.events;

/**
 * Clase que almacena la informacion de un ítem del calendario.
 */
public class CalendarItem {

    private String event;
    private String date;

    /**
     * Constructor que asigna los valores del ítem del calendario.
     * @param event Evento del ítem del calendario.
     * @param date Fecha del ítem del calendario.
     */
    CalendarItem(String event, String date) {
        this.event = event;
        this.date = date;
    }

    /**
     * Método para obtener el evento del ítem del calendario.
     * @return Evento del ítem del calendario.
     */
    public String getEvent() {
        return event;
    }

    /**
     * Método para establecer el evento del ítem del calendario.
     * @param event Nuevo evento para el ítem del calendario.
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Método para obtener la fecha del ítem del calendario.
     * @return Fecha del ítem del calendario.
     */
    public String getDate() {
        return date;
    }

    /**
     * Método para establecer la fecha del ítem del calendario.
     * @param date Nueva fecha para el ítem del calendario.
     */
    public void setDate(String date) {
        this.date = date;
    }

}
