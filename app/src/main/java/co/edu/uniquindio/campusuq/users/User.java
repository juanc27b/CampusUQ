package co.edu.uniquindio.campusuq.users;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un usuario, y permite transmitirlos desde y hacia el
 * servidor y la base de datos local.
 */
public class User implements Parcelable {

    private String _ID;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String document;
    private String password;
    private String apiKey;
    private String administrator;

    /**
     * Constructor de un usuario que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del usuario.
     * @param name Nombre del usuario.
     * @param email Correo del usuario.
     * @param phone Telefono del usuario.
     * @param address Direccion del usuario.
     * @param document Documento del usuario.
     * @param password Contraseña del usuario.
     * @param apiKey Clave de api del usuario.
     * @param administrator Marcador que determina si el usuario tiene o no permisos de
     *                      arministrador.
     */
    public User(String _ID, String name, String email, String phone, String address,
                String document, String password, String apiKey, String administrator) {
        this._ID = _ID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.document = document;
        this.password = password;
        this.apiKey = apiKey;
        this.administrator = administrator;
    }

    /**
     * Constructor de un usuario que recrea los valores de sus variables a partir de un parcel.
     * @param in Parcel que contene los datos necesarios para recrear el usuario.
     */
    protected User(Parcel in) {
        _ID = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        document = in.readString();
        password = in.readString();
        apiKey = in.readString();
        administrator = in.readString();
    }

    /**
     * Escribe en un parcel los datos necesarios para recrear posteriormente el usuario.
     * @param dest Parcel que contendrá los datos para recrear el usuario.
     * @param flags Banderas para especificar otras opciones.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(document);
        dest.writeString(password);
        dest.writeString(apiKey);
        dest.writeString(administrator);
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
    public static final Creator<User> CREATOR = new Creator<User>() {
        /**
         * Crea una nueva instancia de la clase parcelable a partir de los datos provistos en el
         * parcel pasado como parámetro
         * @param in Parcel que contiene los datos para crear un usuario.
         * @return Usuario generado a partir del parcel.
         */
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        /**
         * Crea un nuevo arreglo de parcelables de esta clase.
         * @param size Tamaño del arreglo a ser creado.
         * @return Arreglo de Usuarios.
         */
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Método para obtener la ID del usuario.
     * @return ID del usuario.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del usuario.
     * @param _ID Nueva ID para el usuario.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener el nombre del usuario.
     * @return Nombre del usuario.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del usuario.
     * @param name Nuevo nombre para el usuario.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener el correo del usuario.
     * @return Correo del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método para establecer el correo del usuario.
     * @param email Nuevo correo para el usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método para obtener el telefono del usuario.
     * @return Telefono del usuario.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Método para establecer el telefono del usuario.
     * @param phone Nuevo telefono para el usuario.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Método para obtener la direccion del usuario.
     * @return Direccion del usuario.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Método para establecer la direccion del usuario.
     * @param address Nueva direccion para el usuario.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Método para obtener el documento del usuario.
     * @return Documento del usuario.
     */
    public String getDocument() {
        return document;
    }

    /**
     * Método para establecer el documento del usuario.
     * @param document Nuevo documento para el usuario.
     */
    public void setDocument(String document) {
        this.document = document;
    }

    /**
     * Método para obtener la contraseña del usuario.
     * @return Contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Método para establecer la contraseña del usuario.
     * @param password Nueva contraseña para el usuario.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Método para obtener la clave de api del usuario.
     * @return Clave de api del usuario.
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Método para establecer la clave de api del usuario.
     * @param apiKey Nueva clave de api para el usuario.
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Método para obtener la marca de administrador del usuario.
     * @return Marca de administrador del usuario.
     */
    public String getAdministrator() {
        return administrator;
    }

    /**
     * Método para establecer la marca de administrador del usuario.
     * @param administrator Nueva marca de administrador para el usuario.
     */
    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

}
