package razon.lostandfound.model;

/**
 * Created by HP on 18-Sep-17.
 */

public class Notification {

    public Notification(){};

    String itemID;
    String status;
    String postedBy;
    String postDate;
    String commentedBy;


    public Notification(String commentedBy, String itemID, String status, String postedBy,String postDate) {
        this.commentedBy = commentedBy;
        this.itemID = itemID;
        this.postedBy = postedBy;
        this.status = status;
        this.postDate = postDate;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }


}
