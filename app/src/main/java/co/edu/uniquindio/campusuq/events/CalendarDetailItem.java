package co.edu.uniquindio.campusuq.events;

/**
 * Clase que almacena la informacion de un detalle del ítem del calendario.
 */
public class CalendarDetailItem {

    private String period;
    private String start;
    private String end;

    /**
     * Constructor que asigna los valores del detalle del ítem del calendario.
     * @param period Periodo del detalle del ítem del calendario.
     * @param start Inicio del detalle del ítem del calendario.
     * @param end Fin del detalle del ítem del calendario.
     */
    CalendarDetailItem(String period, String start, String end) {
        this.period = period;
        this.start = start;
        this.end = end;
    }

    /**
     * Método para obtener el periodo del detalle del ítem del calendario.
     * @return Periodo del detalle del ítem del calendario.
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Método para establecer el periodo del detalle del ítem del calendario.
     * @param period Nuevo periodo para el detalle del ítem del calendario.
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * Método para obtener el inicio del detalle del ítem del calendario.
     * @return Inicio del detalle del ítem del calendario.
     */
    public String getStart() {
        return start;
    }

    /**
     * Método para establecer el inicio del detalle del ítem del calendario.
     * @param start Nuevo inicio para el detalle del ítem del calendario.
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * Método para obtener el fin del detalle del ítem del calendario.
     * @return Fin del detalle del ítem del calendario.
     */
    public String getEnd() {
        return end;
    }

    /**
     * Método para establecer el fin del detalle del ítem del calendario.
     * @param end Nuevo fin para el detalle del ítem del calendario.
     */
    public void setEnd(String end) {
        this.end = end;
    }

}
