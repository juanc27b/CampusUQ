package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class ProgramCategory {

    private String _ID;
    private String name;

    public ProgramCategory(String _ID, String name) {
        this._ID = _ID;
        this.name = name;
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

}
