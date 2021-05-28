package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import java.util.ArrayList;
import java.util.Date;

public class Movie {
    private String movieName;
    private String link;
    private String image;
    private String movieID;
    private ArrayList<String> cast;
    private Date releaseDate;
    private String summary;
    private ArrayList<Review> movieReviews;

    public String getMovieName() {
        return movieName;
    }
    public String getMovieID(){return this.movieID;}
    public ArrayList<String> getCast() {
        return cast;
    }
    public Date getReleaseDate() {
        return releaseDate;
    }
    public String getSummary() {
        return summary;
    }
    public ArrayList<Review> getMovieReviews() {
        return movieReviews;
    }
    public String getLink(){
        return this.link;
    }
    public String getImage(){
        return this.image;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setMovieID(String id){this.movieID = id;}
}
