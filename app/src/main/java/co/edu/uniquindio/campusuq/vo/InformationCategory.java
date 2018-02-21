package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 20/02/2018.
 */

public class InformationCategory {

    public String id;
    public String name;
    public String link;
    public String date;

    public InformationCategory(String id, String name, String link, String date) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
