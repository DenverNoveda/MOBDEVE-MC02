package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainMenuActivity extends AppCompatActivity {

    private String addressPop = "";
    private String addressComingSoon = "";
    private int popListSize, soonListSize;
    private Elements popMoviesListAlpha, popNamesListAlpha, popLinksListAlpha;
    private Elements soonMoviesListAlpha, soonNamesListAlpha, soonLinksListAlpha;
    private List<String> popNamesList, popLinksList, popImgList, popMovieIDList;
    private List<String> soonNamesList, soonLinksList, soonImgList,soonMovieIDList;
    private List<Document> docList;
    private Document doc;
    private ArrayList<Movie> resultsPopular;
    private ArrayList<Movie> resultsComingSoon;
    private Handler mHandler;
    private RecyclerView popularRecyclerView;
    private RecyclerView comingSoonRecyclerView;
    private RecyclerView.Adapter popAdapter;
    private LinearLayoutManager popManager;
    private RecyclerView.Adapter soonAdapter;
    private LinearLayoutManager soonManager;

    private TextView tv_seeMorePopular;
    private TextView tv_seeMoreComingSoon;
    private TextView tv_searchByGenre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        docList = new ArrayList<>();
        popNamesList = new ArrayList<>();
        popLinksList = new ArrayList<>();
        popImgList = new ArrayList<>();
        soonImgList = new ArrayList<>();
        soonLinksList = new ArrayList<>();
        soonNamesList = new ArrayList<>();

        resultsPopular = new ArrayList<>();
        resultsComingSoon = new ArrayList<>();
        loadMostPopular();
        loadComingSoon();

        setUpPopularRecyclerView();

        this.tv_seeMorePopular = findViewById(R.id.tv_seeMorePopMovies);
        this.tv_seeMoreComingSoon = findViewById(R.id.tv_seeMoreComingSoonMovies);
        this.tv_searchByGenre = findViewById(R.id.tv_searchByGenre);
        Log.d("SETUP", "OnCreate Done");


        this.tv_searchByGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, ResultsActivity.class);
                startActivity(i);
            }
        });
        this.tv_seeMorePopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, ResultsActivity.class);
                i.setAction("LOAD_MORE_POPULAR");
                startActivity(i);
            }
        });
        this.tv_seeMoreComingSoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, ResultsActivity.class);
                i.setAction("LOAD_MORE_COMING_SOON");
                startActivity(i);
            }
        });
    }

    private void loadMostPopular() {
        addressPop = "https://www.imdb.com/chart/moviemeter/?ref_=nv_mv_mpm";
        getDataMostPopular();
    }

    private void loadComingSoon(){
        addressComingSoon = "https://www.imdb.com/movies-coming-soon/?ref_=nv_mv_cs";
        getDataComingSoon();
    }
    private void fillPopularArray(){
        for(int i = 0; i< popNamesList.size(); i++){
            Movie movie = new Movie();
            Log.d("Name", popNamesList.get(i));
            Log.d("Link", popLinksList.get(i));
            Log.d("Img", popImgList.get(i));
            movie.setMovieName(popNamesList.get(i));
            movie.setImage(popImgList.get(i));
            movie.setLink(popLinksList.get(i));
            movie.setMovieID(popMovieIDList.get(i));
            resultsPopular.add(movie);
        }
    }
    private void fillComingSoonArray(){
        for(int i = 0; i < soonNamesList.size(); i++){
            Movie movie = new Movie();
            Log.d("Name", soonNamesList.get(i));
            Log.d("Link", soonLinksList.get(i));
            movie.setMovieName(soonNamesList.get(i));
            movie.setImage(soonImgList.get(i));
            movie.setLink(soonLinksList.get(i));
            movie.setMovieID(soonMovieIDList.get(i));

            resultsComingSoon.add(movie);
        }
    }
    private void getDataMostPopular() {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(addressPop).get();
                    popMoviesListAlpha = doc.select("td.posterColumn");
                    //Get list size
                    popListSize = popMoviesListAlpha.size();
                    //Get names elements
                    popNamesListAlpha = popMoviesListAlpha.select("img");
                    popNamesList = popNamesListAlpha.eachAttr("alt");
                    popImgList = popNamesListAlpha.eachAttr("src");
                    //Get first 10 elements;
                    if(popListSize>10){
                        popNamesList = trimSelection(popNamesList);
                    }
                    //Get links elements
                    popLinksListAlpha = doc.select("td.posterColumn");
                    popLinksListAlpha = popLinksListAlpha.select("a");
                    popLinksList = popLinksListAlpha.eachAttr("href");
                    //Get first 10 elements;
                    if(popListSize>10){
                        popLinksList = trimSelection(popLinksList);
                        popListSize = 10;
                    }
                    //Fix link elements
                    popMovieIDList = getMovieID(popLinksList);
                    popLinksList = fixLinkList(popLinksList);

                    //API call for posters
//                    for(int i = 0; i < popListSize; i++){
//                        OkHttpClient client = new OkHttpClient();
//
//                        Request request = new Request.Builder()
//                                .url("https://movie-database-imdb-alternative.p.rapidapi.com/?i=" + popMovieIDList.get(i) + "&r=json")
//                                .get()
//                                .addHeader("x-rapidapi-key", "3046c12cfdmsh5c0c9ec58a780edp111336jsnccb6948110d5")
//                                .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
//                                .build();
//                        try {
//                            Response response = client.newCall(request).execute();
//                            String test = response.body().string();
//                            JSONObject obj = new JSONObject(test);
//
//                            popImgList.add((String) obj.get("Poster"));
//
//                        }catch (IOException | JSONException e){
//                            e.printStackTrace();
//                        }
//                    }
                    Log.v("End", "End of run");

                   fillPopularArray();
                   mHandler = new Handler(Looper.getMainLooper());
                   mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            popAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.v("getData", "Finished ");


                } catch (IOException e) {
                }
            }
        });
        thread.start();
    }


    private void getDataComingSoon(){
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(addressComingSoon).get();
                    soonMoviesListAlpha = doc.select("div.image");
                    //Get list size
                    soonListSize = soonMoviesListAlpha.size();
                    //Get names elements
                    soonNamesListAlpha = soonMoviesListAlpha.select("img");
                    soonNamesList = soonNamesListAlpha.eachAttr("title");
                    soonImgList =  soonNamesListAlpha.eachAttr("src");
                    //Get first 10 elements;
                    if(soonListSize>10){
                        soonNamesList = trimSelection(soonNamesList);
                    }
                    //Get links elements
                    soonLinksListAlpha = doc.select("div.image");
                    soonLinksListAlpha = soonLinksListAlpha.select("a");
                    soonLinksList = soonLinksListAlpha.eachAttr("href");
                    //Get first 10 elements;
                    if(soonListSize>10){
                        soonLinksList = trimSelection(soonLinksList);
                        soonListSize = 10;
                    }
                    //Fix link elements
                    soonMovieIDList = getMovieID(soonLinksList);
                    soonLinksList = fixLinkList(soonLinksList);
                    //API call for posters
//                    for(int i = 0; i < soonListSize; i++){
//                        OkHttpClient client = new OkHttpClient();
//
//                        Request request = new Request.Builder()
//                                .url("https://movie-database-imdb-alternative.p.rapidapi.com/?i=" + soonMovieIDList.get(i) + "&r=json")
//                                .get()
//                                .addHeader("x-rapidapi-key", "3046c12cfdmsh5c0c9ec58a780edp111336jsnccb6948110d5")
//                                .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
//                                .build();
//                        try {
//                            Response response = client.newCall(request).execute();
//                            String test = response.body().string();
//                            JSONObject obj = new JSONObject(test);
//
//                            soonImgList.add((String) obj.get("Poster"));
//
//                        }catch (IOException | JSONException e){
//                            e.printStackTrace();
//                        }
//                    }

                    Log.v("End", "End of run");



                    fillComingSoonArray();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            soonAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.v("getData", "Finished ");

                } catch (IOException e) {
                }
            }
        });
        thread.start();
    }
    public List<String> trimSelection(List<String> list){
        Log.v("getFirstFifty", "Trimming Data");
        List<String> listFinal = new ArrayList<>();
        for(int i = 0; i<10; i++){
            listFinal.add(list.get(i));
        }
        Log.v("getFirstFifty", "Finished trimming Data");
        return listFinal;
    }


    public List<String> fixLinkList(List<String> list){
        List<String> fixedLinkList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            fixedLinkList.add("https://www.imdb.com" + list.get(i));
            Log.v("Link Fixer", "https://www.imdb.com" + list.get(i));
        }
        return fixedLinkList;
    }
    private List<String> getMovieID(List<String> list){
        List<String> movieIDList = new ArrayList<>();
        String temp;
        for(int i = 0; i < list.size(); i++){
            temp = "";
            for(int j = 7; j <=16; j++){
                if(list.get(i).charAt(j) != '/'){
                    temp = temp + list.get(i).charAt(j);
                }
            }
            movieIDList.add(temp);
            Log.d("MOVIEID", temp);
        }
        return movieIDList;
    }

    void setUpPopularRecyclerView(){
        this.popularRecyclerView = findViewById(R.id.popularRecyclerView);
        this.comingSoonRecyclerView = findViewById(R.id.comingSoonRecyclerView);
        this.popManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.soonManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.popularRecyclerView.setLayoutManager(this.popManager);
        this.comingSoonRecyclerView.setLayoutManager(this.soonManager);
        this.popAdapter = new MovieListAdapter(this.resultsPopular);
        this.popularRecyclerView.setAdapter(popAdapter);
        this.soonAdapter = new MovieListAdapter(this.resultsComingSoon);
        this.comingSoonRecyclerView.setAdapter(soonAdapter);
    }

}