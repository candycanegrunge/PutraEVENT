package com.putra.management;

import android.media.metrics.Event;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.*;


//TODO:
// FUNCTIONS for Homepage with event list will come here [this is for both the admin and attendee view]

public class HomePage extends AppCompatActivity {
    private RecyclerView eventRV;
    private ArrayList<Event> eventList; // List to hold event data
    private com.example.myapptest.EventAdapter eventAdapter; // Adapter for the RecyclerView
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageButton navigBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        RecyclerView eventRV;
        eventList = new ArrayList<>();

        // Initialize RecyclerView and its adapter
        eventRV = findViewById(R.id.recyclerViewEvents);

        eventRV.setHasFixedSize(true);
        eventRV.setLayoutManager(new LinearLayoutManager(this));

        eventAdapter = new com.example.myapptest.EventAdapter(eventList, this);
        eventRV.setAdapter(eventAdapter);

        getToken();

        navigBtn = findViewById(R.id.navButton);
        // TODO: USE IF ELSE TO SHOW OR HIDE THE 'CREATE EVENTS'

        navigBtn.setOnClickListener(v -> {
            boolean adminFlag = getIntent().getBooleanExtra("isAdmin", false);

            Class<?> targetClass;
            if (adminFlag) {
                targetClass = HomeNav_Admin.class;
            } else {
                targetClass = HomeNav_Attendee.class;
            }

            Intent nav_intent = new Intent(HomePage.this, targetClass);

            nav_intent.putExtra("openDrawer", true); // Pass a flag to open the drawer
            startActivity(nav_intent);
            finish(); // Optional - finishes the current activity to prevent going back to it on back press
        });



        // Fetch event data from Firestore and populate the eventList
        fetchEventData();

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
        // Create a new user with a first and last name
        Map<String, Object> token_m = new HashMap<>();
        token_m.put("token", token);

        // Get the document with the matching uid
        DocumentReference docRefUser = db.collection("users").
                document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the document exists, update the token
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        db.collection("users")
                                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                .update(token_m)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "Token updated successfully");
                                        Log.d("TAG", "Token: " + token);
                                    }
                                });

                        // If the user is an engineering student, update the token in the engineering collection
                        if (Objects.equals(document.get("faculty"), "Faculty of Engineering")) {
                            captureUserTokenToTokenCollection("engineering", token);
                        } else if (Objects.equals(document.get("faculty"), "Faculty of Design and Architecture")) {
                            captureUserTokenToTokenCollection("design_and_architecture", token);
                        }
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }


    // Capture the user token to a dedicated collection for each faculty
    // This is to allow the admin to send notification to the users of a specific faculty
    // FIXME:
    // Currently not handling the case where a new collection will be created if the
    // collection does not exist
    private void captureUserTokenToTokenCollection(String collectionName, String token) {
        DocumentReference docRefToken = db.collection(collectionName).
                document("user_list");

        docRefToken.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the document exists, update the token
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Replace "arrayFieldName" with the name of the field you want to read
                        List<String> user_token_arr = (List<String>) document.get("user_token");
                        // Check whether the token already exists in the array
                        if (user_token_arr.contains(token)) {
                            return;
                        }
                        // Add the new token to the array
                        user_token_arr.add(token);
                        // Update the array in the database
                        docRefToken.update("user_token", user_token_arr)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void unused) {
                                                              Log.d("TAG", "Token updated successfully");
                                                              Log.d("TAG", "Token: " + token);
                                                          }
                                                      }
                                );
                        Log.d("TAG", "Array field value: " + user_token_arr);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    //TODO: Get info from database
    private void fetchEventData() {
        // Fetch event data from Firestore and add it to eventList
        // Use Firestore queries to retrieve the data and populate eventList
        // For example:
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Create an Event object from document data and add it to eventList
                            // Event event = document.toObject(Event.class); //Class requires API level 31 (current min is 24): `android.media.metrics.Event`
                            //eventList.add(event);
                        }
                        // Notify adapter about data changes
                        //eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }
}

