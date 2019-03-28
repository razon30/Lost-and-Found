package razon.lostandfound.model;

/**
 * Created by HP on 07-Oct-17.
 */

public class UserList {

    public UserList(){};

    UserGeneralInfo userGeneralInfo;
    String image;

    public UserList(UserGeneralInfo userGeneralInfo, String image) {
        this.userGeneralInfo = userGeneralInfo;
        this.image = image;
    }

    public UserGeneralInfo getUserGeneralInfo() {
        return userGeneralInfo;
    }

    public void setUserGeneralInfo(UserGeneralInfo userGeneralInfo) {
        this.userGeneralInfo = userGeneralInfo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
