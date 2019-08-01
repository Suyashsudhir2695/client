package com.example.change.foodorder.Model;

public class Favorites {
    private String FoodId, FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDiscount,FoodDesc,UserEmail;


    public Favorites() {
    }

    public Favorites(String foodId, String userEmail, String foodName, String foodPrice, String foodMenuId, String foodImage, String foodDiscount, String foodDesc) {
        FoodId = foodId;
        UserEmail = userEmail;
        FoodName = foodName;
        FoodPrice = foodPrice;
        FoodMenuId = foodMenuId;
        FoodImage = foodImage;
        FoodDiscount = foodDiscount;
        FoodDesc = foodDesc;

    }

    public String getFoodImage() {
        return FoodImage;
    }

    public void setFoodImage(String foodImage) {
        FoodImage = foodImage;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodPrice() {
        return FoodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        FoodPrice = foodPrice;
    }

    public String getFoodMenuId() {
        return FoodMenuId;
    }

    public void setFoodMenuId(String foodMenuId) {
        FoodMenuId = foodMenuId;
    }

    public String getFoodDiscount() {
        return FoodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        FoodDiscount = foodDiscount;
    }

    public String getFoodDesc() {
        return FoodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        FoodDesc = foodDesc;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }
}
