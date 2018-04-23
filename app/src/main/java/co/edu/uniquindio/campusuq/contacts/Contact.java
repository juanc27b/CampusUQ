package co.edu.uniquindio.campusuq.contacts;

public class Contact {

    private String _ID;
    private String category_ID;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String charge;
    private String additionalInformation;

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

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

}
