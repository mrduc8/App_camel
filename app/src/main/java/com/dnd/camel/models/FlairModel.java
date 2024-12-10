package com.dnd.camel.models;

import androidx.annotation.NonNull;

public class FlairModel {
    private String id;
    private String name;

    public FlairModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public FlairModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}