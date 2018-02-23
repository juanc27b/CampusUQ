package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 20/02/2018.
 */

public class Information {

    public String _ID;
    public String category_ID;
    public String name;
    public String content;

    public Information(String _ID, String category_ID, String name, String content) {
        this._ID = _ID;
        this.category_ID = category_ID;
        this.name = name;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
