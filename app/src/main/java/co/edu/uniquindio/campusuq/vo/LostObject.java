package co.edu.uniquindio.campusuq.vo;

public class LostObject {
    private String _ID, user_lost_ID, name, place, date, description, image, user_found_ID;

    public LostObject(String _ID, String user_lost_ID, String name, String place, String date, String description, String image, String user_found_ID) {
        this._ID = _ID;
        this.user_lost_ID = user_lost_ID;
        this.name = name;
        this.place = place;
        this.date = date;
        this.description = description;
        this.image = image;
        this.user_found_ID = user_found_ID;
    }

    public String get_ID() {
        return _ID;
    }
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getUserLost_ID() {
        return user_lost_ID;
    }
    public void setUserLost_ID(String user_lost_ID) {
        this.user_lost_ID = user_lost_ID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getUserFound_ID() {
        return user_found_ID;
    }
    public void setUserFound_ID(String user_found_ID) {
        this.user_found_ID = user_found_ID;
    }
}
