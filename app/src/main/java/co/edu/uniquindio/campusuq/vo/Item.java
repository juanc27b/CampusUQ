package co.edu.uniquindio.campusuq.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Juan Camilo on 7/02/2018.
 */

public class Item implements Parcelable {

    private int background;
    private int image;
    private String title;
    private String description;

    public Item(int background, int image, String title, String description) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(background);
        dest.writeInt(image);
        dest.writeString(title);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public Item(Parcel in) {
        background = in.readInt();
        image = in.readInt();
        title = in.readString();
        description = in.readString();
    }

}
