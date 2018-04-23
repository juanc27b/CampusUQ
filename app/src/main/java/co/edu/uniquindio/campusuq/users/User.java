package co.edu.uniquindio.campusuq.users;

import android.os.Parcel;
import android.os.Parcelable;

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

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel in) {
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

}
