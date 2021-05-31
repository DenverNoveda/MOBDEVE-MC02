package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import java.io.Serializable;
import java.util.Date;

public class Recommend implements Serializable {
    private String reviewMovieName;
    private String recommendContent;
    private String recommendPublished;
    private String reviewAuthor;
    private long likes;
    private long dislikes;


    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public Recommend(String reviewMovieName, String recommendContent, String recommendPublished, String reviewAuthor, long likes, long dislikes) {
        this.reviewMovieName = reviewMovieName;
        this.recommendContent = recommendContent;
        this.recommendPublished = recommendPublished;
        this.reviewAuthor = reviewAuthor;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getReviewMovieName() {
        return reviewMovieName;
    }

    public String getRecommendContent() {
        return recommendContent;
    }

    public String getRecommendPublished() {
        return recommendPublished;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }
}
