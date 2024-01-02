package com.putra.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


//TODO:
// FUNCTIONS for Homepage with event list will come here [this is for both the admin and attendee view]

public class HomePage extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageButton navigBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getToken();

        navigBtn = findViewById(R.id.navButton);
        // TODO: USE IF ELSE TO SHOW OR HIDE THE 'CREATE EVENTS'

        navigBtn.setOnClickListener(v -> {
            Intent nav_adminbar = new Intent(HomePage.this, HomeNav_Admin.class);
            nav_adminbar.putExtra("openDrawer", true); // Pass a flag to open the drawer
            startActivity(nav_adminbar);
            finish(); // Optional - finishes the current activity to prevent going back to it on back press
        });
        ////////
    }

    // Get the token of the device
    private void getToken() {
        // Create token for Firebase Messaging
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    saveToken(token);
                }
            });
    }

    // Save the token into the database
    private void saveToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> token_m = new HashMap<>();
        token_m.put("token", token);

        // Get the document with the matching uid
        DocumentReference docRef = db.collection("users").
                document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the document exists, update the token
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    db.collection("users")
                            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                            .update(token_m)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "Token updated successfully");
                                    Log.d("TAG", "Token: " + token);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", e.toString());
                                }
                            });
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }
}