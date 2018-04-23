package co.edu.uniquindio.campusuq.programs;

public class ProgramFaculty {

    private String _ID;
    private String name;

    ProgramFaculty(String _ID, String name) {
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
