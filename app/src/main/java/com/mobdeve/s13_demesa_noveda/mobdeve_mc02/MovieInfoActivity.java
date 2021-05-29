package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieInfoActivity extends AppCompatActivity {

    private Movie movie;
    private String movieID;

    private String movieName;
    private String cast;
    private String plot;
    private String poster;
    private Handler mHandler;

    private ImageView iv_movieInfoMain;
    private TextView tv_movieInfoName;
    private TextView tv_movieInfoCast;
    private TextView tv_movieInfoDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Intent i = getIntent();
        movieID = i.getStringExtra("MOVIE_ID");

        this.iv_movieInfoMain = findViewById(R.id.iv_movieInfoMain);
        this.tv_movieInfoName = findViewById(R.id.tv_movieInfoName);
        this.tv_movieInfoCast = findViewById(R.id.tv_movieInfoCast);
        this.tv_movieInfoDesc = findViewById(R.id.tv_movieInfoDesc);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://movie-database-imdb-alternative.p.rapidapi.com/?i=" + movieID + "&r=json")
                        .get()
                        .addHeader("x-rapidapi-key", "3046c12cfdmsh5c0c9ec58a780edp111336jsnccb6948110d5")
                        .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String test = response.body().string();
                    JSONObject obj = new JSONObject(test);

                    movieName = (String) obj.get("Title");
                    cast = (String) obj.get("Actors");
                    plot = (String) obj.get("Plot");
                    poster = (String) obj.get("Poster");

                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load(poster).into(iv_movieInfoMain);
                            iv_movieInfoMain.setImageAlpha(255);
                            tv_movieInfoName.setText(movieName);
                            tv_movieInfoCast.setText(cast);
                            tv_movieInfoDesc.setText(plot);
                        }
                    });



                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}