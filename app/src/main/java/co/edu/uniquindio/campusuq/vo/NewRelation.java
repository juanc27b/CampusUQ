package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 13/02/2018.
 */

public class NewRelation {

    public String category_ID;
    public String new_ID;

    public NewRelation(String category_ID, String new_ID) {
        this.category_ID = category_ID;
        this.new_ID = new_ID;
    }

    public String getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    public String getNew_ID() {
        return new_ID;
    }

    public void setNew_ID(String new_ID) {
        this.new_ID = new_ID;
    }

}
