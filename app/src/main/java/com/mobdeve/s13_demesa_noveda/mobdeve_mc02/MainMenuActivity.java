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
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    private String addressPopMovies = "";
    private String addressPopShows = "";
    private String addressComingSoon = "";
    private int popMoviesListSize, popShowsListSize, soonListSize;
    private Elements popMoviesListAlpha, popNamesListAlpha, popLinksListAlpha;
    private Elements soonMoviesListAlpha, soonNamesListAlpha, soonLinksListAlpha;
    private Elements popShowsListAlpha, popShowsNamesListAlpha, popShowsLinksListAlpha;
    private List<String> popNamesList, popLinksList, popImgList, popMovieIDList;
    private List<String> popShowsNamesList, popShowsLinksList, popShowsImgList, popShowsIDList;
    private List<String> soonNamesList, soonLinksList, soonImgList,soonMovieIDList;
    private Document doc1, doc2;
    private ArrayList<Movie> resultsPopMovies;
    private ArrayList<Movie> resultsComingSoon;
    private ArrayList<Movie> resultsPopShows;
    private Handler mHandler;
    private RecyclerView popularMoviesRecyclerView;
    private RecyclerView popularShowsRecyclerView;
    private RecyclerView comingSoonRecyclerView;
    private RecyclerView.Adapter popMoviesAdapter;
    private LinearLayoutManager popMovieManager;
    private RecyclerView.Adapter popShowsAdapter;
    private LinearLayoutManager popShowsManager;
    private RecyclerView.Adapter soonAdapter;
    private LinearLayoutManager soonManager;

    private TextView tv_seeMorePopularMovies;
    private TextView tv_seeMorePopularShows;
    private TextView tv_seeMoreComingSoon;
    private TextView tv_searchByGenre;

    private ImageView iv_goToProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        popNamesList = new ArrayList<>();
        popLinksList = new ArrayList<>();
        popImgList = new ArrayList<>();
        popShowsNamesList = new ArrayList<>();
        popShowsLinksList = new ArrayList<>();
        popShowsImgList = new ArrayList<>();
        soonImgList = new ArrayList<>();
        soonLinksList = new ArrayList<>();
        soonNamesList = new ArrayList<>();

        resultsPopMovies = new ArrayList<>();
        resultsPopShows = new ArrayList<>();
        resultsComingSoon = new ArrayList<>();

        loadMostPopMovies();
        loadMostPopShows();
        loadComingSoon();

        setUpPopularRecyclerView();

        this.tv_seeMorePopularMovies = findViewById(R.id.tv_seeMorePopMovies);
        this.tv_seeMorePopularShows = findViewById(R.id.tv_seeMorePopShows);
        this.tv_seeMoreComingSoon = findViewById(R.id.tv_seeMoreComingSoonMovies);
        this.tv_searchByGenre = findViewById(R.id.tv_searchByGenre);
        this.iv_goToProfile = findViewById(R.id.iv_goToProfile);

        Log.d("SETUP", "OnCreate Done");


        this.tv_searchByGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, ResultsActivity.class);
                startActivity(i);

            }
        });

        this.iv_goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, UserProfileActivity.class);
                startActivity(i);
            }
        });
        this.tv_seeMorePopularMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, ResultsActivity.class);
                i.setAction("LOAD_MORE_POPULAR_MOVIES");
                startActivity(i);
            }
        });
        this.tv_seeMorePopularShows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, ResultsActivity.class);
                i.setAction("LOAD_MORE_POPULAR_SHOWS");
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

    private void loadMostPopMovies() {
        addressPopMovies = "https://www.imdb.com/chart/moviemeter/?ref_=nv_mv_mpm";
        getDataPopMovies();
    }
    private void loadMostPopShows() {
        addressPopShows = "https://www.imdb.com/chart/tvmeter/?ref_=nv_tvv_mptv";
        getDataPopShows();
    }

    private void loadComingSoon(){
        addressComingSoon = "https://www.imdb.com/movies-coming-soon/?ref_=nv_mv_cs";
        getDataComingSoon();
    }
    private void fillPopularMoviesArray(){
        for(int i = 0; i< popNamesList.size(); i++){
            Movie movie = new Movie();
            Log.d("Name", popNamesList.get(i));
            Log.d("Link", popLinksList.get(i));
            Log.d("Img", popImgList.get(i));
            movie.setMovieName(popNamesList.get(i));
            movie.setImage(popImgList.get(i));
            movie.setLink(popLinksList.get(i));
            movie.setMovieID(popMovieIDList.get(i));
            resultsPopMovies.add(movie);
        }
    }
    private void fillPopularShowsArray(){
        for(int i = 0; i< popShowsNamesList.size(); i++){
            Movie movie = new Movie();
            Log.d("Name", popShowsNamesList.get(i));
            Log.d("Link", popShowsLinksList.get(i));
            Log.d("Img", popShowsImgList.get(i));
            movie.setMovieName(popShowsNamesList.get(i));
            movie.setImage(popShowsImgList.get(i));
            movie.setLink(popShowsLinksList.get(i));
            movie.setMovieID(popShowsIDList.get(i));
            resultsPopShows.add(movie);
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
    private void getDataPopMovies() {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc1 = Jsoup.connect(addressPopMovies).get();
                    popMoviesListAlpha = doc1.select("td.posterColumn");
                    //Get list size
                    popShowsListSize = popMoviesListAlpha.size();
                    //Get names elements
                    popNamesListAlpha = popMoviesListAlpha.select("img");
                    popNamesList = popNamesListAlpha.eachAttr("alt");
                    popImgList = popNamesListAlpha.eachAttr("src");
                    //Get first 10 elements;
                    if(popMoviesListSize >10){
                        popNamesList = trimSelection(popNamesList);
                    }
                    //Get links elements
                    popLinksListAlpha = doc1.select("td.posterColumn");
                    popLinksListAlpha = popLinksListAlpha.select("a");
                    popLinksList = popLinksListAlpha.eachAttr("href");
                    //Get first 10 elements;
                    if(popMoviesListSize >10){
                        popLinksList = trimSelection(popLinksList);
                        popMoviesListSize = 10;
                    }
                    //Fix link elements
                    popMovieIDList = getMovieID(popLinksList);
                    popLinksList = fixLinkList(popLinksList);

                    fillPopularMoviesArray();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            popMoviesAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.v("End", "End of run");
                    Log.v("getData", "Finished ");


                } catch (IOException e) {
                }
            }
        });
        thread.start();
    }

    private void getDataPopShows() {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc2 = Jsoup.connect(addressPopShows).get();
                    popShowsListAlpha = doc2.select("td.posterColumn");
                    //Get list size
                    popShowsListSize = popShowsListAlpha.size();
                    //Get names elements
                    popShowsNamesListAlpha = popShowsListAlpha.select("img");
                    popShowsNamesList = popShowsNamesListAlpha.eachAttr("alt");
                    popShowsImgList = popShowsNamesListAlpha.eachAttr("src");
                    //Get first 10 elements;
                    if(popShowsListSize >10){
                        popShowsNamesList = trimSelection(popShowsNamesList);
                    }
                    //Get links elements
                    popShowsLinksListAlpha = doc2.select("td.posterColumn");
                    popShowsLinksListAlpha = popShowsLinksListAlpha.select("a");
                    popShowsLinksList = popShowsLinksListAlpha.eachAttr("href");
                    //Get first 10 elements;
                    if(popShowsListSize >10){
                        popShowsLinksList = trimSelection(popShowsLinksList);
                        popShowsListSize = 10;
                    }
                    //Fix link elements
                    popShowsIDList = getMovieID(popShowsLinksList);
                    popShowsLinksList = fixLinkList(popShowsLinksList);

                    fillPopularShowsArray();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            popShowsAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.v("End", "End of run");
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
                    doc1 = Jsoup.connect(addressComingSoon).get();
                    soonMoviesListAlpha = doc1.select("div.image");
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
                    soonLinksListAlpha = doc1.select("div.image");
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

                    fillComingSoonArray();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            soonAdapter.notifyDataSetChanged();
                        }
                    });
                    Log.v("End", "End of run");
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
        this.popularMoviesRecyclerView = findViewById(R.id.popMoviesRecyclerView);
        this.comingSoonRecyclerView = findViewById(R.id.comingSoonRecyclerView);
        this.popularShowsRecyclerView = findViewById(R.id.popShowsRecyclerView);
        this.popMovieManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.popShowsManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.soonManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.popularMoviesRecyclerView.setLayoutManager(this.popMovieManager);
        this.popularShowsRecyclerView.setLayoutManager(this.popShowsManager);
        this.comingSoonRecyclerView.setLayoutManager(this.soonManager);
        this.popMoviesAdapter = new MovieListAdapter(this.resultsPopMovies);
        this.popularMoviesRecyclerView.setAdapter(popMoviesAdapter);
        this.popShowsAdapter = new MovieListAdapter(resultsPopShows);
        this.popularShowsRecyclerView.setAdapter(popShowsAdapter);
        this.soonAdapter = new MovieListAdapter(this.resultsComingSoon);
        this.comingSoonRecyclerView.setAdapter(soonAdapter);

    }

}