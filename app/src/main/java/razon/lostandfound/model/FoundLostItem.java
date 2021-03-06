package razon.lostandfound.model;

import android.support.annotation.NonNull;

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
    String time;

    String proPic;
    String enable;

    public FoundLostItem(String id, String username, String name, String caption,
                         String image, String time, String proPic, String enable) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.caption = caption;
        this.image = image;
        this.time = time;
        this.proPic = proPic;
        this.enable = enable;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getProPic() {
        return proPic;
    }

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
