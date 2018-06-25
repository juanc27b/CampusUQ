package co.edu.uniquindio.campusuq.news;

/**
 * Clase que almacena la informacion de una noticia (objeto de valor para las funcionalidades de
 * Noticias y Eventos), y permite transmitirlo entre el servidor y la base de datos local.
 */
public class New {

    private String _ID;
    private String name;
    private String link;
    private String image;
    private String summary;
    private String content;
    private String date;
    private String author;

    /**
     * Constructor de una noticia que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID de la noticia.
     * @param name Nombre de la noticia.
     * @param link Enlace de la noticia.
     * @param image Imagen de la noticia.
     * @param summary Resumen de la noticia.
     * @param content Contenido completo de la noticia.
     * @param date Fecha y hora de la noticia.
     * @param author Autor de la noticia.
     */
    New(String _ID, String name, String link, String image, String summary, String content, String date, String author) {
        this._ID = _ID;
        this.name = name;
        this.link = link;
        this.image = image;
        this.summary = summary;
        this.content = content;
        this.date = date;
        this.author = author;
    }

    /**
     * Método para obtener la ID de la noticia.
     * @return ID de la noticia.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID de la noticia.
     * @param _ID Nueva ID para la noticia.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre de la noticia.
     * @return Nombre de la noticia.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nomre de la noticia.
     * @param name Nuevo nombre para la noticia.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el enlace de la noticia.
     * @return Enlace de la noticia.
     */
    public String getLink() {
        return link;
    }

    /**
     * Método para establecer el enlace de la noticia.
     * @param link Nuevo enlace para la noticia.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Método para obtener la imagen de la noticia.
     * @return Imagen de la noticia.
     */
    public String getImage() {
        return image;
    }

    /**
     * Método para establecer la imagen de la noticia.
     * @param image Nueva imagen para la noticia.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Método para obtener el resumen de la noticia.
     * @return Resumen de la noticia.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Método para establecer el resumen de la noticia.
     * @param summary Nuevo resumen para la noticia.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Método para obtener el contenido de la noticia.
     * @return Contenido de la noticia.
     */
    public String getContent() {
        return content;
    }

    /**
     * Método para establecer el contenido de la noticia.
     * @param content Nuevo contenido para la noticia.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Método para obtener la fecha de la noticia.
     * @return Fecha de la noticia.
     */
    public String getDate() {
        return date;
    }

    /**
     * Método para establecer la fecha de la noticia.
     * @param date Nueva fecha para la noticia.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Método para obtener el autor de la noticia.
     * @return Autor de la noticia.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Método para establecer el autor de la noticia.
     * @param author Nuevo autor para la noticia.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

}
