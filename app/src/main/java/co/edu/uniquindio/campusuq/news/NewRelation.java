package co.edu.uniquindio.campusuq.news;

public class NewRelation {

    private String category_ID;
    private String new_ID;

    NewRelation(String category_ID, String new_ID) {
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
