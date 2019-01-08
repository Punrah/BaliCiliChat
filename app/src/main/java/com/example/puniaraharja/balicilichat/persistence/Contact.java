package com.example.puniaraharja.balicilichat.persistence;

/**
 * Created by Startup on 5/30/17.
 */

public class Contact {

    public String id;
    public String name;
    public String image;
    private String role;

    public Contact()
    {}

    public Contact(String id, String name, String image, String role) {
        this.id=id;
        this.name=name;
        this.image=image;
        this.role=role;
    }



    public String getUid() {
        return id;
    }

    public void setUid(String uid) {
        this.id = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}