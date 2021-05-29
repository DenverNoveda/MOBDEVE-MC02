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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    private String addressPop = "";
    private String addressComingSoon = "";
    private int listSize;
    private Elements popMoviesListAlpha, popNamesListAlpha, popLinksListAlpha;
    private Elements soonMoviesListAlpha, soonNamesListAlpha, soonLinksListAlpha;
    private List<String> popNamesList, popLinksList, popImgList, popMovieIDList;
    private List<String> soonNamesList, soonLinksList, soonImgList;
    private List<Document> docList;
    private Document doc;
    private ArrayList<Movie> resultsPopular;
    private ArrayList<Movie> resultsComingSoon;
    private Handler mHandler;
    private RecyclerView popularRecyclerView;
    private RecyclerView comingSoonRecyclerView;
    private RecyclerView.Adapter myAdapter1;
    private LinearLayoutManager myManager1;
    private RecyclerView.Adapter myAdapter2;
    private LinearLayoutManager myManager2;

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
//        loadComingSoon();

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
    }

    private void loadMostPopular() {
        addressPop = "https://m.imdb.com/search/title/?groups=top_100";
        getDataMostPopular();
    }

    private void loadComingSoon(){
        addressComingSoon = "https://www.imdb.com/search/title/?user_rating=8.0,10.0";
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
                    popMoviesListAlpha = doc.select(".lister-item");
                    //Get list size
                    listSize = popMoviesListAlpha.size();
                    //Get names elements
                    popNamesListAlpha = popMoviesListAlpha.select("img");
                    popImgList = popNamesListAlpha.eachAttr("src");
                    popNamesList = popNamesListAlpha.eachAttr("alt");
                    //Get first 10 elements;
                    if(listSize>10){
                        popNamesList = trimSelection(popNamesList);
                    }
                    //Get links elements
                    popLinksListAlpha = doc.select("h3.lister-item-header");
                    popLinksListAlpha = popLinksListAlpha.select("a");
                    popLinksList = popLinksListAlpha.eachAttr("href");
                    //Get first 10 elements;
                    if(listSize>10){
                        popLinksList = trimSelection(popLinksList);
                        listSize = 10;
                    }
                    //Fix link elements
                    popMovieIDList = getMovieID(popLinksList);
                    popLinksList = fixLinkList(popLinksList);

                    // Get Images
                    //Get documents for image elements
//                    popLinksList.stream().forEach(link -> {
//                        try {
//                            Document document1 = Jsoup.connect(link).get();
//                            docList.add(document1);
//                        }catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//
//                    for (int i = 0; i<listSize;  i++){
//                        Document doc2 = docList.get(i);
//                        Element poster = doc2.selectFirst(".poster");
//                        Element img = poster.selectFirst("img");
//                        popImgList.add(img.attr("src"));
//                        Log.v("Image List Builder",img.attr("src") );
//                    }
                    Log.v("End", "End of run");

                   fillPopularArray();
                   mHandler = new Handler(Looper.getMainLooper());
                   mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter1.notifyDataSetChanged();
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
                    soonMoviesListAlpha = doc.select(".lister-item");
                    //Get list size
                    listSize = soonMoviesListAlpha.size();
                    //Get names elements
                    soonNamesListAlpha = soonMoviesListAlpha.select("img");
                    soonNamesList = soonNamesListAlpha.eachAttr("alt");
                    //Get first 10 elements;
                    if(listSize>10){
                        soonNamesList = trimSelection(soonNamesList);
                    }
                    //Get links elements
                    soonLinksListAlpha = doc.select("h3.lister-item-header");
                    soonLinksListAlpha = soonLinksListAlpha.select("a");
                    soonLinksList = soonLinksListAlpha.eachAttr("href");
                    //Get first 10 elements;
                    if(listSize>10){
                        soonLinksList = trimSelection(soonLinksList);
                        listSize = 10;
                    }
                    //Fix link elements
                    soonLinksList = fixLinkList(soonLinksList);
                    //Get documents for image elements
//                    soonLinksList.stream().forEach(link -> {
//                        try {
//                            Document document1 = Jsoup.connect(link).get();
//                            docList.add(document1);
//                        }catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                    for (int i = 0; i<listSize;  i++){
//                        Document doc2 = docList.get(i);
//                        Element poster = doc2.selectFirst(".poster");
//                        Element img = poster.selectFirst("img");
//                        soonImgList.add(img.attr("src"));
//                        Log.v("Image List Builder",img.attr("src") );
//                    }

                    Log.v("End", "End of run");


                    //Test list by printing
                    //Iterator iterator = namesList.iterator();
                    //testWrite(iterator);


                    fillComingSoonArray();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter2.notifyDataSetChanged();
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
            for(int j = 7; j <=15; j++){
                temp = temp + list.get(i).charAt(j);
            }
            movieIDList.add(temp);
            Log.d("MOVIEID", temp);
        }
        return movieIDList;
    }

    void setUpPopularRecyclerView(){
        this.popularRecyclerView = findViewById(R.id.popularRecyclerView);
        this.comingSoonRecyclerView = findViewById(R.id.comingSoonRecyclerView);
        this.myManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.myManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.popularRecyclerView.setLayoutManager(this.myManager1);
        this.comingSoonRecyclerView.setLayoutManager(this.myManager2);
        this.myAdapter1 = new MovieListAdapter(this.resultsPopular);
        this.popularRecyclerView.setAdapter(myAdapter1);
        this.myAdapter2 = new MovieListAdapter(this.resultsComingSoon);
        this.comingSoonRecyclerView.setAdapter(myAdapter2);
    }
}