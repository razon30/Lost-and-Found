package razon.lostandfound.model;

/**
 * Created by HP on 18-Sep-17.
 */

public class Notification {

    String itemID;
    String status;
    String noti;

    public Notification(String itemID, String status, String noti) {
        this.itemID = itemID;
        this.status = status;
        this.noti = noti;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNoti() {
        return noti;
    }

    public void setNoti(String noti) {
        this.noti = noti;
    }
}
