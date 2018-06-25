package co.edu.uniquindio.campusuq.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un objeto perdido, y permite transmitirlo desde y hacia el
 * servidor y la base de datos local.
 */
public class LostObject implements Parcelable {

    private String _ID;
    private String userLost_ID;
    private String name;
    private String place;
    private String dateLost;
    private String date;
    private String description;
    private String image;
    private String userFound_ID;
    private String readed;

    /**
     * Constructor de un objeto perdido que se encarga de asignar valores a cada una de sus
     * variables.
     * @param _ID ID del objeto perdido.
     * @param userLost_ID ID del usuario que perdio el objeto.
     * @param name Nombre del objeto perdido.
     * @param place Lugar en el cual se perdio el objeto.
     * @param dateLost Fecha en la cual se perdio el objeto.
     * @param date Fecha de publicacion o actualizacion del reporte del objeto perdido.
     * @param description Descripcion del objeto perdido.
     * @param image Imagen del objeto perdido.
     * @param userFound_ID Usuario que encontro el objeto perdido.
     * @param readed Marca que indica si ya se ha leido el reporte del objeto perdido.
     */
    LostObject(String _ID, String userLost_ID, String name, String place, String dateLost,
               String date, String description, String image, String userFound_ID, String readed) {
        this._ID = _ID;
        this.userLost_ID = userLost_ID;
        this.name = name;
        this.place = place;
        this.dateLost = dateLost;
        this.date = date;
        this.description = description;
        this.image = image;
        this.userFound_ID = userFound_ID;
        this.readed = readed;
    }

    /**
     * Constructor de un objeto perdido que recrea los valores de sus variables a partir de un
     * parcel.
     * @param in Parcel que contene los datos necesarios para recrear el objeto perdido.
     */
    private LostObject(Parcel in) {
        _ID = in.readString();
        userLost_ID = in.readString();
        name = in.readString();
        place = in.readString();
        dateLost = in.readString();
        date = in.readString();
        description = in.readString();
        image = in.readString();
        userFound_ID = in.readString();
        readed = in.readString();
    }

    /**
     * Escribe en un parcel los datos necesarios para recrear posteriormente el objeto perdido.
     * @param dest Parcel que contendrá los datos para recrear el objeto perdido.
     * @param flags Banderas para especificar otras opciones.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(userLost_ID);
        dest.writeString(name);
        dest.writeString(place);
        dest.writeString(dateLost);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(userFound_ID);
        dest.writeString(readed);
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
    public static final Creator<LostObject> CREATOR = new Creator<LostObject>() {
        /**
         * Crea una nueva instancia de la clase parcelable a partir de los datos provistos en el
         * parcel pasado como parámetro
         * @param in Parcel que contiene los datos para crear un objeto perdido.
         * @return Objeto perdido generado a partir del parcel.
         */
        @Override
        public LostObject createFromParcel(Parcel in) {
            return new LostObject(in);
        }

        /**
         * Crea un nuevo arreglo de parcelables de esta clase.
         * @param size Tamaño del arreglo a ser creado.
         * @return Arreglo de objetos perdidos.
         */
        @Override
        public LostObject[] newArray(int size) {
            return new LostObject[size];
        }
    };

    /**
     * Método para obtener la ID del objeto perdido.
     * @return ID del objeto perdido.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del objeto perdido.
     * @param _ID Nueva ID para el objeto perdido.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la ID del usuario dueño del objeto perdido.
     * @return ID del usuario dueño del objeto perdido.
     */
    public String getUserLost_ID() {
        return userLost_ID;
    }

    /**
     * Método para establecer la ID del usuario dueño del objeto perdido.
     * @param userLost_ID Nueva ID para el usuario dueño del objeto perdido.
     */
    public void setUserLost_ID(String userLost_ID) {
        this.userLost_ID = userLost_ID;
    }

    /**
     * Método para obtener el nombre del objeto perdido.
     * @return Nombre del objeto perdido.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del objeto perdido.
     * @param name Nuevo nombre para el objeto perdido.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el lugar de perdida del objeto perdido.
     * @return Lugar de perdida del objeto perdido.
     */
    public String getPlace() {
        return place;
    }

    /**
     * Método para establecer el lugar de perdida del objeto perdido.
     * @param place Nuevo lugar de perdida para el objeto perdido.
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * Método para obtener la fecha de perdida del objeto perdido.
     * @return Fecha de perdida del objeto perdido.
     */
    public String getDateLost() {
        return dateLost;
    }

    /**
     * Método para establecer la fecha de perdida del objeto perdido.
     * @param dateLost Nueva fecha de perdida para el objeto perdido.
     */
    public void setDateLost(String dateLost) {
        this.dateLost = dateLost;
    }

    /**
     * Método para obtener la fecha de publicacion o actualizacion del reporte del objeto perdido.
     * @return Fecha de publicacion o actualizacion del reporte del objeto perdido.
     */
    public String getDate() {
        return date;
    }

    /**
     * Método para establecer la fecha de publicacion o actualizacion del reporte del objeto
     * perdido.
     * @param date Nueva fecha de publicacion o actualizacion del reporte para el objeto perdido.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Método para obtener la descripcion del objeto perdido.
     * @return Descripcion del objeto perdido.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método para establecer la descripcion del objeto perdido.
     * @param description Nueva descripcion para el objeto perdido.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Método para obtener la imagen del objeto perdido.
     * @return Imagen del objeto perdido.
     */
    public String getImage() {
        return image;
    }

    /**
     * Método para establecer la imagen del objeto perdido.
     * @param image Nueva imagen para el objeto perdido.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Método para obtener la ID del usuario que encontro el objeto perdido.
     * @return ID del usuario que encontro el objeto perdido.
     */
    public String getUserFound_ID() {
        return userFound_ID;
    }

    /**
     * Método para establecer la ID del usuario que encontro el objeto perdido.
     * @param userFound_ID Nueva ID para el usuario que encontro el objeto perdido.
     */
    public void setUserFound_ID(String userFound_ID) {
        this.userFound_ID = userFound_ID;
    }

    /**
     * Método para obtener la marca que indica si ya se ha leido el reporte del objeto perdido.
     * @return Marca que indica si ya se ha leido el reporte del objeto perdido.
     */
    public String getReaded() {
        return readed;
    }

    /**
     * Método para establecer la marca que indica si ya se ha leido el reporte del objeto perdido.
     * @param readed Nueva marca que indica si ya se ha leido el reporte para el objeto perdido.
     */
    public void setReaded(String readed) {
        this.readed = readed;
    }

}
