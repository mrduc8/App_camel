package com.dnd.camel.models;

public class PostImageModel {
    private String id;
    private String post_id;
    private String image_link;

    public PostImageModel(String id, String post_id, String image_link) {
        this.id = id;
        this.post_id = post_id;
        this.image_link = image_link;
    }

    public PostImageModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }
}
