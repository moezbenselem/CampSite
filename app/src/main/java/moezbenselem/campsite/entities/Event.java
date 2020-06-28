package moezbenselem.campsite.entities;

/**
 * Created by Moez on 30/01/2019.
 */

public class Event {

    String id, location, date, topic, name, time, admin;
    Double lon, lat;


    public Event() {
    }

    public Event(String id, String location, String date, String topic, String name, String time, String admin, Double lon, Double lat) {
        this.id = id;
        this.location = location;
        this.date = date;
        this.topic = topic;
        this.name = name;
        this.time = time;
        this.admin = admin;
        this.lon = lon;
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }


}
