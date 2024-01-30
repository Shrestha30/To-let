package com.example.to_let;

import com.google.android.gms.maps.model.LatLng;

public class Post {
    //id is for multiple post and storing them given unique id
    // text is the post details under each id
    private String id;
    private String text;

    public Post() {}
    public Post(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
