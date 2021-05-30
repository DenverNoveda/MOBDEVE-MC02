package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import java.io.Serializable;

public class Favorite implements Serializable {

    private String username;
    private String favoriteMovieName;

    public Favorite(String username, String favoriteMovieName) {
        this.username = username;
        this.favoriteMovieName = favoriteMovieName;
    }

    public String getFavoriteMovieName() {
        return favoriteMovieName;
    }

    public String getUsername() {
        return username;
    }
}
