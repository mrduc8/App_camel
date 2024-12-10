package com.dnd.camel.models;

public class ListMenuModel {
    private int img;
    private String title;

    public ListMenuModel(int img, String title) {
        this.img = img;
        this.title = title;
    }

    public ListMenuModel() {

    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
