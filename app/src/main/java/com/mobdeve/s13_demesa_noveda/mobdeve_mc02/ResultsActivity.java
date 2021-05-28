package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
    public void titleSearch(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    String query = et_resultsSearchParam.getText().toString();
                    query = query.replace(" ", "+");
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://movie-database-imdb-alternative.p.rapidapi.com/?s=" + query + "&page=1&r=json")
                            .get()
                            .addHeader("x-rapidapi-key", "3046c12cfdmsh5c0c9ec58a780edp111336jsnccb6948110d5")
                            .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                            .build();

                    Response response = client.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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

                    Log.v("End", "End of run");


                    //Create intent
//                    Intent i = new Intent(context, MovieList.class);
//                    //Pass lists
//                    i.putStringArrayListExtra("names", (ArrayList<String>)namesList);
//                    i.putStringArrayListExtra("links", (ArrayList<String>)linksList);
//                    i.putStringArrayListExtra("img", (ArrayList<String>)imgList);
//                    //Start activity
//                    startActivity(i);
//                    finish();

                } catch (IOException e) {
                }
            }
        }).start();
    }
    public void getDataTitleSearch(){
        new Thread(new Runnable() {
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
                        String source = img.attr("src");
                        imgList.add(source);
                        Log.v("Image List Builder", source);
                    }
                    if(listSize > 10) {
                        namesList = trimSelection(namesList);
                        linksList = trimSelection(linksList);
                        imgList = trimSelection(imgList);
                    }

                    //Test list by printing
                    //Iterator iterator = namesList.iterator();
                    //testWrite(iterator);

                    Log.v("getData", "Finished");

//                    //Create intent
//                    Intent i = new Intent(context, movieList.class);
//                    //Pass lists
//                    i.putStringArrayListExtra("names", (ArrayList<String>)namesList);
//                    i.putStringArrayListExtra("links", (ArrayList<String>)linksList);
//                    i.putStringArrayListExtra("img", (ArrayList<String>)imgList);
//                    //Start activity
//                    startActivity(i);
//                    finish();

                } catch (IOException e) {
                }
            }
        }).start();
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
}