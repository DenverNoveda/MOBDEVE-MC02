package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class UserProfileActivity extends AppCompatActivity {

    // Firebase variables
    private FirebaseAuth mAuth;

    // Front end variables
    private TextView tv_username, tv_userAccDate, tv_userBio, tv_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();

        tv_username = findViewById(R.id.tv_username);
        tv_userAccDate = findViewById(R.id.tv_userAccDate);
        tv_userBio = findViewById(R.id.tv_userBio);
        tv_logout = findViewById(R.id.tv_logout);

    }

    // Triggered By: Pressing the Logout TextView | Logs out the user and moves them to the registration activity.
    public void logout(View v) {
        mAuth.signOut();
        Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}