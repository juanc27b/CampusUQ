package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewCategory {

    public String id;
    public String name;
    public String link;

    public NewCategory(String id, String name, String link) {
        this.id = id;
        this.name = name;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
