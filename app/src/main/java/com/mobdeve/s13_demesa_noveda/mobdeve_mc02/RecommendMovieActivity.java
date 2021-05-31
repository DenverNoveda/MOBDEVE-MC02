package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecommendMovieActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private FirebaseUser currentUser;

    private TextView tv_selectedMovie;
    private ImageView iv_moviePhoto;
    private EditText et_userRecommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movie);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        // PANG CHECK LANG SIR
        Log.v("CHECKPOINT", "MESSAGE:");
        Log.v("CHECKPOINT", currentUser.getDisplayName());

        this.tv_selectedMovie = findViewById(R.id.tv_selectedMovie2);
        this.iv_moviePhoto = findViewById(R.id.iv_moviePhoto2);
        this.et_userRecommend = findViewById(R.id.et_userReview);

        this.tv_selectedMovie.setText(getIntent().getStringExtra("movieName"));
        Picasso.get().load(getIntent().getStringExtra("moviePosterPath")).into(iv_moviePhoto);
    }

    public void sendRecommendation(View v) {

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a | EEE, MMMM d, yyyy");

        String username = currentUser.getDisplayName();
        String selectedMovie = tv_selectedMovie.getText().toString();
        String recommendation = et_userRecommend.getText().toString();
        String timeDate = sdf.format(new Date());

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("movie", selectedMovie);
        user.put("recommendationText", recommendation);
        user.put("date", timeDate);

        database.collection("recommendations").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.v("RecommendMovieActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("RecommendMovieActivity", "Add Document Failure: Error adding document", e);
                    }
        });

        Intent i = new Intent(this, MovieInfoActivity.class);
        i.putExtra("MOVIE_ID", getIntent().getStringExtra("MOVIE_ID"));
        this.startActivity(i);
        setResult(2);
        finish();

    }
}