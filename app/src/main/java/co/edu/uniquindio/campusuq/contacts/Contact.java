package co.edu.uniquindio.campusuq.contacts;

/**
 * Clase que almacena la información de un contacto (objeto de valor para la funcionalidad de
 * Directorio telefónico) y permite transmitirlo entre del servidor y la base de datos local.
 */
public class Contact {

    private String _ID;
    private String category_ID;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String charge;
    private String additionalInformation;

    /**
     * Constructor de un contacto que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del contacto.
     * @param category_ID ID de la categoría del contacto.
     * @param name Nombre del contacto.
     * @param address Dirección del contacto.
     * @param phone Teléfono del contacto.
     * @param email Correo electrónico del contacto.
     * @param charge Puesto o cargo del contacto.
     * @param additionalInformation Información adicional del contacto.
     */
    Contact(String _ID, String category_ID, String name, String address, String phone, String email, String charge, String additionalInformation) {
        this._ID = _ID;
        this.category_ID = category_ID;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.charge = charge;
        this.additionalInformation = additionalInformation;
    }

    /**
     * Método para obtener la ID del contacto.
     * @return ID del contacto.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del contacto.
     * @param _ID Nueva ID para el contacto.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la ID de la categoría del contacto.
     * @return ID de la categoría del contacto.
     */
    public String getCategory_ID() {
        return category_ID;
    }

    /**
     * Método para establecer la ID de la categoría del contacto.
     * @param category_ID Nueva ID para la categoría del contacto.
     */
    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    /**
     * Método para obtener el nombre del contacto.
     * @return Nombre del contacto.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del contacto.
     * @param name Nuevo nombre para el contacto.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener la dirección del contacto.
     * @return Dirección del contacto.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Método para establecer la dirección del contacto.
     * @param address Nueva dirección para el contacto.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Método para obtener el teléfono del contacto.
     * @return Teléfono del contacto.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Método para establecer el teléfono del contacto.
     * @param phone Nuevo teléfono para el contacto.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Método para obtener el correo electrónico del contacto.
     * @return Correo electrónico del contacto.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método para establecer el correo electrónico del contacto.
     * @param email Nuevo correo electrónico del contacto.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método para obtener el cargo del contacto.
     * @return Cargo del contacto.
     */
    public String getCharge() {
        return charge;
    }

    /**
     * Método para establecer el cargo del contacto.
     * @param charge Nuevo cargo del contacto.
     */
    public void setCharge(String charge) {
        this.charge = charge;
    }

    /**
     * Método para obtener la información adicional del contacto.
     * @return Información adicional del contacto.
     */
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Método para establecer la información adicional del contacto.
     * @param additionalInformation Nueva información adicional para el contacto.
     */
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

}
