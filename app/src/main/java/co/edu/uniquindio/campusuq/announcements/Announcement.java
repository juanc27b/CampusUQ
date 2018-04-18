package co.edu.uniquindio.campusuq.announcements;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un anuncio (objeto de valor para las funcionalidades de
 * incidentes y comunicados), y permite transmitirlo desde y hacia el servidor y la base de datos
 * local.
 */
public class Announcement implements Parcelable {

    private String _ID;
    private String user_ID;
    private String type;
    private String name;
    private String date;
    private String description;
    private String read;

    Announcement(String _ID, String user_ID, String type, String name, String date,
                 String description, String read) {
        this._ID = _ID;
        this.user_ID = user_ID;
        this.type = type;
        this.name = name;
        this.date = date;
        this.description = description;
        this.read = read;
    }

    private Announcement(Parcel in) {
        _ID = in.readString();
        user_ID = in.readString();
        type = in.readString();
        name = in.readString();
        date = in.readString();
        description = in.readString();
        read = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(user_ID);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(read);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Announcement> CREATOR = new Creator<Announcement>() {
        @Override
        public Announcement createFromParcel(Parcel in) {
            return new Announcement(in);
        }

        @Override
        public Announcement[] newArray(int size) {
            return new Announcement[size];
        }
    };

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

}
