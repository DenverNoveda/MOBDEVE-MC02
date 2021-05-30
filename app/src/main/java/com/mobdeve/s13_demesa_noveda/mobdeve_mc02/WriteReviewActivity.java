package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class WriteReviewActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private FirebaseUser currentUser;

    private TextView tv_selectedMovie2;
    private ImageView iv_moviePhoto2;
    private EditText et_userReview;
    private RatingBar rb_userReviewRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        this.tv_selectedMovie2 = findViewById(R.id.tv_selectedMovie2);
        this.iv_moviePhoto2 = findViewById(R.id.iv_moviePhoto2);
        this.et_userReview = findViewById(R.id.et_userReview);
        this.rb_userReviewRating = findViewById(R.id.rb_userReviewRating);

        this.tv_selectedMovie2.setText(getIntent().getStringExtra("movieName"));
        Picasso.get().load(getIntent().getStringExtra("moviePosterPath")).into(iv_moviePhoto2);
    }

    public void sendReview(View v) {

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a | EEE, MMMM d, yyyy");

        String username = currentUser.getDisplayName();
        String selectedMovie = tv_selectedMovie2.getText().toString();
        String review  = et_userReview.getText().toString();
        String timeDate = sdf.format(new Date());
        String reviewScore = rb_userReviewRating.getRating() + "";

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("movie", selectedMovie);
        user.put("reviewText", review);
        user.put("date", timeDate);
        user.put("rating", reviewScore);

        database.collection("reviews").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.v("WriteReviewActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("WriteReviewActivity", "Add Document Failure: Error adding document", e);
            }
        });

        Intent i = new Intent(this, MovieInfoActivity.class);
        i.putExtra("MOVIE_ID", getIntent().getStringExtra("MOVIE_ID"));
        this.startActivity(i);
        setResult(2);
        finish();
    }
}