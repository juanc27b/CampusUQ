package co.edu.uniquindio.campusuq.emails;

import java.math.BigInteger;

/**
 * Clase que almacena la informacion de un correo (objeto de valor para la funcionalidad de Correo
 * institucional), y permite transmitirlo desde y hacia el servidor y la base de datos local.
 */
public class Email {

    private String _ID;
    private String name;
    private String from;
    private String to;
    private String date;
    private String snippet;
    private String content;
    private BigInteger historyID;

    /**
     * Constructor de un correo que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del correo.
     * @param name Nombre del correo.
     * @param from Remitente del correo.
     * @param to Destinatario del correo.
     * @param date Fecha del correo.
     * @param snippet Resumen del correo.
     * @param content Contenido completo del correo.
     * @param historyID ID de historial del correo.
     */
    public Email(String _ID, String name, String from, String to, String date, String snippet,
                 String content, BigInteger historyID) {
        this._ID = _ID;
        this.name = name;
        this.from = from;
        this.to = to;
        this.date = date;
        this.snippet = snippet;
        this.content = content;
        this.historyID = historyID;
    }

    /**
     * Método para obtener la ID del correo.
     * @return ID del correo.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del correo.
     * @param _ID Nueva ID para el correo.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre del correo.
     * @return Nombre del correo.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del correo.
     * @param name Nuevo nombre para el correo.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el remitente del correo.
     * @return Remitente del correo.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Método para establecer el remitente del correo.
     * @param from Nuevo remitente para el correo.
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Método para obtener el destinatario del correo.
     * @return Destinatario del correo.
     */
    public String getTo() {
        return to;
    }

    /**
     * Método para establecer el destinatario del correo.
     * @param to Nuevo destinatario para el correo.
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Método para obtener la fecha del correo.
     * @return Fecha del correo.
     */
    public String getDate() {
        return date;
    }

    /**
     * Método para establecer la fecha del correo.
     * @param date Nueva fecha para el correo.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Método para obtener el resumen del correo.
     * @return Resumen del correo.
     */
    public String getSnippet() {
        return snippet;
    }

    /**
     * Método para establecer el resumen del correo.
     * @param snippet Nuevo resumen para el correo.
     */
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    /**
     * Método para obtener el contenido del correo.
     * @return Contenido del correo.
     */
    public String getContent() {
        return content;
    }

    /**
     * Método para establecer el contenido del correo.
     * @param content Nuevo contenido para el correo.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Método para obtener la ID de historial del correo.
     * @return ID de historial del correo.
     */
    public BigInteger getHistoryID() {
        return historyID;
    }

    /**
     * Método para establecer la ID de historial del correo.
     * @param historyID Nueva ID de historial para el correo.
     */
    public void setHistoryID(BigInteger historyID) {
        this.historyID = historyID;
    }

}
