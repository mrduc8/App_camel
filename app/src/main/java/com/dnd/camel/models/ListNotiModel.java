package com.dnd.camel.models;

public class ListNotiModel {
    private String id;
    private String img;
    private String content;
    private String postId;
    private boolean is_click;

    public ListNotiModel(String img, String content, String postId, boolean is_click, String id) {
        this.img = img;
        this.content = content;
        this.postId = postId;
        this.is_click = is_click;
        this.id = id;
    }

    public ListNotiModel() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isIs_click() {
        return is_click;
    }

    public void setIs_click(boolean is_click) {
        this.is_click = is_click;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
