package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ResultsActivity extends AppCompatActivity {

    private String address = "";
    private int listSize;
    private Elements moviesList, namesListAlpha, linksListAlpha, imgListAlpha;
    private List<String> namesList, linksList, imgList, movieIDList;
    private List<Document> docList;
    private Document doc;
    private String[] genreList = {null,null,null,null,null,null,null,null,null,null,null,null,null};
    private String genres;
    private ArrayList<Movie> results;

    private Handler mHandler;

    private RecyclerView results_SearchRecyclerView;
    private GridLayoutManager myManager;
    private MovieListAdapter myAdapter;

    private ConstraintLayout results_FilterConstraintLayout;
    private Button btn_searchResults;
    private Button btn_resultsFilter;
    private Button btn_resultsFilterAction;
    private Button btn_resultsFilterRomance;
    private Button btn_resultsFilterComedy;
    private Button btn_resultsFilterFantasy;
    private Button btn_resultsFilterCrime;
    private Button btn_resultsFilterHorror;
    private Button btn_resultsFilterThriller;
    private Button btn_resultsFilterAdventure;
    private Button btn_resultsFilterAnimation;
    private Button btn_resultsFilterSciFi;
    private Button btn_resultsFilterFamily;
    private Button btn_resultsFilterMystery;
    private Button btn_resultsFilterDrama;

    private EditText et_resultsSearchParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        docList = new ArrayList<>();
        namesList = new ArrayList<>();
        linksList = new ArrayList<>();
        imgList = new ArrayList<>();
        results = new ArrayList<>();
        Intent i = getIntent();
        if(i.getAction()!=null){
            if(i.getAction().equalsIgnoreCase("LOAD_MORE_POPULAR")){
                address = "https://www.imdb.com/chart/moviemeter/?ref_=nv_mv_mpm";
                results.clear();
                getDataMostPopular();
            }else if(i.getAction().equalsIgnoreCase("LOAD_MORE_COMING_SOON")){
                address = "https://www.imdb.com/movies-coming-soon/?ref_=nv_mv_cs";
                results.clear();
                getDataComingSoon();
            }
        }
        setUpRecyclerView();
        this.btn_searchResults = findViewById(R.id.btn_resultsSearch);
        this.et_resultsSearchParam = findViewById(R.id.et_resultsSearchParam);
        this.btn_resultsFilter = findViewById(R.id.btn_resultsFilter);
        this.results_FilterConstraintLayout = findViewById(R.id.results_FilterConstraintLayout);
        setUpFilterButtons();

        this.btn_searchResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                results_FilterConstraintLayout.setVisibility(View.GONE);
                titleSearch();
                fillResults();
            }
        });
        this.btn_resultsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(results_FilterConstraintLayout.getVisibility() == View.GONE){
                    results_FilterConstraintLayout.setVisibility(View.VISIBLE);
                }else if(results_FilterConstraintLayout.getVisibility() == View.VISIBLE){
                    results_FilterConstraintLayout.setVisibility(View.GONE);
                }
            }
        });
    }
    private void fillResults(){
        for(int i = 0; i< namesList.size(); i++){
            Movie movie = new Movie();
            Log.d("Name", namesList.get(i));
            Log.d("Link", linksList.get(i));
            movie.setMovieName(namesList.get(i));
            movie.setImage(imgList.get(i));
            movie.setLink(linksList.get(i));
            movie.setMovieID(movieIDList.get(i));
            results.add(movie);
        }
    }
    public void titleSearch(){
        genres = "";
        docList.clear();
        namesList.clear();
        linksList.clear();
        imgList.clear();
        results.clear();
        filterGenres();
        String query = et_resultsSearchParam.getText().toString();
        query = query.replace(" ", "+");
        address = "https://www.imdb.com/search/title/?title=" + query;
        if(!genres.isEmpty())
            address = "https://m.imdb.com/search/title/?title="+ query + "&genres=" + genres;
        if(query.equals("")){
            Toast.makeText(this, "Please enter a query", Toast.LENGTH_LONG).show();
        }else{
            //Start jSoup function
            Toast.makeText(this, "Loading results...", Toast.LENGTH_LONG).show();
            getDataTitleSearch();
        }
    }
    public void filterGenres(){
        //Find and set query data
            genres = "";
            for(int i = 0; i < 13; i++){
                String test = genreList[i];
                if(test!=null){
                    genres = genres + genreList[i] + ",";
                }

        }
    }

    public void getDataTitleSearch(){
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(address).get();
                    moviesList = doc.select(".lister-item-image");
                    //Get list size
                    listSize = moviesList.size();
                    //Get names elements
//                    namesListAlpha = moviesList.select("img");
//                    imgListAlpha = moviesList.select("img");
//                    namesList = namesListAlpha.eachAttr("alt");
//                    imgList = imgListAlpha.eachAttr("src");
                    //Get links elements
                    linksListAlpha = doc.select("div.lister-item-image");
                    linksListAlpha = linksListAlpha.select("a");
                    linksList = linksListAlpha.eachAttr("href");
                    //Fix link elements

                    movieIDList = getMovieID(linksList);
                    linksList = fixLinkList(linksList);
//                    if(listSize > 30){
////                        namesList = trimSelection(namesList);
//                        linksList = trimSelection(linksList);
//                        movieIDList = trimSelection(movieIDList);
//                        listSize = 30;
//                    }

                    for(int i = 0; i < listSize; i++){
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("https://movie-database-imdb-alternative.p.rapidapi.com/?i=" + movieIDList.get(i) + "&r=json")
                                .get()
                                .addHeader("x-rapidapi-key", "3046c12cfdmsh5c0c9ec58a780edp111336jsnccb6948110d5")
                                .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String test = response.body().string();
                            JSONObject obj = new JSONObject(test);
                            String success = (String) obj.get("Response");
                            if(success.equalsIgnoreCase("False")){
                                linksList.remove(i);
                            }else{
                                namesList.add((String) obj.get("Title"));
                                imgList.add((String) obj.get("Poster"));
                            }
                        }catch (IOException | JSONException e){
                            e.printStackTrace();
                        }
                    }
                    fillResults();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.v("getData", "Finished");

                } catch (IOException e) {
                }
            }
        });

        thread.start();
    }

    private void getDataMostPopular() {
        results.clear();
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(address).get();
                    moviesList = doc.select("td.posterColumn");
                    //Get list size
                    listSize = moviesList.size();
                    //Get names elements
                    namesListAlpha = moviesList.select("img");
                    namesList = namesListAlpha.eachAttr("alt");
                    imgList = namesListAlpha.eachAttr("src");
                    //Get first 30 elements;
//                    if(listSize>30){
//                        namesList = trimSelection(namesList);
//                    }
                    //Get links elements
                    linksListAlpha = doc.select("td.posterColumn");
                    linksListAlpha = linksListAlpha.select("a");
                    linksList = linksListAlpha.eachAttr("href");
                    //Get first 10 elements;
//                    if(listSize>30){
//                        linksList = trimSelection(linksList);
//                        listSize = 30;
//                    }
                    //Fix link elements
                    movieIDList = getMovieID(linksList);
                    linksList = fixLinkList(linksList);

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

                    fillResults();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
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
                    doc = Jsoup.connect(address).get();
                    moviesList = doc.select("div.image");
                    //Get list size
                    listSize = moviesList.size();
                    //Get names elements
                    namesListAlpha = moviesList.select("img");
                    namesList = namesListAlpha.eachAttr("title");
                    imgList = namesListAlpha.eachAttr("src");
                    //Get first 30 elements;
//                    if(listSize>30){
//                        namesList = trimSelection(namesList);
//                    }
                    //Get links elements
                    linksListAlpha = doc.select("div.image");
                    linksListAlpha = linksListAlpha.select("a");
                    linksList = linksListAlpha.eachAttr("href");
                    //Get first 30 elements;
//                    if(listSize>30){
//                        linksList = trimSelection(linksList);
//                        listSize = 30;
//                    }
                    //Fix link elements
                    movieIDList = getMovieID(linksList);
                    linksList = fixLinkList(linksList);
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



                    fillResults();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.v("getData", "Finished ");

                } catch (IOException e) {
                }
            }
        });
        thread.start();
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
    public List<String> trimSelection(List<String> list){
        Log.v("getFirstFifty", "Trimming Data");
        List<String> listFinal = new ArrayList<>();
        for(int i = 0; i<30; i++){
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

    private void setUpFilterButtons(){
        this.btn_resultsFilterAction = findViewById(R.id.btn_resultsFilterAction);
        this.btn_resultsFilterAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[0]==null) {
                    genreList[0] = "action";
                    btn_resultsFilterAction.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterAction.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[0].equalsIgnoreCase("action")){
                    genreList[0] = null;
                    btn_resultsFilterAction.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterAction.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });

        this.btn_resultsFilterRomance = findViewById(R.id.btn_resultsFilterRomance);
        this.btn_resultsFilterRomance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[1]==null) {
                    genreList[1] = "romance";
                    btn_resultsFilterRomance.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterRomance.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[1].equalsIgnoreCase("romance")){
                    genreList[1] = null;
                    btn_resultsFilterRomance.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterRomance.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterDrama = findViewById(R.id.btn_resultsFilterDrama);
        this.btn_resultsFilterDrama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[2]==null) {
                    genreList[2] = "drama";
                    btn_resultsFilterDrama.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterDrama.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[2].equalsIgnoreCase("drama")){
                    genreList[2] = null;
                    btn_resultsFilterDrama.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterDrama.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });

        this.btn_resultsFilterFantasy = findViewById(R.id.btn_resultsFilterFantasy);
        this.btn_resultsFilterFantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[3]==null) {
                    genreList[3] = "fantasy";
                    btn_resultsFilterFantasy.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterFantasy.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[3].equalsIgnoreCase("fantasy")){
                    genreList[3] = null;
                    btn_resultsFilterFantasy.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterFantasy.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterFamily = findViewById(R.id.btn_resultsFilterFamily);
        this.btn_resultsFilterFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[4]==null) {
                    genreList[4] = "family";
                    btn_resultsFilterFamily.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterFamily.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[4].equalsIgnoreCase("family")){
                    genreList[4] = null;
                    btn_resultsFilterFamily.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterFamily.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterCrime = findViewById(R.id.btn_resultsFilterCrime);
        this.btn_resultsFilterCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[5]==null) {
                    genreList[5] = "crime";
                    btn_resultsFilterCrime.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterCrime.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[5].equalsIgnoreCase("crime")){
                    genreList[5] = null;
                    btn_resultsFilterCrime.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterCrime.setBackgroundColor(Color.parseColor("#EEEEEE"));
                };
            }
        });
        this.btn_resultsFilterMystery = findViewById(R.id.btn_resultsFilterMystery);
        this.btn_resultsFilterMystery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[6]==null) {
                    genreList[6] = "mystery";
                    btn_resultsFilterMystery.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterMystery.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[6].equalsIgnoreCase("mystery")){
                    genreList[6] = null;
                    btn_resultsFilterMystery.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterMystery.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterHorror = findViewById(R.id.btn_resultsFilterHorror);
        this.btn_resultsFilterHorror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[7]==null) {
                    genreList[7] = "horror";
                    btn_resultsFilterHorror.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterHorror.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[7].equalsIgnoreCase("horror")){
                    genreList[7] = null;
                    btn_resultsFilterHorror.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterHorror.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterComedy = findViewById(R.id.btn_resultsFilterComedy);
        this.btn_resultsFilterComedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[8]==null) {
                    genreList[8] = "comedy";
                    btn_resultsFilterComedy.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterComedy.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[8].equalsIgnoreCase("comedy")){
                    genreList[8] = null;
                    btn_resultsFilterComedy.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterComedy.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterThriller = findViewById(R.id.btn_resultsFilterThriller);
        this.btn_resultsFilterThriller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[9]==null) {
                    genreList[9] = "thriller";
                    btn_resultsFilterThriller.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterThriller.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[9].equalsIgnoreCase("thriller")){
                    genreList[9] = null;
                    btn_resultsFilterThriller.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterThriller.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterSciFi = findViewById(R.id.btn_resultsFilterScifi);
        this.btn_resultsFilterSciFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[10]==null) {
                    genreList[10] = "sci-fi";
                    btn_resultsFilterSciFi.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterSciFi.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[10].equalsIgnoreCase("sci-fo")){
                    genreList[10] = null;
                    btn_resultsFilterSciFi.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterSciFi.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterAdventure = findViewById(R.id.btn_resultsFilterAdventure);
        this.btn_resultsFilterAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[11]==null) {
                    genreList[11] = "adventure";
                    btn_resultsFilterAdventure.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterAdventure.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[11].equalsIgnoreCase("adventure")){
                    genreList[11] = null;
                    btn_resultsFilterAdventure.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterAdventure.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
        this.btn_resultsFilterAnimation = findViewById(R.id.btn_resultsFilterAnimation);
        this.btn_resultsFilterAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[12]==null) {
                    genreList[12] = "animation";
                    btn_resultsFilterAnimation.setTextColor(Color.parseColor("#EEEEEE"));
                    btn_resultsFilterAnimation.setBackgroundColor(Color.parseColor("#222831"));
                }
                else if(genreList[12].equalsIgnoreCase("animation")){
                    genreList[12] = null;
                    btn_resultsFilterAnimation.setTextColor(Color.parseColor("#393e46"));
                    btn_resultsFilterAnimation.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });
    }
    private void setUpRecyclerView(){
        this.results_SearchRecyclerView = findViewById(R.id.results_searchRecylcerView);
        this.myAdapter = new MovieListAdapter(results);
        this.myManager = new GridLayoutManager(getApplicationContext(), 3);
        this.results_SearchRecyclerView.setLayoutManager(myManager);
        this.results_SearchRecyclerView.setAdapter(myAdapter);
    }
}