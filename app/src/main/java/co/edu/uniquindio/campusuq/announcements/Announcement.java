package co.edu.uniquindio.campusuq.announcements;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un anuncio (objeto de valor para las funcionalidades de
 * Sistema de seguridad e Información de cartelera), y permite transmitirlo a través del servidor
 * y la base de datos local.
 */
public class Announcement implements Parcelable {

    private String _ID;
    private String user_ID;
    private String type;
    private String name;
    private String date;
    private String description;
    private String read;

    /**
     * Constructor de un anuncio que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del anuncio.
     * @param user_ID ID del usuario que publicó el anuncio.
     * @param type Tipo del anuncio, puede ser I para incidente (Sistema de seguridad) o C para
     *             comunicado (Información de cartelera).
     * @param name Nombre del anuncio.
     * @param date Fecha en que se publicó el anuncio.
     * @param description Descripción del anuncio.
     * @param read Marcación de leído sobre el anuncio.
     */
    Announcement(String _ID, String user_ID, String type, String name, String date,
                 String description, String read) {
        this._ID = _ID;
        this.user_ID = user_ID;
        this.type = type;
        this.name = name;
        this.date = date;
        this.description = description;
        this.read = read;
    }

    /**
     * Constructor de un anuncio que recrea los valores de sus variables a partir de un parcel.
     * @param in Parcel que contene los datos necesarios para recrear el anuncio.
     */
    private Announcement(Parcel in) {
        _ID = in.readString();
        user_ID = in.readString();
        type = in.readString();
        name = in.readString();
        date = in.readString();
        description = in.readString();
        read = in.readString();
    }

    /**
     * Escribe en un parcel los datos necesarios para recrear posteriormente el anuncio.
     * @param dest Parcel que contendrá los datos para recreal el anuncio.
     * @param flags Banderas para especificar otras opciones.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(user_ID);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(read);
    }

    /**
     * Describe los tipos de objetos especiales contenidos en el parcel.
     * @return Máscara de bits que indica los tipos de objetos especiales en el parcel.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Interfaz para generar instancias de parcelables de esta clase a partir de un parcel.
     */
    public static final Creator<Announcement> CREATOR = new Creator<Announcement>() {
        /**
         * Crea una nueva instancia de la clase parcelable a partir de los datos provistos en el
         * parcel pasado como parámetro
         * @param in Parcel que contiene los datos para crear un anuncio.
         * @return Anuncio generado a partir del parcel.
         */
        @Override
        public Announcement createFromParcel(Parcel in) {
            return new Announcement(in);
        }

        /**
         * Crea un nuevo arreglo de parcelables de esta clase.
         * @param size Tamaño del arreglo a ser creado.
         * @return Arreglo de anuncios.
         */
        @Override
        public Announcement[] newArray(int size) {
            return new Announcement[size];
        }
    };

    /**
     * Método para obtener la ID del anuncio.
     * @return ID del anuncio.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del anuncio.
     * @param _ID Nueva ID para el anuncio.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la ID del propietario del anuncio.
     * @return ID del usuario que publicó el anuncio.
     */
    public String getUser_ID() {
        return user_ID;
    }

    /**
     * Método para establecer la ID del propietario del anuncio.
     * @param user_ID Nueva ID para el propietario del anuncio.
     */
    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    /**
     * Método para obtener el tipo del anuncio.
     * @return Tipo del anuncio.
     */
    public String getType() {
        return type;
    }

    /**
     * Método para establecer el tipo del anuncio.
     * @param type Nuevo tipo del anuncio.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Método para obtener el nombre del anuncio.
     * @return Nombre del anuncio.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del anuncio.
     * @param name Nuevo nombre del anuncio.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener la fecha del anuncio.
     * @return Fecha del anuncio.
     */
    public String getDate() {
        return date;
    }

    /**
     * Método para establecer la fecha del anuncio.
     * @param date Nueva fecha del anuncio.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Método para obtener la descripción del anuncio.
     * @return Descripción del anuncio.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método para establecer la descripción del anuncio.
     * @param description Nueva descripción del anuncio.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Método para obtener la marcación de leído sobre el anuncio.
     * @return Marcación de leído sobre el anuncio.
     */
    public String getRead() {
        return read;
    }

    /**
     * Método para establecer la marcación de leído sobre el anuncio.
     * @param read Nueva marcación de leído sobre el anuncio.
     */
    public void setRead(String read) {
        this.read = read;
    }

}
