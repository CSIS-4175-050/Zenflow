package com.mehla.zenflow.model;

import com.google.gson.annotations.SerializedName;

public class Exercise {
    @SerializedName("sanskrit_name")
    private String sanskritName;

    @SerializedName("english_name")
    private String englishName;

    private String description;
    private String time;
    private String image;

    // getters and setters

    public String getSanskritName() {
        return sanskritName;
    }

    public void setSanskritName(String sanskritName) {
        this.sanskritName = sanskritName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}