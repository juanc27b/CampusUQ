package co.edu.uniquindio.campusuq.vo;

public class Dish {
    private String _ID, name, description, price, image;

    public Dish(String _ID, String name, String description, String price, String image) {
        this._ID = _ID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
