package com.example.whywasteapp.home;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class FoodModel {
    String UserKey,DonatorNode,FoodName,FoodImage,DonatorName,DonationTime,FoodQty;

    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }

    public String getDonatorNode() {
        return DonatorNode;
    }

    public void setDonatorNode(String donatorNode) {
        DonatorNode = donatorNode;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodImage() {
        return FoodImage;
    }

    public void setFoodImage(String foodImage) {
        FoodImage = foodImage;
    }

    public String getDonatorName() {
        return DonatorName;
    }

    public void setDonatorName(String donatorName) {
        DonatorName = donatorName;
    }

    public String getDonationTime() {
        return DonationTime;
    }

    public void setDonationTime(String donationTime) {
        DonationTime = donationTime;
    }
}