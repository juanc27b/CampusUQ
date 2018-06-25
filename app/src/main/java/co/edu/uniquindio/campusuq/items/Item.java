package co.edu.uniquindio.campusuq.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un ítem, y permite transmitirlo entre el servidor y la base
 * de datos local.
 */
public class Item implements Parcelable {

    private int background;
    private int image;
    private String title;
    private String description;

    /**
     * Constructor de un ítem que se encarga de asignar valores a cada una de sus variables.
     * @param background Fondo de la imagen del ítem.
     * @param image Imagen del item.
     * @param title Titulo del item.
     * @param description Descripcion del item.
     */
    Item(int background, int image, String title, String description) {
        this.background = background;
        this.image = image;
        this.title = title;
        this.description = description;
    }

    /**
     * Constructor de un ítem que recrea los valores de sus variables a partir de un parcel.
     * @param in Parcel que contene los datos necesarios para recrear el ítem.
     */
    private Item(Parcel in) {
        background = in.readInt();
        image = in.readInt();
        title = in.readString();
        description = in.readString();
    }

    /**
     * Escribe en un parcel los datos necesarios para recrear posteriormente el ítem.
     * @param dest Parcel que contendrá los datos para recreal el anuncio.
     * @param flags Banderas para especificar otras opciones.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(background);
        dest.writeInt(image);
        dest.writeString(title);
        dest.writeString(description);
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
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        /**
         * Crea una nueva instancia de la clase parcelable a partir de los datos provistos en el
         * parcel pasado como parámetro
         * @param in Parcel que contiene los datos para crear un ítem.
         * @return Ítem generado a partir del parcel.
         */
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        /**
         * Crea un nuevo arreglo de parcelables de esta clase.
         * @param size Tamaño del arreglo a ser creado.
         * @return Arreglo de ítems.
         */
        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    /**
     * Método para obtener el fondo de la imagen del ítem.
     * @return Fondo de la imagen del ítem.
     */
    public int getBackground() {
        return background;
    }

    /**
     * Método para establecer el fondo de la imagen del ítem.
     * @param background Nuevo fondo de la imagen para el ítem.
     */
    public void setBackground(int background) {
        this.background = background;
    }

    /**
     * Método para obtener la imagen del ítem.
     * @return Imagen del ítem.
     */
    public int getImage() {
        return image;
    }

    /**
     * Método para establecer la imagen del ítem.
     * @param image Nueva imagen para el ítem.
     */
    public void setImage(int image) {
        this.image = image;
    }

    /**
     * Método para obtener el titulo del ítem.
     * @return Titulo del ítem.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Método para establecer el titulo del ítem.
     * @param title Nuevo titulo para el ítem.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Método para obtener la descripcion del ítem.
     * @return Descripcion del ítem.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método para establecer la descripcion del ítem.
     * @param description Nueva descripcion para el ítem.
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
