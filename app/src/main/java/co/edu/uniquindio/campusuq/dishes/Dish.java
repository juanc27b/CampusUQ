package co.edu.uniquindio.campusuq.dishes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un plato, y permite transmitirlo desde y hacia el servidor y
 * la base de datos local.
 */
public class Dish implements Parcelable {

    private String _ID;
    private String name;
    private String description;
    private String price;
    private String image;

    Dish(String _ID, String name, String description, String price, String image) {
        this._ID = _ID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    private Dish(Parcel in) {
        _ID = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Dish> CREATOR = new Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel in) {
            return new Dish(in);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
