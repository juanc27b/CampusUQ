package co.edu.uniquindio.campusuq.vo;

/**
 * Created by JuanCamilo on 23/02/2018.
 */

public class Quota {
    public String _ID;
    public String type;
    public String name;
    public String quota;

    public Quota(String _ID, String type, String name, String quota) {
        this._ID = _ID;
        this.type = type;
        this.name = name;
        this.quota = quota;
    }

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
