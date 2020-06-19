package moezbenselem.campsite.entities;

/**
 * Created by Moez on 05/08/2018.
 */

public class Conv {

    private boolean seen;
    private long timestamp;
    private String userImage, partner, partnerGender;
    private String message, type, from;
    private long time;


    public Conv() {
    }

    public Conv(boolean seen, long timestamp, String userImage, String partner, String partnerGender, String message, String type, String from, long time) {

        this.seen = seen;
        this.timestamp = timestamp;
        this.userImage = userImage;
        this.partner = partner;
        this.partnerGender = partnerGender;
        this.message = message;
        this.type = type;
        this.from = from;
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPartnerGender() {
        return partnerGender;
    }

    public void setPartnerGender(String partnerGender) {
        this.partnerGender = partnerGender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
