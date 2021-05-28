package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieInfoActivity extends AppCompatActivity {

    private Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
    }

    private void getData() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://movie-database-imdb-alternative.p.rapidapi.com/?i="+ movie.getMovieID() +"&r=json")
                .get()
                .addHeader("x-rapidapi-key", "3046c12cfdmsh5c0c9ec58a780edp111336jsnccb6948110d5")
                .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
    }
}