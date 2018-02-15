package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 7/02/2018.
 */

public class Item {

    public int background;
    public int image;
    public int title;
    public int description;

    public Item(int background, int image, int title, int description) {
        this.background = background;
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

}
