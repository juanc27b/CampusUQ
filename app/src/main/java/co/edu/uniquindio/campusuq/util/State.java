package co.edu.uniquindio.campusuq.util;

/**
 * Clase que almacena un estado de exito o fracaso durante la comunicacion con el servidor.
 */
public class State {

    private int state;

    /**
     * Constructor que asigna el estado.
     * @param state Estado de exito o fracaso.
     */
    public State(int state) {
        this.state = state;
    }

    /**
     * Obtiene el estado.
     * @return Estado de exito o fracaso.
     */
    public int get() {
        return state;
    }

    /**
     * Establece el estado.
     * @param state Estado de exito o fracaso.
     */
    public void set(int state) {
        this.state = state;
    }

}
