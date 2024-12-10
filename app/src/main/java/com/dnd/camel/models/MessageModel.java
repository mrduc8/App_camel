package com.dnd.camel.models;

import java.sql.Timestamp;

public class MessageModel {
    private String id;
    private String sender_id;
    private String receiver_id;
    private String content;
    private Timestamp created_date;
    private boolean is_deleted_by_me;
    private boolean is_deleted_by_other;
    private boolean is_seen;

    public MessageModel(String id, String sender_id, String receiver_id, String content, Timestamp created_date, boolean is_deleted_by_me, boolean is_deleted_by_other, boolean is_seen) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content = content;
        this.created_date = created_date;
        this.is_deleted_by_me = is_deleted_by_me;
        this.is_deleted_by_other = is_deleted_by_other;
        this.is_seen = is_seen;
    }

    public MessageModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public boolean isIs_deleted_by_me() {
        return is_deleted_by_me;
    }

    public void setIs_deleted_by_me(boolean is_deleted_by_me) {
        this.is_deleted_by_me = is_deleted_by_me;
    }

    public boolean isIs_deleted_by_other() {
        return is_deleted_by_other;
    }

    public void setIs_deleted_by_other(boolean is_deleted_by_other) {
        this.is_deleted_by_other = is_deleted_by_other;
    }

    public boolean isIs_seen() {
        return is_seen;
    }

    public void setIs_seen(boolean is_seen) {
        this.is_seen = is_seen;
    }
}
