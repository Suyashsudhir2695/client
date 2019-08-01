package com.example.change.foodorder.Model;

public class ComplaintsModel {
    private String id;
    private String email;
    private String title;
    private String about;
    private String body;

    public ComplaintsModel() {
    }

    public ComplaintsModel(String id, String email, String title, String about, String body) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.about = about;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
