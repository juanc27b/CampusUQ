package co.edu.uniquindio.campusuq.vo;

public class Quota {

    private int _ID;
    private String type;
    private String name;
    private int quota;

    public Quota(int _ID, String type, String name, int quota) {
        this._ID = _ID;
        this.type = type;
        this.name = name;
        this.quota = quota;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
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

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

}
