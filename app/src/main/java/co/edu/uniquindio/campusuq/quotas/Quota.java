package co.edu.uniquindio.campusuq.quotas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un cupo (objeto de valor para las funcionalidades de Salas
 * de cómputo, Parqueaderos, Laboratorios, Zonas de estudio, Espacios culturales y deportivos, y
 * Auditorios), y permite transmitirlos desde y hacia el servidor y la base de datos local.
 */
public class Quota implements Parcelable {

    private String _ID;
    private String type;
    private String name;
    private String quota;

    /**
     * Constructor de un cupo que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del cupo.
     * @param type Tipo de cupo.
     * @param name Nombre del cupo.
     * @param quota Cupo.
     */
    Quota(String _ID, String type, String name, String quota) {
        this._ID = _ID;
        this.type = type;
        this.name = name;
        this.quota = quota;
    }

    /**
     * Constructor de un cupo que recrea los valores de sus variables a partir de un parcel.
     * @param in Parcel que contene los datos necesarios para recrear el cupo.
     */
    private Quota(Parcel in) {
        _ID = in.readString();
        type = in.readString();
        name = in.readString();
        quota = in.readString();
    }

    /**
     * Escribe en un parcel los datos necesarios para recrear posteriormente el cupo.
     * @param dest Parcel que contendrá los datos para recrear el cupo.
     * @param flags Banderas para especificar otras opciones.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(quota);
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
    public static final Creator<Quota> CREATOR = new Creator<Quota>() {
        /**
         * Crea una nueva instancia de la clase parcelable a partir de los datos provistos en el
         * parcel pasado como parámetro
         * @param in Parcel que contiene los datos para crear un cupo.
         * @return Cupo generado a partir del parcel.
         */
        @Override
        public Quota createFromParcel(Parcel in) {
            return new Quota(in);
        }

        /**
         * Crea un nuevo arreglo de parcelables de esta clase.
         * @param size Tamaño del arreglo a ser creado.
         * @return Arreglo de cupos.
         */
        @Override
        public Quota[] newArray(int size) {
            return new Quota[size];
        }
    };

    /**
     * Método para obtener la ID del cupo.
     * @return ID del cupo.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del cupo.
     * @param _ID Nueva ID para el cupo.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el tipo del cupo.
     * @return Tipo del cupo.
     */
    public String getType() {
        return type;
    }

    /**
     * Método para establecer el tipo del cupo.
     * @param type Nuevo tipo para el cupo.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Método para obtener el nombre del cupo.
     * @return Nombre del cupo.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del cupo.
     * @param name Nuevo nombre para el cupo.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el cupo.
     * @return Cupo.
     */
    public String getQuota() {
        return quota;
    }

    /**
     * Método para establecer el cupo.
     * @param quota Nuevo cupo.
     */
    public void setQuota(String quota) {
        this.quota = quota;
    }

}
