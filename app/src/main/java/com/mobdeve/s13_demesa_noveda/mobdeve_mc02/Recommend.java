package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import java.io.Serializable;
import java.util.Date;

public class Recommend implements Serializable {
    private String reviewMovieName;
    private String recommendContent;
    private String recommendPublished;
    private String reviewAuthor;


    public Recommend(String reviewMovieName, String recommendContent, String recommendPublished, String reviewAuthor) {
        this.reviewMovieName = reviewMovieName;
        this.recommendContent = recommendContent;
        this.recommendPublished = recommendPublished;
        this.reviewAuthor = reviewAuthor;
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
