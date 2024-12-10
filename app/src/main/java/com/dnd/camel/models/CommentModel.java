package com.dnd.camel.models;

import java.util.Map;

public class CommentModel {
    private String id;
    private String content;
    private Map<String, Boolean> likes;
    private Map<String, CommentModel> replies;
    private String parent_id;
    private String user_id;
    private String post_id;
    private String image_link;
    private Long created_date;
    private boolean is_deleted;
    private Long modified_date;

    public CommentModel(String id, String content, String parent_id, String user_id, String post_id,
                        String image_link, Long created_date, boolean is_deleted, Long modified_date) {
        this.id = id;
        this.content = content;
        this.parent_id = parent_id;
        this.user_id = user_id;
        this.post_id = post_id;
        this.image_link = image_link;
        this.created_date = created_date;
        this.is_deleted = is_deleted;
        this.modified_date = modified_date;
    }

    public CommentModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, CommentModel> getReplies() {
        return replies;
    }

    public void setReplies(Map<String, CommentModel> replies) {
        this.replies = replies;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
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

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
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

    public Long getModified_date() {
        return modified_date;
    }

    public void setModified_date(Long modified_date) {
        this.modified_date = modified_date;
    }
}
