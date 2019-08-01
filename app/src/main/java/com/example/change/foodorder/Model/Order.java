package com.example.change.foodorder.Model;

public class Order {
    private String UserEmail;
    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String Image;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Order() {

    }

    public Order(String UserEmail, String productId, String productName, String quantity, String price, String discount,String image) {
        this.UserEmail = UserEmail;
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
    }



    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        this.UserEmail = userEmail;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
