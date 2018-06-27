package co.edu.uniquindio.campusuq.news;

/**
 * Clase que almacena la informacion de una categoria de noticia (objeto de valor para las
 * funcionalidades de Noticias y Eventos), y permite transmitirlo entre el servidor y la base de
 * datos local.
 */
public class NewCategory {

    private String _ID;
    private String name;
    private String link;

    /**
     * Constructor de una categoria de noticia que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID de la categoria de noticia.
     * @param name Nombre de la categoria de noticia.
     * @param link Enlace de la categoria de noticia.
     */
    NewCategory(String _ID, String name, String link) {
        this._ID = _ID;
        this.name = name;
        this.link = link;
    }

    /**
     * Método para obtener la ID de la categoria de noticia.
     * @return ID de la categoria de noticia.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la categoria de noticia.
     * @param _ID Nueva ID para la categoria de noticia.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre de la categoria de noticia.
     * @return Nombre de la categoria de noticia.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre de la categoria de noticia.
     * @param name Nuevo nombre para la categoria de noticia.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el enlace de la categoria de noticia.
     * @return ID del enlace de la categoria de noticia.
     */
    public String getLink() {
        return link;
    }

    /**
     * Método para establecer el enlace de la categoria de noticia.
     * @param link Nuevo enlace para la categoria de noticia.
     */
    public void setLink(String link) {
        this.link = link;
    }

}
