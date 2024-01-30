package com.example.to_let;

import java.util.List;

public class User {

    // name,status,imageUrl all are user information
    private String name;
    private String status;
    private String imageUrl;

    private double registerLatitude;
    private double registerLongitude;

    public double getRegisterLatitude() {
        return registerLatitude;
    }

    public void setRegisterLatitude(double registerLatitude) {
        this.registerLatitude = registerLatitude;
    }

    public double getRegisterLongitude() {
        return registerLongitude;
    }

    public void setRegisterLongitude(double registerLongitude) {
        this.registerLongitude = registerLongitude;
    }

    public User() {}

    public User(String name, String status, String imageUrl, double registerLatitude, double registerLongitude) {
        this.name = name;
        this.status = status;
        this.imageUrl = imageUrl;
        this.registerLatitude = registerLatitude;
        this.registerLongitude = registerLongitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
