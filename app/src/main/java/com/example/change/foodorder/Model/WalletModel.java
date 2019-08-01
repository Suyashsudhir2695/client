package com.example.change.foodorder.Model;

public class WalletModel {
    private String email;
    private String id;
    private String time;
    private String type;

    public WalletModel() {
    }

    public WalletModel( String email,String time, String type) {
        this.email = email;
        this.time = time;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
