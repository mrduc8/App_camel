package com.dnd.camel.models;

public class UserPostModel {
    private String avatar;
    private String userName;
    private String postTitle;
    private String postImg;
    int likes;

    public UserPostModel(String img, String userName, String postTitle, String postImg, int likes) {
        this.avatar = img;
        this.userName = userName;
        this.postTitle = postTitle;
        this.postImg = postImg;
        this.likes = likes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public UserPostModel(String img, String userName) {
        this.avatar = img;
        this.userName = userName;
    }

    public UserPostModel() {
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
