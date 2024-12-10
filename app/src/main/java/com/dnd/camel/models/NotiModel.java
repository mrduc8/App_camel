package com.dnd.camel.models;

public class NotiModel {
    private String PostId;
    private String UserLikeId;
    private String UserPostId;
    private String ImgAvatar;
    private String Content;
    private boolean is_active;
    private boolean is_seen;
    private boolean is_click;

    public NotiModel(String postId, String userLikeId, String userPostId, String imgAvatar, String content, boolean is_active, boolean is_seen, boolean is_click) {
        PostId = postId;
        UserLikeId = userLikeId;
        UserPostId = userPostId;
        ImgAvatar = imgAvatar;
        Content = content;
        this.is_active = is_active;
        this.is_seen = is_seen;
        this.is_click = is_click;
    }

    public NotiModel() {
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getUserLikeId() {
        return UserLikeId;
    }

    public void setUserLikeId(String userLikeId) {
        UserLikeId = userLikeId;
    }

    public String getUserPostId() {
        return UserPostId;
    }

    public void setUserPostId(String userPostId) {
        UserPostId = userPostId;
    }

    public String getImgAvatar() {
        return ImgAvatar;
    }

    public void setImgAvatar(String imgAvatar) {
        ImgAvatar = imgAvatar;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public boolean isIs_seen() {
        return is_seen;
    }

    public void setIs_seen(boolean is_seen) {
        this.is_seen = is_seen;
    }

    public boolean isIs_click() {
        return is_click;
    }

    public void setIs_click(boolean is_click) {
        this.is_click = is_click;
    }
}
