package com.dnd.camel.models;

import java.util.HashMap;
import java.util.Map;

public class PostModel {
    private String id;
    private String title;
    private String content;
    private Map<String, Integer> user_likes = new HashMap<>();
    private Map<String, Integer> list_user_save = new HashMap<>();
    private boolean anonymous;
    private String postImg;
    private String user_id;
    private String flair_id;
    private Long created_date;
    private boolean is_deleted;

    public PostModel(String id, String title, String content, boolean anonymous, String user_id, String flair_id, Long created_date, boolean is_deleted) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.user_id = user_id;
        this.flair_id = flair_id;
        this.created_date = created_date;
        this.is_deleted = is_deleted;
    }

    public PostModel(String title, String content, boolean anonymous, String user_id, String flair_id, Long created_date) {
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.user_id = user_id;
        this.flair_id = flair_id;
        this.created_date = created_date;
    }

    public Map<String, Integer> getUser_likes() {
        return user_likes;
    }

    public void setUser_likes(Map<String, Integer> user_likes) {
        this.user_likes = user_likes;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public PostModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFlair_id() {
        return flair_id;
    }

    public void setFlair_id(String flair_id) {
        this.flair_id = flair_id;
    }

    public Long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Long created_date) {
        this.created_date = created_date;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Map<String, Integer> getList_user_save() {
        return list_user_save;
    }

    public void setList_user_save(Map<String, Integer> list_user_save) {
        this.list_user_save = list_user_save;
    }
}
