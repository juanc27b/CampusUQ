package co.edu.uniquindio.campusuq.programs;

/**
 * Clase que almacena la informacion de una facultad de programa (objeto de valor para la
 * funcionalidad de Oferta academica), y permite transmitirlo desde el servidor y  desde y hacia la
 * base de datos local.
 */
public class ProgramFaculty {

    private String _ID;
    private String name;

    /**
     * Constructor de una facultad de programa que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID de la facultad de programa.
     * @param name Nombre de la facultad de programa.
     */
    ProgramFaculty(String _ID, String name) {
        this._ID = _ID;
        this.name = name;
    }

    /**
     * Método para obtener la ID de la facultad de programa.
     * @return ID de la facultad de programa.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la facultad de programa.
     * @param _ID Nueva ID para la facultad de programa.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre de la facultad de programa.
     * @return Nombre de la facultad de programa.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre de la facultad de programa.
     * @param name Nuevo nombre para la facultad de programa.
     */
    public void setName(String name) {
        this.name = name;
    }

}
