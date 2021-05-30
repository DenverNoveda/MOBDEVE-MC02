package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    // Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private FirebaseUser currentUser;

    // Front end variables
    private TextView tv_username, tv_userAccDate, tv_userBio, tv_logout;
    private RecyclerView favoritesRecyclerView, userReviewsRecyclerView, userRecommendationsRecyclerView;

    // others
    private ArrayList<Recommend> recommends = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<Favorite> favorites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        tv_username = findViewById(R.id.tv_username);
        tv_userAccDate = findViewById(R.id.tv_userAccDate);
        tv_userBio = findViewById(R.id.tv_userBio);
        tv_logout = findViewById(R.id.tv_logout);
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        userReviewsRecyclerView = findViewById(R.id.userReviewsRecyclerView);
        userRecommendationsRecyclerView = findViewById(R.id.userRecommendationsRecyclerView);

        setUserProfileData();
    }

    // Get user information from the Firebase database
    private void setUserProfileData() {

        String username = currentUser.getDisplayName();

        // (1) set basic user info
        database.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            tv_username.setText(document.getDocuments().get(0).get("username").toString());
                            tv_userAccDate.setText(document.getDocuments().get(0).get("accCreationDate").toString());
                            tv_userBio.setText(document.getDocuments().get(0).get("bio").toString());
                        } else {
                            Log.d("UserProfileActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // (2) set user's recommendations
        database.collection("recommendations").whereEqualTo("username", username).orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String movie = document.get("movie").toString();  Log.v("UserProfileActivity","String movie: " + movie);
                                String recommendationText = document.get("recommendationText").toString();  Log.v("UserProfileActivity","String recommendationText: " + recommendationText);
                                String date = document.get("date").toString();  Log.v("UserProfileActivity","String date: " + date);

                                Recommend recommend = new Recommend(movie, recommendationText, date, username);
                                recommends.add(recommend);
                                Log.d("UserProfileActivity", "From setUserProfileData(): (Part 2) " + recommends.toString());

                            }

                            Log.d("UserProfileActivity", "Setting up RecyclerView for User's Recommendations");
                            setUserRecommendationsAdapter();

                        } else {
                            Log.v("UserProfileActivity: ", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // (3) set user's reviews
        database.collection("reviews").whereEqualTo("username", username).orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String movie = document.get("movie").toString();  Log.v("UserProfileActivity","String movie: " + movie);
                                String reviewText = document.get("reviewText").toString();  Log.v("UserProfileActivity","String reviewText: " + reviewText);
                                String date = document.get("date").toString();  Log.v("UserProfileActivity","String date: " + date);
                                String rating = document.get("rating").toString(); Log.v("UserProfileActivity","String rating: " + rating);

                                Review review = new Review(movie, reviewText, date, username, rating);
                                reviews.add(review);
                                Log.d("UserProfileActivity", "From setUserProfileData(): (Part 3) " + reviews.toString());

                            }

                            Log.d("UserProfileActivity", "Setting up RecyclerView for User's Reviews");
                            setUserReviewsAdapter();

                        } else {
                            Log.v("UserProfileActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // (4) set user's favorites
        /*database.collection("favorites").whereEqualTo("username", username).orderBy("movie", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String movie = document.get("movie").toString();

                                Favorite favorite = new Favorite(movie, username);
                                favorites.add(favorite);
                                Log.d("UserProfileActivity", "From setUserProfileData(): (Part 4) " + favorites.toString());

                            }

                            Log.d("UserProfileActivity", "Setting up RecyclerView for User's Favorites");
                            setUserFavoritesAdapter();

                        } else {
                            Log.v("UserProfileActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });    */

    }

    // sets up userRecommendationsAdapter
    public void setUserRecommendationsAdapter() {
        Log.d("UserProfileActivity", "Invoked: setUserRecommendationsAdapter()");
        Log.d("UserProfileActivity", "From setUserRecommendationsAdapter(): " + recommends.toString());
        UserRecommendationsAdapter userRecoAdapter = new UserRecommendationsAdapter(recommends);
        RecyclerView.LayoutManager userRecommendationsLayoutManager = new LinearLayoutManager(getApplicationContext());
        userRecommendationsRecyclerView.setLayoutManager(userRecommendationsLayoutManager);
        userRecommendationsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        userRecommendationsRecyclerView.setAdapter(userRecoAdapter);
    }

    // sets up userReviewsAdapter
    public void setUserReviewsAdapter() {
        Log.d("UserProfileActivity", "Invoked: setUserReviewsAdapter()");
        Log.d("UserProfileActivity", "From setUserReviewsAdapter(): " + reviews.toString());
        UserReviewsAdapter userRevAdapter = new UserReviewsAdapter(reviews);
        RecyclerView.LayoutManager userReviewsLayoutManager = new LinearLayoutManager(getApplicationContext());
        userReviewsRecyclerView.setLayoutManager(userReviewsLayoutManager);
        userReviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        userReviewsRecyclerView.setAdapter(userRevAdapter);
    }

    // sets up userReviewsAdapter
    /*public void setUserFavoritesAdapter() {
        Log.d("UserProfileActivity", "Invoked: setUserFavoritesAdapter()");
        Log.d("UserProfileActivity", "From setUserFavoritesAdapter(): " + reviews.toString());
        UserFavoritesAdapter userFavAdapter = new UserFavoritesAdapter(favorites);
        RecyclerView.LayoutManager userFavoritesLayoutManager = new LinearLayoutManager(getApplicationContext());
        favoritesRecyclerView.setLayoutManager(userFavoritesLayoutManager);
        favoritesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        favoritesRecyclerView.setAdapter(userFavAdapter);
    }*/

    // Triggered By: Pressing the Logout TextView | Logs out the user and moves them to the registration activity.
    public void logout(View v) {
        mAuth.signOut();
        Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}