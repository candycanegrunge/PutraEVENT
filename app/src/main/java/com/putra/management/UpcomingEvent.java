package com.putra.management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FieldPath;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpcomingEvent extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    private RecyclerView eventRV;
    private ArrayList<event> eventArrayList;
    private EventRVAdapter eventRVAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcoming_event);

        // need to chnage the button name
        ImageButton backButton_EventCreate_Home = findViewById(R.id.backBtn_createEvt_Home);

        db = FirebaseFirestore.getInstance();
        Log.d("TAG", "userID " + userId);

        // Retrieve and display user events
        displayUserEvents();

        backButton_EventCreate_Home.setOnClickListener(v -> {
            Intent backToAdminHomeNav = new Intent(UpcomingEvent.this, HomeNav_Admin.class);
            startActivity(backToAdminHomeNav);
            finish(); // Optional - finishes the current activity to prevent going back to it on back press
        });

    }
    private void displayUserEvents() {

        eventRV = findViewById(R.id.recyclerViewEvents);
        db = FirebaseFirestore.getInstance();
        eventArrayList = new ArrayList<>();
        eventRV.setHasFixedSize(true);
        eventRV.setLayoutManager(new LinearLayoutManager(this));

        eventRVAdapter = new EventRVAdapter(eventArrayList, this);
        eventRV.setAdapter(eventRVAdapter);

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        List<String> eventIds = (List<String>) documentSnapshot.get("event_register");

                        if (eventIds != null && !eventIds.isEmpty()) {
                            // Process the data from the event_register array
                            for (String eventId : eventIds) {
                                showToast("Event ID: " + eventId);

                                db.collection("event").document(eventId).get()
                                    .addOnSuccessListener(eventSnapshot -> {
                                        if (eventSnapshot.exists()) {
                                            event c = eventSnapshot.toObject(event.class);
                                            eventArrayList.add(c);

                                        } else {
                                            // Handle the case where the event is not found in "events" collection
                                            showToast("Event with ID " + eventId + " not found in database");
                                        }
                                        eventRVAdapter.notifyDataSetChanged();

                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the failure to fetch event details
                                        showToast("Failed to fetch event details for eventId: " + eventId);
                                    });
                            }

                            // Set click listener for each item in the RecyclerView
                            eventRVAdapter.setOnItemClickListener((position, v) -> {
                                // Retrieve the document ID of the clicked event
                                Intent intent = new Intent(UpcomingEvent.this, view_specific_event.class);
                                String selectedEventDocumentId = eventIds.get(position);
                                intent.putExtra("eventDocumentId", selectedEventDocumentId);

                                // Call the method to handle the click event
                                onEventClick(selectedEventDocumentId);
                            });
                        }
                        else {
                            showToast("No events registered.");
                        }
                    } else {
                        showToast("User document does not exist.");
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve user events: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onEventClick(String selectedEventDocumentId) {
        // Intent to navigate to the view_specific_event activity
        Intent intent = new Intent(UpcomingEvent.this, view_registered_event.class);
        // Pass the selected event's document ID to the view_specific_event activity
        intent.putExtra("eventDocumentId", selectedEventDocumentId);
        // Start the view_specific_event activity
        startActivity(intent);
    }
}
