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

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LostObject> CREATOR = new Creator<LostObject>() {
        @Override
        public LostObject createFromParcel(Parcel in) {
            return new LostObject(in);
        }

        @Override
        public LostObject[] newArray(int size) {
            return new LostObject[size];
        }
    };

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getUserLost_ID() {
        return userLost_ID;
    }

    public void setUserLost_ID(String userLost_ID) {
        this.userLost_ID = userLost_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDateLost() {
        return dateLost;
    }

    public void setDateLost(String dateLost) {
        this.dateLost = dateLost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserFound_ID() {
        return userFound_ID;
    }

    public void setUserFound_ID(String userFound_ID) {
        this.userFound_ID = userFound_ID;
    }

    public String getReaded() {
        return readed;
    }

    public void setReaded(String readed) {
        this.readed = readed;
    }

}
