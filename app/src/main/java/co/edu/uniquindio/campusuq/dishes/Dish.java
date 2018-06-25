package co.edu.uniquindio.campusuq.dishes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un plato (objeto de valor para la funcionalidad de Menú del
 * restaurante), y permite transmitirlo desde y hacia el servidor y la base de datos local.
 */
public class Dish implements Parcelable {

    private String _ID;
    private String name;
    private String description;
    private String price;
    private String image;

    /**
     * Constructor de un plato que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del plato.
     * @param name Nombre dle plato.
     * @param description Descripcion del plato.
     * @param price Precio del plato.
     * @param image Imagen del plato.
     */
    Dish(String _ID, String name, String description, String price, String image) {
        this._ID = _ID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    /**
     * Constructor de un plato que recrea los valores de sus variables a partir de un parcel.
     * @param in Parcel que contene los datos necesarios para recrear el plato.
     */
    private Dish(Parcel in) {
        _ID = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        image = in.readString();
    }

    /**
     * Escribe en un parcel los datos necesarios para recrear posteriormente el plato.
     * @param dest Parcel que contendrá los datos para recrear el plato.
     * @param flags Banderas para especificar otras opciones.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(image);
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
    public static final Creator<Dish> CREATOR = new Creator<Dish>() {
        /**
         * Crea una nueva instancia de la clase parcelable a partir de los datos provistos en el
         * parcel pasado como parámetro
         * @param in Parcel que contiene los datos para crear un plato.
         * @return Plato generado a partir del parcel.
         */
        @Override
        public Dish createFromParcel(Parcel in) {
            return new Dish(in);
        }

        /**
         * Crea un nuevo arreglo de parcelables de esta clase.
         * @param size Tamaño del arreglo a ser creado.
         * @return Arreglo de platos.
         */
        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

    /**
     * Método para obtener la ID del plato.
     * @return ID del plato.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del plato.
     * @param _ID Nueva ID para el plato.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre del plato.
     * @return Nombre del plato.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nomre del plato.
     * @param name Nuevo nomre para el plato.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener la descripcion del plato.
     * @return Descripcion del plato.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método para establecer la descripcion del plato.
     * @param description Nueva descripcion para el plato.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Método para obtener el precio del plato.
     * @return Precio del plato.
     */
    public String getPrice() {
        return price;
    }

    /**
     * Método para establecer el precio del plato.
     * @param price Nuevo precio para el plato.
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * Método para obtener la imagen del plato.
     * @return Imagen del plato.
     */
    public String getImage() {
        return image;
    }

    /**
     * Método para establecer la imagen del plato.
     * @param image Nueva imagen para el plato.
     */
    public void setImage(String image) {
        this.image = image;
    }

}
