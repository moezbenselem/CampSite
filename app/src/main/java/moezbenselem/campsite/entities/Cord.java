package moezbenselem.campsite.entities;

/**
 * Created by Moez on 12/10/2018.
 */

public class Cord {

    Double lon, lat;
    long time;

    public Cord() {
    }

    public Cord(Double lon, Double lat, long time) {
        this.lon = lon;
        this.lat = lat;
        this.time = time;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
