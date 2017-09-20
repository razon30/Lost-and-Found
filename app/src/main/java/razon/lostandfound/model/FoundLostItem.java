package razon.lostandfound.model;

/**
 * Created by HP on 18-Sep-17.
 */

public class FoundLostItem {

    public FoundLostItem(){};

    String id;
    String username;
    String name;
    String caption;
    String image;

    public FoundLostItem(String id, String username, String name, String caption, String image) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.caption = caption;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
