package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultsActivity extends AppCompatActivity {

    private String address = "";
    private Context context;
    private int listSize;
    private Elements moviesList, namesListAlpha, linksListAlpha;
    private List<String> namesList, linksList, imgList;
    private List<Document> docList;
    private Document doc;
    private String[] genres = {};
    private ArrayList<Movie> results;
    private Handler mHandler;

    private RecyclerView results_SearchRecyclerView;
    private LinearLayoutManager myManager;
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
    private Button btn_resultsFilterRomCom;
    private Button btn_resultsFilterActCom;
    private Button btn_resultsFilterSuperhero;
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
        setUpRecyclerView();
        this.btn_searchResults = findViewById(R.id.btn_resultsSearch);
        this.et_resultsSearchParam = findViewById(R.id.et_resultsSearchParam);
        this.btn_resultsFilter = findViewById(R.id.btn_resultsFilter);
        this.results_FilterConstraintLayout = findViewById(R.id.results_FilterConstraintLayout);
        this.btn_searchResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleSearch();
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
            results.add(movie);
        }
    }
    public void titleSearch(){
        String query = et_resultsSearchParam.getText().toString();
        query = query.replace(" ", "+");
        address = "https://m.imdb.com/find?q=" + query + "&s=tt&ttype=ft";
        if(query.equals("")){
            Toast.makeText(this, "Please enter a query", Toast.LENGTH_LONG).show();
        }else{
            //Start jSoup function
            getDataTitleSearch();
        }
        }
    public void genreSearch(View v){
        //Find and set query data
        if(isEmptyStringArray(genres)){
            Toast.makeText(this, "Please select genres", Toast.LENGTH_LONG).show();
        }else{

            String query = "";
            for(int i = 0; i < 14; i++){
                String test2 = genres[i];
                if(test2!=null){
                    query = query + genres[i] + ",";
                }
            }
            address = "https://www.imdb.com/search/title/?genres=" + query + "&explore=title_type,genres&title_type=movie,tvMovie,tvSeries&ref_=adv_explore_rhs";
            //Start jSoup function
            getDataGenreSearch();
        }
    }
    public void getDataGenreSearch(){
        Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(address).get();
                    moviesList = doc.select(".lister-item");
                    //Get list size
                    listSize = moviesList.size();
                    //Get names elements
                    namesListAlpha = moviesList.select("img");
                    namesList = namesListAlpha.eachAttr("alt");
                    //Get first 10 elements;
                    if(listSize>10){
                        namesList = trimSelection(namesList);
                    }
                    //Get links elements
                    linksListAlpha = doc.select("h3.lister-item-header");
                    linksListAlpha = linksListAlpha.select("a");
                    linksList = linksListAlpha.eachAttr("href");
                    //Get first 10 elements;
                    if(listSize>10){
                        linksList = trimSelection(linksList);
                        listSize = 10;
                    }
                    //Fix link elements
                    linksList = fixLinkList(linksList);
                    //Get documents for image elements
                    linksList.stream().forEach(link -> {
                        try {
                            Document document1 = Jsoup.connect(link).get();
                            docList.add(document1);
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
//                    Get image elements
                    for (int i = 0; i<listSize;  i++){
                        Document doc2 = docList.get(i);
                        Element poster = doc.selectFirst(".poster");
                        Element img = poster.selectFirst("img");
                        String source = img.attr("src");
                        imgList.add(source);
                        Log.v("Image List Builder", source);
                    }
                    fillResults();
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.v("getData", "Finished ");
                    Log.v("End", "End of run");



                } catch (IOException e) {
                }
            }
        }).start();
    }
    public void getDataTitleSearch(){
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(address).get();
                    moviesList = doc.select("a.subpage:lt(100)");
                    //Get list size
                    listSize = moviesList.size();
                    //Get names elements
                    namesListAlpha = moviesList.select(".media-body");
                    namesListAlpha = namesListAlpha.select("span");
                    namesList = namesListAlpha.eachText();
                    //Get links elements
                    linksList = moviesList.eachAttr("href");
                    //Fix link elements
                    linksList = fixLinkList(linksList);
                    //Trim Lists
                    if(listSize > 10) {
                        namesList = trimSelection(namesList);
                        linksList = trimSelection(linksList);
                        listSize = 10;
                    }
                    //Get documents for image elements
                    linksList.stream().forEach(link -> {
                        try {
                            Document document1 = Jsoup.connect(link).get();
                            docList.add(document1);
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    //Get image elements
                    for (int i = 0; i < listSize;  i++){
                        Document doc2 = docList.get(i);
                        Element poster = doc2.selectFirst(".poster");
                        Element img = poster.selectFirst("img");
                        imgList.add(img.attr("src"));
                        Log.v("Image List Builder", img.attr("src"));
                    }
                    if(listSize > 10) {
                        namesList = trimSelection(namesList);
                        linksList = trimSelection(linksList);
                        imgList = trimSelection(imgList);
                    }
                    
                    Log.v("getData", "Finished");

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
    public boolean isEmptyStringArray(String [] array){
        for(int i=0; i<array.length; i++){
            if(array[i]!=null){
                return false;
            }
        }
        return true;
    }

    private void setUpRecyclerView(){
        this.results_SearchRecyclerView = findViewById(R.id.results_searchRecylcerView);
        this.myAdapter = new MovieListAdapter(results);
        this.myManager = new LinearLayoutManager(this);
        this.results_SearchRecyclerView.setLayoutManager(myManager);
        this.results_SearchRecyclerView.setAdapter(myAdapter);
    }
}