package com.dnd.camel.models;

public class LikeCommentModel {
    private String user_id;
    private String comment_id;

    public LikeCommentModel(String user_id, String comment_id) {
        this.user_id = user_id;
        this.comment_id = comment_id;
    }

    public LikeCommentModel() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
