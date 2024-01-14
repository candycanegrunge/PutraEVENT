package com.putra.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


//TODO:
// FUNCTIONS for Homepage with event list will come here [this is for both the admin and attendee view]
public class HomePage extends AppCompatActivity {
    private static boolean isAdmin = false;
    private RecyclerView eventRV;
    private ArrayList<event> eventArrayList;
    private EventRVAdapter eventRVAdapter;
    private FirebaseFirestore db;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageButton navigBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        eventRV = findViewById(R.id.recyclerViewEvents);
        db = FirebaseFirestore.getInstance();
        eventArrayList = new ArrayList<>();
        eventRV.setHasFixedSize(true);
        eventRV.setLayoutManager(new LinearLayoutManager(this));

        eventRVAdapter = new EventRVAdapter(eventArrayList, this);
        eventRV.setAdapter(eventRVAdapter);

        getToken();

        navigBtn = findViewById(R.id.navButton);
        // TODO: USE IF ELSE TO SHOW OR HIDE THE 'CREATE EVENTS'

        navigBtn.setOnClickListener(v -> {
            checkRole(new RoleCheckCallback() {
                @Override
                public void onRoleCheckCompleted(boolean isAdmin) {
                    Class<?> targetClass;

                    if (isAdmin) {
                        targetClass = HomeNav_Admin.class;
                    } else {
                        targetClass = HomeNav_Attendee.class;
                    }

                    Intent nav_intent = new Intent(HomePage.this, targetClass);
                    nav_intent.putExtra("openDrawer", true); // Pass a flag to open the drawer
                    startActivity(nav_intent);
                    finish(); // Optional - finishes the current activity to prevent going back to it on back press

                }
            });
        });

        db.collection("event").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            event c = d.toObject(event.class);
                            eventArrayList.add(c);
                        }
                        eventRVAdapter.notifyDataSetChanged();

                        // Set click listener for each item in the RecyclerView
                        eventRVAdapter.setOnItemClickListener((position, v) -> {
                            // Retrieve the document ID of the clicked event
                            Intent intent = new Intent(HomePage.this, view_specific_event.class);
                            String selectedEventDocumentId = list.get(position).getId();
                            intent.putExtra("eventDocumentId", selectedEventDocumentId);

                            // Call the method to handle the click event
                            onEventClick(selectedEventDocumentId);
                        });
                    } else {
                        Toast.makeText(HomePage.this, "No data found in database", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(HomePage.this, "Failed to get data.", Toast.LENGTH_SHORT).show();
                });



    }

    public interface RoleCheckCallback {
        void onRoleCheckCompleted(boolean isAdmin);
    }

    // This method is to check the role of the user signed in
    public void checkRole(RoleCheckCallback callback) {
        AtomicBoolean isAdminFlag = new AtomicBoolean(false);

        String curr_uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DocumentReference docRef = db.collection("users").document(curr_uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    isAdminFlag.set(Objects.equals(role, "admin"));
                    callback.onRoleCheckCompleted(isAdminFlag.get());

                    // Create a log to check the role
                    Log.d("TAG", "Role: " + role);
                    Log.d("TAG", "isAdmin: " + isAdminFlag);
                }
            }
        });
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

    // Method to handle the click event on a specific event
    private void onEventClick(String selectedEventDocumentId) {
        // Intent to navigate to the register_event activity
        Intent intent = new Intent(HomePage.this, view_specific_event.class);
        // Pass the selected event's document ID to the register_event activityF
        intent.putExtra("eventDocumentId", selectedEventDocumentId);
        // Start the register_event activity
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

////////////////////
}

