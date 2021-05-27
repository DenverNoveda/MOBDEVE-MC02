package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {

    private TextView tv_seeMorePopMovies, tv_seeMoreComingSoonMovies, tv_searchByGenre;
    private ImageView iv_goToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        tv_seeMorePopMovies = findViewById(R.id.tv_seeMorePopMovies);
        tv_seeMoreComingSoonMovies = findViewById(R.id.tv_seeMoreComingSoonMovies);
        tv_searchByGenre = findViewById(R.id.tv_searchByGenre);
        iv_goToProfile = findViewById(R.id.iv_goToProfile);
    }

    // Triggered By: Pressing the Profile image/icon | Moves the user to his/her profile.
    public void goToProfile(View v) {
        Intent i = new Intent(this, UserProfileActivity.class);
        startActivity(i);
    }

    // On pressing back, the application will exit
    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}