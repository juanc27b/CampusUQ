package co.edu.uniquindio.campusuq.vo;

import java.math.BigInteger;

public class Email {

    private String _ID;
    private String name;
    private String from;
    private String to;
    private String date;
    private String content;
    private BigInteger historyID;

    public Email(String _ID, String name, String from, String to, String date, String content, BigInteger historyID) {
        this._ID = _ID;
        this.name = name;
        this.from = from;
        this.to = to;
        this.date = date;
        this.content = content;
        this.historyID = historyID;
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

    public BigInteger getHistoryID() {
        return historyID;
    }

    public void setHistoryID(BigInteger historyID) {
        this.historyID = historyID;
    }

}
