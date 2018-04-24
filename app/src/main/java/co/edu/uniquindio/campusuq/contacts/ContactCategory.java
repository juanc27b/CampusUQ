package co.edu.uniquindio.campusuq.contacts;

/**
 * Clase que almacena la información de una categoría de contacto (objeto de valor para la
 * funcionalidad del Directorio telefónico) y permite transmitirlo entre el servidor y la base
 * de datos local.
 */
public class ContactCategory {

    private String _ID;
    private String name;
    private String link;

    /**
     * Constructor de una categoría de contacto que asigna valores a cada una de sus variables.
     * @param _ID ID de la categoría de contacto.
     * @param name Nombre de la categoría del contacto.
     * @param link Enlace web de la categoría del contacto.
     */
    ContactCategory(String _ID, String name, String link) {
        this._ID = _ID;
        this.name = name;
        this.link = link;
    }

    /**
     * Método para obtener la ID de la categoría de contacto.
     * @return ID de la categoría de contacto.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la categoría de contacto.
     * @param _ID Nueva ID para la categoría de contacto.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre de la categoría de contacto.
     * @return Nombre de la categoría de contacto.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre de la categoría de contacto.
     * @param name Nuevo nombre para la categoría de contacto.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el enlace web de la categoría de contacto.
     * @return Enlace web de la categoría de contacto.
     */
    public String getLink() {
        return link;
    }

    /**
     * Método para establecer el enlace web de la categoría de contacto.
     * @param link Nuevo enlace web para la categoría de contacto.
     */
    public void setLink(String link) {
        this.link = link;
    }

}
