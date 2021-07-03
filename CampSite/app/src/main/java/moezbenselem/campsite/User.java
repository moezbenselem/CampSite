package moezbenselem.campsite;

/**
 * Created by Moez on 30/01/2019.
 */

public class User {

    String device_token, email, gender, image, status, thumb_image, username;

    public User() {
    }


    public User(String device_token, String email, String gender, String image, String status, String thumb_image, String username) {
        this.device_token = device_token;
        this.email = email;
        this.gender = gender;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
        this.username = username;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
