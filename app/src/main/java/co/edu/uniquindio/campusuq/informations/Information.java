package co.edu.uniquindio.campusuq.informations;

/**
 * Clase que almacena una informacion y permite transmitirlo desde y hacia el servidor y la base de
 * datos local.
 */
public class Information {

    private String _ID;
    private String category_ID;
    private String name;
    private String content;

    /**
     * Constructor de una informacion que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID de la informacion.
     * @param category_ID ID de la categoria de la informacion.
     * @param name Nombre de la informacion.
     * @param content Contenido de la informacion.
     */
    Information(String _ID, String category_ID, String name, String content) {
        this._ID = _ID;
        this.category_ID = category_ID;
        this.name = name;
        this.content = content;
    }

    /**
     * Método para obtener la ID de la informacion.
     * @return ID de la informacion.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la informacion.
     * @param _ID Nueva ID para la informacion.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la ID de la categoria de la informacion.
     * @return ID de la categoria de la informacion.
     */
    public String getCategory_ID() {
        return category_ID;
    }

    /**
     * Método para establecer la ID de la categoria de la informacion.
     * @param category_ID Nueva ID de la categoria para la informacion.
     */
    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    /**
     * Método para obtener el nombre de la informacion.
     * @return Nombre de la informacion.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre de la informacion.
     * @param name Nuevo nombre para la informacion.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el contenido de la informacion.
     * @return Contenido de la informacion.
     */
    public String getContent() {
        return content;
    }

    /**
     * Método para establecer el contenido de la informacion.
     * @param content Nuevo contenido para la informacion.
     */
    public void setContent(String content) {
        this.content = content;
    }

}
