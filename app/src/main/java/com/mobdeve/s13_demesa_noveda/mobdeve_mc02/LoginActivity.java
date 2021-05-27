package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Firebase variables
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    // Frontend variables
    private TextView tv_registerLink, tv_resetPassword;
    private EditText et_loginEmail, et_loginPass;
    private Button btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Check if user has logged in before (and has not logged out through the app) else, continue as normal
        if (currentUser != null) {
            // User is logged in -> redirect to main menu
            Intent i = new Intent(this, MainMenuActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        et_loginEmail = findViewById(R.id.et_loginEmail);
        et_loginPass = findViewById(R.id.et_loginPass);
        tv_registerLink = findViewById(R.id.tv_registerLink);
        tv_resetPassword = findViewById(R.id.tv_resetPassword);
        btn_login = findViewById(R.id.btn_login);
    }

    // Triggered By: Login button | Logs in the user with correct credentials and moves them to the Main Menu.
    public void login(View v) {

        // Get user input from EditText fields
        String email = et_loginEmail.getText().toString().trim();
        String pass = et_loginPass.getText().toString().trim();

        // Check if fields are empty | (1/2)
        if(email.isEmpty()) {
            et_loginEmail.setError("Please input your email.");
            et_loginEmail.requestFocus();
            return;
        }

        // Check if fields are empty | (2/2)
        else if(pass.isEmpty()) {
            et_loginPass.setError("Please input your password.");
            et_loginPass.requestFocus();
            return;
        }

        // Authenticate user credentials inputted
        else {
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If credentials are correct, login the user
                    if(task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                        startActivity(i);
                        finish();
                    }
                    // Else, inform user (through Toast) that credentials are invalid
                    else {
                        FirebaseAuthException e = (FirebaseAuthException)task.getException();
                        Toast.makeText(getApplicationContext(), "Invalid Login. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Triggered By: Pressing the Register TextView | Moves the user to the registration activity.
    public void register(View v) {

        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}