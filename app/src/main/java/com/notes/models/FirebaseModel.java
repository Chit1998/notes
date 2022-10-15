package com.notes.models;

public class FirebaseModel {
    private String title;
    private String content;

    public FirebaseModel(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public FirebaseModel() {
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
}
