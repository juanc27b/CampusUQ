package co.edu.uniquindio.campusuq.vo;

import java.math.BigInteger;

public class Email {

    private int _ID;
    private String name;
    private String from;
    private String to;
    private String date;
    private String snippet;
    private String content;
    private BigInteger historyID;

    public Email(int _ID, String name, String from, String to, String date, String snippet, String content, BigInteger historyID) {
        this._ID = _ID;
        this.name = name;
        this.from = from;
        this.to = to;
        this.date = date;
        this.snippet = snippet;
        this.content = content;
        this.historyID = historyID;
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

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
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