package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class New {

    private String _ID;
    private String name;
    private String link;
    private String image;
    private String summary;
    private String content;
    private String date;
    private String author;

    public New(String _ID, String name, String link, String image, String summary, String content, String date, String author) {
        this._ID = _ID;
        this.name = name;
        this.link = link;
        this.image = image;
        this.summary = summary;
        this.content = content;
        this.date = date;
        this.author = author;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
