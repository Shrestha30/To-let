/*
Class to Update and retrieve information of house in database
House and To-let is interchangeably used throughout the project
 */
package com.example.to_let;

public class House {
    private String Detail;
    private String Name;
    private String StreetAddress;

    private double Lat;
    private double Lng;

    private int IsVerified=0,Rent=0,Bedrooms=0,Floor=0,Bathrooms=0,Size=0;

    public House(){}

    public House(String detail, String name, String streetAddress, double lat, double lng, int isVerified, int rent, int bedrooms, int floor, int bathrooms, int size) {
        Detail = detail;
        Name = name;
        StreetAddress = streetAddress;
        Lat = lat;
        Lng = lng;
        IsVerified = isVerified;
        Rent = rent;
        Bedrooms = bedrooms;
        Floor = floor;
        Bathrooms = bathrooms;
        Size = size;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStreetAddress() {
        return StreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        StreetAddress = streetAddress;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public int getIsVerified() {
        return IsVerified;
    }

    public void setIsVerified(int isVerified) {
        IsVerified = isVerified;
    }

    public int getRent() {
        return Rent;
    }

    public void setRent(int rent) {
        Rent = rent;
    }

    public int getBedrooms() {
        return Bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        Bedrooms = bedrooms;
    }

    public int getFloor() {
        return Floor;
    }

    public void setFloor(int floor) {
        Floor = floor;
    }

    public int getBathrooms() {
        return Bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        Bathrooms = bathrooms;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }
}
