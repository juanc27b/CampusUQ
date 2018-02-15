package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewRelation {

    public String categoryId;
    public String newId;

    public NewRelation(String categoryId, String newId) {
        this.categoryId = categoryId;
        this.newId = newId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

}
