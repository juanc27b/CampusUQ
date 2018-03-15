package co.edu.uniquindio.campusuq.vo;

public class Email {
    private String _ID, name, from, to, date, content;

    public Email(String _ID, String name, String from, String to, String date, String content) {
        this._ID = _ID;
        this.name = name;
        this.from = from;
        this.to = to;
        this.date = date;
        this.content = content;
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

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
