package razon.lostandfound.model;

import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by HP on 17-Sep-17.
 */

public class UserGeneralInfo {

    public String name;
    public String username;
    public String nsuId;
    public String email;
    public String pass;
    public String designation;
    public String phone;
    public String catagory;

    public UserGeneralInfo(){};

    public UserGeneralInfo(String name, String username, String nsuId, String email, String pass,
                           String designation, String phone, String catagory) {
        this.name = name;
        this.username = username;
        this.nsuId = nsuId;
        this.email = email;
        this.pass = pass;
        this.designation = designation;
        this.phone = phone;
        this.catagory = catagory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNsuId() {
        return nsuId;
    }

    public void setNsuId(String nsuId) {
        this.nsuId = nsuId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }
}
