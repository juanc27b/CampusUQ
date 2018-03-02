package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class CalendarDetailItem {

    private String period;
    private String start;
    private String end;

    public CalendarDetailItem(String period, String start, String end) {
        this.period = period;
        this.start = start;
        this.end = end;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
