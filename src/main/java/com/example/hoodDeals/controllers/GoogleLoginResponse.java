package com.example.hoodDeals.controllers;

import com.example.hoodDeals.entities.AppUser;

public class GoogleLoginResponse {
    private String token;
    private AppUser user;

    public GoogleLoginResponse(String token, AppUser user) {
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
}
