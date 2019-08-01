package com.example.change.foodorder.Model;

public class Category {
    private String Name;
    private String Image;
    private String Desc;

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public Category() {
    }

    public Category(String name, String image,String desc) {
        Name = name;
        Image = image;
        Desc = desc;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
