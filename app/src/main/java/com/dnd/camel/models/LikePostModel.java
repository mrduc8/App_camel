package com.dnd.camel.models;

public class LikePostModel {
    private String user_id;
    private String post_id;

    public LikePostModel(String user_id, String post_id) {
        this.user_id = user_id;
        this.post_id = post_id;
    }

    public LikePostModel() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
