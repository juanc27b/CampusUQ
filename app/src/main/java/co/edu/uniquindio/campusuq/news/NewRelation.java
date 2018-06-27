package co.edu.uniquindio.campusuq.news;

/**
 * Clase que almacena la informacion de una relacion de noticia (objeto de valor para las
 * funcionalidades de Noticias y Eventos), y permite transmitirlo entre el servidor y la base de
 * datos local.
 */
public class NewRelation {

    private String category_ID;
    private String new_ID;

    /**
     * Constructor de una relacion de noticia que se encarga de asignar valores a cada una de sus
     * variables.
     * @param category_ID ID de la categoria de noticia.
     * @param new_ID ID de la noticia.
     */
    NewRelation(String category_ID, String new_ID) {
        this.category_ID = category_ID;
        this.new_ID = new_ID;
    }

    /**
     * Método para obtener la ID de la categoria de noticia.
     * @return ID de la categoria de noticia.
     */
    public String getCategory_ID() {
        return category_ID;
    }

    /**
     * Método para establecer la ID de la categoria de noticia.
     * @param category_ID Nueva ID para la categoria de noticia.
     */
    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    /**
     * Método para obtener la ID de la noticia.
     * @return ID de la noticia.
     */
    public String getNew_ID() {
        return new_ID;
    }

    /**
     * Método para establecer la ID de la noticia.
     * @param new_ID Nueva ID para la noticia.
     */
    public void setNew_ID(String new_ID) {
        this.new_ID = new_ID;
    }

}
