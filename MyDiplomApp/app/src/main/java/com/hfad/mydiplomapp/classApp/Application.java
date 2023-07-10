package com.hfad.mydiplomapp.classApp;

import android.media.Image;

public class Application {
    private String id;
    private String userId;
    private String lastName;
    private String name;
    private String sureName;
    private String data;
    private String gender;
    private String imageId;
    private int applicationStage;

    public Application(String id, String userId, String lastName, String name, String sureName, String data, String gender, String imageId, int applicationStage) {
        this.id = id;
        this.userId = userId;
        this.lastName = lastName;
        this.name = name;
        this.sureName = sureName;
        this.data = data;
        this.gender = gender;
        this.imageId = imageId;
        this.applicationStage = applicationStage;
    }

    public Application() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public int getApplicationStage() {
        return applicationStage;
    }

    public void setApplicationStage(int applicationStage) {
        this.applicationStage = applicationStage;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", sureName='" + sureName + '\'' +
                ", data='" + data + '\'' +
                ", gender='" + gender + '\'' +
                ", imageId='" + imageId + '\'' +
                ", applicationStage=" + applicationStage +
                '}';
    }
}
