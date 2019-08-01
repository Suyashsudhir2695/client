package com.example.change.foodorder.Model;

public class User {
    private String name;
    private String password;
    private String phone;
    private String isStaff;
    private String email;
    private String address;
    private String balance;
    private String twoStep;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;


    private String secureCode;

    public User() {
    }

    public User(String name, String password, String phone, String secureCode,String image,String balance,String twoStep) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.secureCode = secureCode;
        this.isStaff = "false";
        this.image = image;
        this.balance = balance;
        this.twoStep = twoStep;
    }

    public String getTwoStep() {
        return twoStep;
    }

    public void setTwoStep(String twoStep) {
        this.twoStep = twoStep;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }
}