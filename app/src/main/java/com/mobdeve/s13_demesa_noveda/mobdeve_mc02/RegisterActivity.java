package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Firebase variables
    private FirebaseAuth auth;
    private FirebaseFirestore database;


    // Front end variables
    private TextView et_registerUsername, et_registerPass, et_registerPassConfirm, et_registerEmail;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        et_registerUsername = findViewById(R.id.et_registerUsername);
        et_registerEmail = findViewById(R.id.et_registerEmail);
        et_registerPass = findViewById(R.id.et_registerPass);
        et_registerPassConfirm = findViewById(R.id.et_registerPassConfirm);
        btn_register = findViewById(R.id.btn_register);

    }

    // Triggered By: Register button | Registers the user and moves them to the Main Menu.
    public void registerUser(View v) {

        // Get user input from EditText fields
        String username = et_registerUsername.getText().toString().trim();
        String email = et_registerEmail.getText().toString().trim();
        String pass = et_registerPass.getText().toString().trim();
        String confirmPass = et_registerPassConfirm.getText().toString().trim();

        // Check: Is the username empty? (1/7)
        if(username.isEmpty()) {
            et_registerUsername.setError("Please input a username.");
            et_registerUsername.requestFocus();
            return;
        }

        // Check: Is the email empty? (2/7)
        else if(email.isEmpty()) {
            et_registerEmail.setError("Please input an Email.");
            et_registerEmail.requestFocus();
            return;
        }

        // Check: Is the password empty? (3/7)
        else if(pass.isEmpty()) {
            et_registerPass.setError("Please input a password.");
            et_registerPass.requestFocus();
            return;
        }

        // Check: Is the confirm password empty? (4/7)
        else if(confirmPass.isEmpty()) {
            et_registerPassConfirm.setError("Please confirm your password.");
            et_registerPassConfirm.requestFocus();
            return;
        }

        // Check: Is the email valid? (5/7)
        else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            et_registerEmail.setError("Please provide a valid email.");
            et_registerEmail.requestFocus();
            return;
        }

        // Check: Are the passwords matching? (6/7)
        else if(!(pass).equals(confirmPass)) {
            et_registerPassConfirm.setError("Passwords do not match.");
            et_registerPassConfirm.requestFocus();
            return;
        }

        // Check: Is the password at least six (6) characters? (7/7)
        else if(pass.length() < 6) {
            et_registerPass.setError("Password should at least be six (6) characters.");
            et_registerPass.requestFocus();
            return;
        }

        // Inputted info are good, confirm user registration
        else {
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // (1) Create new user with the info inputted (by the user)
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                                String timeDate = sdf.format(new Date());

                                Map<String, Object> user = new HashMap<>();
                                user.put("username", username);
                                user.put("password", pass);
                                user.put("email", email);
                                user.put("accCreationDate", timeDate);
                                user.put("bio", " ");

                                 // (2) add new user as a new document (in "users" collection) with generated ID
                                database.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.v("ZED", "DocumentSnapshot added with ID: " + documentReference.getId());

                                        FirebaseUser user = auth.getCurrentUser();
                                        Log.v("ZED", "getCurrentUser(): " + auth.getCurrentUser().getUid());
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                        user.updateProfile(profileUpdates);


                                    }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Add Document Failure:", "Error adding document", e);
                                            }
                                        });

                                // (3) Bring user to the Main Menu
                                Toast.makeText(getApplicationContext(), "successfully registered!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                // Show registration error (not covered in the checks done through above)
                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                Toast.makeText(getApplicationContext(), "Registration Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}