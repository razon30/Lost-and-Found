package razon.lostandfound.model;

/**
 * Created by HP on 30-Sep-17.
 */

public class Message {

    public Message(){};

    String body;
    String image;
    String senderName;
    String time;

    public Message(String body, String image, String senderName, String time) {
        this.body = body;
        this.image = image;
        this.senderName = senderName;
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
