package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieInfoActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private FirebaseUser currentUser;

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
    private TextView tv_favorite;

    private RecyclerView movieInfo_RecommendRv;
    private RecyclerView movieInfo_ReviewRv;

    private int result;

    private ArrayList<Recommend> recommends = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Intent i = getIntent();
        movieID = i.getStringExtra("MOVIE_ID");

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        this.iv_movieInfoMain = findViewById(R.id.iv_movieInfoMain);
        this.tv_movieInfoName = findViewById(R.id.tv_movieInfoName);
        this.tv_movieInfoCast = findViewById(R.id.tv_movieInfoCast);
        this.tv_movieInfoDesc = findViewById(R.id.tv_movieInfoDesc);
        this.tv_favorite = findViewById(R.id.tv_favorite);
        this.movieInfo_RecommendRv = findViewById(R.id.movieInfo_RecommendRv);
        this.movieInfo_ReviewRv = findViewById(R.id.movieInfo_ReviewRv);

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

                            // get database infos + setup favorite
                            getRecommendations();
                            getReviews();
                            initializeFavorite();
                        }
                    });

                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    // sends user to RecommendMovieActivity
    public void writeRecommendation(View v) {
        Intent i = new Intent(v.getContext(), RecommendMovieActivity.class);
        i.putExtra("movieName", tv_movieInfoName.getText());
        i.putExtra("moviePosterPath", poster);
        i.putExtra("MOVIE_ID", movieID); // for loading back
        startActivityForResult(i, result);
    }

    // sends user to RecommendMovieActivity
    public void writeReview(View v) {
        Intent i = new Intent(v.getContext(), WriteReviewActivity.class);
        i.putExtra("movieName", tv_movieInfoName.getText());
        i.putExtra("moviePosterPath", poster);
        i.putExtra("MOVIE_ID", movieID); // for loading back
        startActivityForResult(i, result);
    }

    // retrieve Recommendations data from Firebase
    public void getRecommendations() {
        database.collection("recommendations").whereEqualTo("movie", tv_movieInfoName.getText().toString()).orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.v("getRecommendations(): ","ADDED: User: " + document.get("username") + " | Date: " + document.get("date"));

                                String movie = document.get("movie").toString();  Log.v("getRecommendations(): ","String movie: " + movie);
                                String recommendationText = document.get("recommendationText").toString();  Log.v("getRecommendations(): ","String recommendationText: " + recommendationText);
                                String date = document.get("date").toString();  Log.v("getRecommendations(): ","String date: " + date);
                                String username = document.get("username").toString();  Log.v("getRecommendations(): ","String username: " + username);

                                Recommend recommend = new Recommend(movie, recommendationText, date, username);
                                recommends.add(recommend);
                                Log.d("getRecommendations(): ", "From getRecommendations(): " + recommends.toString());

                            }

                            Log.d("getRecommendations(): ", "Setting up RecyclerView for Recommendations");
                            setRecommendationsAdapter();

                        } else {
                            Log.v("getRecommendations(): ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // retrieve Reviews data from Firebase
    public void getReviews() {
        database.collection("reviews").whereEqualTo("movie", tv_movieInfoName.getText().toString()).orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.v("getReviews(): ","ADDED: User: " + document.get("username") + " | Date: " + document.get("date"));

                                String movie = document.get("movie").toString();  Log.v("getReviews(): ","String movie: " + movie);
                                String reviewText = document.get("reviewText").toString();  Log.v("getReviews(): ","String recommendationText: " + reviewText);
                                String date = document.get("date").toString();  Log.v("getReviews(): ","String date: " + date);
                                String username = document.get("username").toString();  Log.v("getReviews(): ","String username: " + username);
                                String rating = document.get("rating").toString();

                                Review review = new Review(movie, reviewText, date, username, rating);
                                reviews.add(review);
                                Log.d("getReviews(): ", "From getReviews(): " + reviews.toString());

                            }

                            Log.d("getReviews(): ", "Setting up RecyclerView for Reviews");
                            setReviewsAdapter();

                        } else {
                            Log.v("getReviews(): ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void initializeFavorite() {

        Log.d("MovieInfoActivity", "initializeFavorite(): START");
        String movie = tv_movieInfoName.getText().toString();
        String user = currentUser.getDisplayName();

        Log.d("MovieInfoActivity", "initializeFavorite(): Attempting...");
        database.collection("favorites").whereEqualTo("username", user).whereEqualTo("movie", movie).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("MovieInfoActivity", "task.isSuccessful() triggered");
                            if (task.getResult().isEmpty()) {
                                Log.d("MovieInfoActivity", "initializeFavorite(): Not in favorites. Initializing...");
                                tv_favorite.setText("☆ ADD TO FAVORITES ☆");
                            } else {
                                Log.d("MovieInfoActivity", "initializeFavorite(): In favorites. Initializing...");
                                tv_favorite.setText("★ REMOVE FROM FAVORITES ★");
                            }
                        } else {
                            Log.d("MovieInfoActivity", "initializeFavorite(): Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void setFavorite(View v) {

        Log.d("MovieInfoActivity", "setupFavorite(): START");
        String movie = tv_movieInfoName.getText().toString();
        String user = currentUser.getDisplayName();

        Log.d("MovieInfoActivity", "checkIfFavorite(): Attempting...");
        database.collection("favorites").whereEqualTo("username", user).whereEqualTo("movie", movie).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("MovieInfoActivity", "task.isSuccessful() triggered");
                            if (task.getResult().isEmpty()) {
                                Log.d("MovieInfoActivity", "checkIfFavorite(): Not in favorites. Adding...");
                                addToFavorites();
                            } else {
                                Log.d("MovieInfoActivity", "checkIfFavorite(): In favorites. Removing...");
                                removeFromFavorites();
                            }
                        } else {
                            Log.d("MovieInfoActivity", "removeFromFavorites(): Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // add movie to favorites
    public void addToFavorites() {
        String movie = tv_movieInfoName.getText().toString();
        String user = currentUser.getDisplayName();

        Map<String, Object> newFavorite = new HashMap<>();
        newFavorite.put("username", user);
        newFavorite.put("movie", movie);

        database.collection("favorites").add(newFavorite).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("MovieInfoActivity", "addToFavorites(): DocumentSnapshot added with ID: " + documentReference.getId());
                tv_favorite.setText("★ REMOVE FROM FAVORITES ★");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MovieInfoActivity", "addToFavorites(): Add Document Failure: Error adding document", e);
            }
        });
    }

    // remove movie from favorites
    public void removeFromFavorites() {
        String movie = tv_movieInfoName.getText().toString();
        String user = currentUser.getDisplayName();

        database.collection("favorites").whereEqualTo("username", user).whereEqualTo("movie", movie).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                                Log.d("MovieInfoActivity", "removeFromFavorites(): Document successfully deleted.");
                                tv_favorite.setText("☆ ADD TO FAVORITES ☆");
                            }
                        } else {
                            Log.d("MovieInfoActivity", "removeFromFavorites(): Error getting documents: ", task.getException());
                        }
                    }
        });
    }

    // sets up RecommendationsAdapter
    public void setRecommendationsAdapter() {
        Log.d("getRecommendations(): ", "Invoked: setRecommendationsAdapter()");
        Log.d("getRecommendations(): ", "From setRecommendationsAdapter(): " + recommends.toString());
        RecommendationsAdapter recoAdapter = new RecommendationsAdapter(recommends);
        RecyclerView.LayoutManager recommendationsLayoutManager = new LinearLayoutManager(getApplicationContext());
        movieInfo_RecommendRv.setLayoutManager(recommendationsLayoutManager);
        movieInfo_RecommendRv.setItemAnimator(new DefaultItemAnimator());
        movieInfo_RecommendRv.setAdapter(recoAdapter);
    }

    // sets up ReviewsAdapter
    public void setReviewsAdapter() {
        Log.d("getReviews(): ", "Invoked: setReviewsAdapter()");
        Log.d("getReviews(): ", "From setReviewsAdapter(): " + reviews.toString());
        ReviewsAdapter revAdapter = new ReviewsAdapter(reviews);
        RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(getApplicationContext());
        movieInfo_ReviewRv.setLayoutManager(reviewsLayoutManager);
        movieInfo_ReviewRv.setItemAnimator(new DefaultItemAnimator());
        movieInfo_ReviewRv.setAdapter(revAdapter);
    }

    // basically, finish this activity if the user wrote a review/recommendation (so this activity essentially refreshes)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2){
            finish();
        }
    }
}