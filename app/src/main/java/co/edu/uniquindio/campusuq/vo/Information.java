package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 20/02/2018.
 */

public class Information {

    public String id;
    public String categoryId;
    public String name;
    public String content;

    public Information(String id, String categoryId, String name, String content) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
