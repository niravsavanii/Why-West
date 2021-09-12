package com.example.whywasteapp.profile;

public class DonateModel {

    String foodName, imageName, foodQty, date;
    public DonateModel(){}

    public DonateModel(String foodName, String imageName, String foodQty, String date) {
        this.foodName = foodName;
        this.imageName = imageName;
        this.foodQty = foodQty;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodQty() {
        return foodQty;
    }

    public void setFoodQty(String foodQty) {
        this.foodQty = foodQty;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

/*public class DonateModel {

    public DonateModel(){}

    String FoodName,DonatorName,FoodImg,Date,Time;

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getDonatorName() {
        return DonatorName;
    }

    public void setDonatorName(String donatorName) {
        DonatorName = donatorName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFoodImg() {
        return FoodImg;
    }

    public void setFoodImg(String foodImg) {
        FoodImg = foodImg;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}*/
