package com.dnd.camel.models;

import com.google.firebase.Timestamp;

public class UserModel {
    private String id;
    private String first_name;
    private String last_name;
    private String avatar;
    private String email;
    private String password;
    private String address;
    private String SDT;
    private String birthday;
    private Timestamp created_date;
    private Timestamp modified_date;
    private String imgAvatar;
    private String imgBackground;

    public UserModel(String id, String first_name, String last_name, String email, String password, Timestamp created_date, Timestamp modified_date, String imgAvatar, String imgBackground, String address, String SDT, String birthday) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.created_date = created_date;
        this.modified_date = modified_date;
        this.imgAvatar = imgAvatar;
        this.imgBackground = imgBackground;
        this.address = address;
        this.SDT = SDT;
        this.birthday = birthday;
    }

    public UserModel() {

    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public Timestamp getModified_date() {
        return modified_date;
    }

    public void setModified_date(Timestamp modified_date) {
        this.modified_date = modified_date;
    }

    public String getImgAvatar() {
        return imgAvatar;
    }

    public void setImgAvatar(String imgAvatar) {
        this.imgAvatar = imgAvatar;
    }

    public String getImgBackground() {
        return imgBackground;
    }

    public void setImgBackground(String imgBackground) {
        this.imgBackground = imgBackground;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
