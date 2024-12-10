package com.dnd.camel.models;

public class ListMessModel {
    private String img;
    private String userName;
    private String mess;
    private String id;

    public ListMessModel() {

    }

    public ListMessModel(String img, String userName, String mess, String id) {
        this.img = img;
        this.userName = userName;
        this.mess = mess;
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}