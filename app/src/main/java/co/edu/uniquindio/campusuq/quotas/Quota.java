package co.edu.uniquindio.campusuq.quotas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que almacena la informacion de un cupo (objeto de valor para las funcionalidades de salas
 * de cómputo, parqueaderos, laboratorios, zonas de estudio, espacios culturales y deportivos, y
 * auditorios), y permite transmitirlos desde y hacia el servidor y la base de datos local.
 */
public class Quota implements Parcelable {

    private String _ID;
    private String type;
    private String name;
    private String quota;

    Quota(String _ID, String type, String name, String quota) {
        this._ID = _ID;
        this.type = type;
        this.name = name;
        this.quota = quota;
    }

    private Quota(Parcel in) {
        _ID = in.readString();
        type = in.readString();
        name = in.readString();
        quota = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(quota);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Quota> CREATOR = new Creator<Quota>() {
        @Override
        public Quota createFromParcel(Parcel in) {
            return new Quota(in);
        }

        @Override
        public Quota[] newArray(int size) {
            return new Quota[size];
        }
    };

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
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

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

}
