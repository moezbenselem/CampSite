package moezbenselem.campsite;

/**
 * Created by Moez on 03/02/2019.
 */

public class Notification {

    String from, type, time;


    public Notification() {
    }

    public Notification(String from, String type, String time) {
        this.from = from;
        this.type = type;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
