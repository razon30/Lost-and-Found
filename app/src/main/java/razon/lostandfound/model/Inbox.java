package razon.lostandfound.model;

/**
 * Created by HP on 02-Oct-17.
 */

public class Inbox {

    public Inbox(){}

    String name;
    String designation;
    String image;
    String username;

    public Inbox(String name, String username, String designation, String image) {
        this.name = name;
        this.username = username;
        this.designation = designation;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
