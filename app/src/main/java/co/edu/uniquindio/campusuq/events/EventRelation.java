package co.edu.uniquindio.campusuq.events;

/**
 * Clase que almacena la informacion de una relacion de evento (objeto de valor para la
 * funcionalidad de Calendario academico), y permite transmitirla desde el servidor y desde y hacia
 * la base de datos local.
 */
public class EventRelation {

    private String category_ID;
    private String event_ID;
    private String period_ID;
    private String date_ID;

    /**
     * Constructor de una relacion de evento que se encarga de asignar valores a cada una de sus
     * variables.
     * @param category_ID ID de la categoria de evento.
     * @param event_ID ID del evento.
     * @param period_ID ID del periodo de evento.
     * @param date_ID ID de la fecha de evento.
     */
    EventRelation(String category_ID, String event_ID, String period_ID, String date_ID) {
        this.category_ID = category_ID;
        this.event_ID = event_ID;
        this.period_ID = period_ID;
        this.date_ID = date_ID;
    }

    /**
     * Método para obtener la ID de la categoria de evento.
     * @return ID de la categoria de evento.
     */
    public String getCategory_ID() {
        return category_ID;
    }

    /**
     * Método para establecer la ID de la categoria de evento.
     * @param category_ID Nueva ID para la categoria de evento.
     */
    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    /**
     * Método para obtener la ID del evento.
     * @return ID del de evento.
     */
    public String getEvent_ID() {
        return event_ID;
    }

    /**
     * Método para establecer la ID del evento.
     * @param event_ID Nueva ID para el evento.
     */
    public void setEvent_ID(String event_ID) {
        this.event_ID = event_ID;
    }

    /**
     * Método para obtener la ID del periodo de evento.
     * @return ID del periodo de evento.
     */
    public String getPeriod_ID() {
        return period_ID;
    }

    /**
     * Método para establecer la ID del periodo de evento.
     * @param period_ID Nueva ID para el periodo de evento.
     */
    public void setPeriod_ID(String period_ID) {
        this.period_ID = period_ID;
    }

    /**
     * Método para obtener la ID de la fecha de evento.
     * @return ID de la fecha de evento.
     */
    public String getDate_ID() {
        return date_ID;
    }

    /**
     * Método para establecer la ID de la fecha de evento.
     * @param date_ID Nueva ID para la fecha de evento.
     */
    public void setDate_ID(String date_ID) {
        this.date_ID = date_ID;
    }

}
