package com.example.to_let;

     //Timeline data model
public class TimeLineDataModel {

    int image;
    String name;
    String Post_details;

    public TimeLineDataModel(int image, String name, String post_details) {
        this.image = image;
        this.name = name;
        Post_details = post_details;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost_details() {
        return Post_details;
    }

    public void setPost_details(String post_details) {
        Post_details = post_details;
    }
}
