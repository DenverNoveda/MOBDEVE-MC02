package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import java.io.Serializable;

public class Review implements Serializable {
    private String reviewMovieName;
    private String reviewContent;
    private String reviewPublished;
    private String reviewAuthor;
    private String rating;
    private int likes;
    private int dislikes;

    public Review(String reviewMovieName, String reviewContent, String reviewPublished, String reviewAuthor, String rating, int likes, int dislikes) {
        this.reviewMovieName = reviewMovieName;
        this.reviewContent = reviewContent;
        this.reviewPublished = reviewPublished;
        this.reviewAuthor = reviewAuthor;
        this.rating = rating;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Review(String reviewMovieName, String reviewContent, String reviewPublished, String reviewAuthor, String rating) {
        this.reviewMovieName = reviewMovieName;
        this.reviewContent = reviewContent;
        this.reviewPublished = reviewPublished;
        this.reviewAuthor = reviewAuthor;
        this.rating = rating;
    }

    public String getReviewMovieName() {
        return reviewMovieName;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewPublished() {
        return reviewPublished;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getRating() {
        return rating;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }
}
