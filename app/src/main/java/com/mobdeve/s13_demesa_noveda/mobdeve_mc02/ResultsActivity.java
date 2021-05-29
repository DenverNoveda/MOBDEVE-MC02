package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private String address = "";
    private int listSize;
    private Elements moviesList, namesListAlpha, linksListAlpha;
    private List<String> namesList, linksList, imgList;
    private List<Document> docList;
    private Document doc;
    private String[] genreList = {null,null,null,null,null,null,null,null,null,null,null,null,null,null};
    private String genres;
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
//            movie.setImage(imgList.get(i));
            movie.setLink(linksList.get(i));
            results.add(movie);
        }
    }
    public void titleSearch(){
        genres = "";
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
            getDataTitleSearch();
        }
    }
    public void filterGenres(){
        //Find and set query data
            genres = "";
            for(int i = 0; i < 14; i++){
                String test2 = genreList[i];
                if(test2!=null){
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
                    moviesList = doc.select(".lister-item");
                    //Get list size
                    listSize = moviesList.size();
                    //Get names elements
                    namesListAlpha = moviesList.select("img");
                    imgList = moviesList.eachAttr("src");
                    namesList = namesListAlpha.eachAttr("alt");
                    //Get links elements
                    linksListAlpha = doc.select("h3.lister-item-header");
                    linksListAlpha = linksListAlpha.select("a");
                    linksList = linksListAlpha.eachAttr("href");
                    //Fix link elements
                    linksList = fixLinkList(linksList);
                    //Trim Lists
                    if(listSize > 10) {
                        namesList = trimSelection(namesList);
                        linksList = trimSelection(linksList);
                        listSize = 10;
                    }
                    //Get documents for image elements
//                    linksList.stream().forEach(link -> {
//                        try {
//                            Document document1 = Jsoup.connect(link).get();
//                            docList.add(document1);
//                        }catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
                    //Get image elements
//                    for (int i = 0; i < listSize;  i++){
//                        Document doc2 = docList.get(i);
//                        Element poster = doc2.selectFirst(".poster");
//                        Element img = poster.selectFirst("img");
//                        imgList.add(img.attr("src"));
//                        Log.v("Image List Builder", img.attr("src"));
//                    }
                    if(listSize > 10) {
                        namesList = trimSelection(namesList);
                        linksList = trimSelection(linksList);
                        imgList = trimSelection(imgList);
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

    private void setUpFilterButtons(){
        this.btn_resultsFilterAction = findViewById(R.id.btn_resultsFilterAction);
        this.btn_resultsFilterAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[0]==null) {
                    genreList[0] = "action";
                }
                else if(genreList[0].equalsIgnoreCase("action")){
                    genreList[0] = null;
                }
            }
        });

        this.btn_resultsFilterActCom = findViewById(R.id.btn_resultsFilterActCom);
        this.btn_resultsFilterActCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[1]==null) {
                    genreList[1] = "action,comedy";
                }
                else if(genreList[1].equalsIgnoreCase("action,comedy")){
                    genreList[1] = null;
                }
            }
        });

        this.btn_resultsFilterRomance = findViewById(R.id.btn_resultsFilterRomance);
        this.btn_resultsFilterRomance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[2]==null) {
                    genreList[2] = "romance";
                }
                else if(genreList[2].equalsIgnoreCase("romance")){
                    genreList[2] = null;
                };
            }
        });
        this.btn_resultsFilterDrama = findViewById(R.id.btn_resultsFilterDrama);
        this.btn_resultsFilterDrama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[3]==null) {
                    genreList[3] = "drama";
                }
                else if(genreList[3].equalsIgnoreCase("drama")){
                    genreList[3] = null;
                }
            }
        });

        this.btn_resultsFilterFantasy = findViewById(R.id.btn_resultsFilterFantasy);
        this.btn_resultsFilterFantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[4]==null) {
                    genreList[4] = "fantasy";
                }
                else if(genreList[4].equalsIgnoreCase("fantasy")){
                    genreList[4] = null;
                }
            }
        });
        this.btn_resultsFilterFamily = findViewById(R.id.btn_resultsFilterFamily);
        this.btn_resultsFilterFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[5]==null) {
                    genreList[5] = "family";
                }
                else if(genreList[5].equalsIgnoreCase("family")){
                    genreList[5] = null;
                }
            }
        });
        this.btn_resultsFilterCrime = findViewById(R.id.btn_resultsFilterCrime);
        this.btn_resultsFilterCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[6]==null) {
                    genreList[6] = "crime";
                }
                else if(genreList[6].equalsIgnoreCase("crime")){
                    genreList[6] = null;
                };
            }
        });
        this.btn_resultsFilterMystery = findViewById(R.id.btn_resultsFilterMystery);
        this.btn_resultsFilterMystery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[7]==null) {
                    genreList[7] = "mystery";
                }
                else if(genreList[7].equalsIgnoreCase("mystery")){
                    genreList[7] = null;
                }
            }
        });
        this.btn_resultsFilterHorror = findViewById(R.id.btn_resultsFilterHorror);
        this.btn_resultsFilterHorror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[8]==null) {
                    genreList[8] = "horror";
                }
                else if(genreList[3].equalsIgnoreCase("horror")){
                    genreList[8] = null;
                }
            }
        });
        this.btn_resultsFilterComedy = findViewById(R.id.btn_resultsFilterComedy);
        this.btn_resultsFilterComedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[9]==null) {
                    genreList[9] = "comedy";
                }
                else if(genreList[9].equalsIgnoreCase("comedy")){
                    genreList[9] = null;
                }
            }
        });
        this.btn_resultsFilterThriller = findViewById(R.id.btn_resultsFilterThriller);
        this.btn_resultsFilterThriller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[10]==null) {
                    genreList[10] = "thriller";
                }
                else if(genreList[10].equalsIgnoreCase("thriller")){
                    genreList[10] = null;
                }
            }
        });
        this.btn_resultsFilterRomCom = findViewById(R.id.btn_resultsFilterRomCom);
        this.btn_resultsFilterRomCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[11]==null) {
                    genreList[11] = "romance,comedy";
                }
                else if(genreList[11].equalsIgnoreCase("romance,comedy")){
                    genreList[11] = null;
                }
            }
        });
        this.btn_resultsFilterSciFi = findViewById(R.id.btn_resultsFilterScifi);
        this.btn_resultsFilterSciFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[12]==null) {
                    genreList[12] = "sci-fi";
                }
                else if(genreList[12].equalsIgnoreCase("sci-fo")){
                    genreList[12] = null;
                }
            }
        });
        this.btn_resultsFilterAdventure = findViewById(R.id.btn_resultsFilterAdventure);
        this.btn_resultsFilterAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[13]==null) {
                    genreList[13] = "adventure";
                }
                else if(genreList[13].equalsIgnoreCase("adventure")){
                    genreList[13] = null;
                }
            }
        });
        this.btn_resultsFilterAnimation = findViewById(R.id.btn_resultsFilterAnimation);
        this.btn_resultsFilterAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genreList[14]==null) {
                    genreList[14] = "animation";
                }
                else if(genreList[14].equalsIgnoreCase("animation")){
                    genreList[14] = null;
                }
            }
        });
    }
    private void setUpRecyclerView(){
        this.results_SearchRecyclerView = findViewById(R.id.results_searchRecylcerView);
        this.myAdapter = new MovieListAdapter(results);
        this.myManager = new LinearLayoutManager(this);
        this.results_SearchRecyclerView.setLayoutManager(myManager);
        this.results_SearchRecyclerView.setAdapter(myAdapter);
    }
}