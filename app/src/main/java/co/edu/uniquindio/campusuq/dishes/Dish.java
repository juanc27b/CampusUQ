package co.edu.uniquindio.campusuq.dishes;

public class Dish {
    private int _ID;
    private String name;
    private String description;
    private int price;
    private String image;

    Dish(int _ID, String name, String description, int price, String image) {
        this._ID = _ID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
