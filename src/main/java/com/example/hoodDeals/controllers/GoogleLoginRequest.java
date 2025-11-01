package com.example.hoodDeals.controllers;

public class GoogleLoginRequest {
    private String googleId;
    private String email;
    private String name;
    private String picture;

    // Getters and Setters
    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
}
