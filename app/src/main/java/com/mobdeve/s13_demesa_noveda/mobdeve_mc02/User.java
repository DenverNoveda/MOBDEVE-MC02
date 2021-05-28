package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String email;
    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<Recommend> recommends = new ArrayList<>();

    // Constructors
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters
    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getEmail() { return email; }

    // Setters / Adders
    public void setPassword(String password) {
        this.password = password;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void addRecommend(Recommend recommend) {
        this.recommends.add(recommend);
    }
}
