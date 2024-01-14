package com.putra.management;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.ebanx.swipebtn.SwipeButton;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class view_specific_event extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String eventDocumentId;
    private ImageButton backButton_EventRegis_Home;
    private FirebaseFirestore db;
    private String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_specific_event);

        backButton_EventRegis_Home = findViewById(R.id.backBtn_RegsEvt_Home);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        eventDocumentId = intent.getStringExtra("eventDocumentId");

        backButton_EventRegis_Home.setOnClickListener(v -> {
            Intent backToAdminHomeNav = new Intent(view_specific_event.this, HomePage.class);
            startActivity(backToAdminHomeNav);
            finish(); // Optional - finishes the current activity to prevent going back to it on back press
        });

        // Perform a Firestore query to get detailed event information
        FirebaseFirestore.getInstance().collection("event").document(eventDocumentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventTitle = documentSnapshot.getString("title");
                        String eventDate = documentSnapshot.getString("date");
                        String eventStartTime = documentSnapshot.getString("start_time");
                        String eventEndTime = documentSnapshot.getString("end_time");
                        String eventVenue = documentSnapshot.getString("venue");
                        String eventDescription = documentSnapshot.getString("description");
                        String eventSpeaker = documentSnapshot.getString("speaker_name");
                        String eventOrganizer = documentSnapshot.getString("organizer");
                        String eventSeat = String.valueOf(documentSnapshot.getLong("seat"));
                        String eventImageUrl = documentSnapshot.getString("image");

                        // check the availability
                        int totalSeats = documentSnapshot.getLong("seat").intValue();
                        ArrayList<String> userRegister = (ArrayList<String>) documentSnapshot.get("user_register");
                        int availableSeats = totalSeats - userRegister.size();

                        // Set the values to display in view_specific_event.xml
                        TextView titleTextView = findViewById(R.id.titleTextView);
                        TextView dateTextView = findViewById(R.id.dateTextView);
                        TextView startTimeTextView = findViewById(R.id.startTimeTextView);
                        TextView venueTextView = findViewById(R.id.venueTextView);
                        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
                        TextView speaker_nameTextView = findViewById(R.id.speaker_nameTextView);
                        TextView organizerTextView = findViewById(R.id.organizerTextView);
                        TextView seatTextView = findViewById(R.id.seatTextView);

                        ImageView imageView = findViewById(R.id.imageView);

                        titleTextView.setText(eventTitle);
                        dateTextView.setText(eventDate);
                        descriptionTextView.setText(eventDescription);
                        speaker_nameTextView.setText(eventSpeaker);
                        startTimeTextView.setText(eventStartTime);
                        venueTextView.setText(eventVenue);
                        speaker_nameTextView.setText(eventSpeaker);
                        organizerTextView.setText(eventOrganizer);
                        seatTextView.setText(eventSeat);

                        Picasso.get().load(eventImageUrl).into(imageView);

                        // check condition
                        SwipeButton swipeButton = findViewById(R.id.swipe_btn_reg);

                        if(availableSeats > 0)
                        {
                            swipeButton.setOnStateChangeListener(active -> {
                                accessSpecificEventAndStoreUserId();
                            });
                        } else{
                            swipeButton.setEnabled(false);
                            Toast.makeText(view_specific_event.this, "Quote full", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Handle the case where the document doesn't exist
                        Toast.makeText(view_specific_event.this, "Event details not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failures in Firestore query
                    Toast.makeText(view_specific_event.this, "Failed to retrieve event details", Toast.LENGTH_SHORT).show();
                });
    }

    private void accessSpecificEventAndStoreUserId() {
        // Go to specific event document to store participant ID
        DocumentReference eventRef = db.collection("event").document(eventDocumentId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
//                int totalSeats = documentSnapshot.getLong("seat").intValue();
                ArrayList<String> userRegister = (ArrayList<String>) documentSnapshot.get("user_register");

                // If user_register is null or not initialized, create a new ArrayList
                if (userRegister == null) {
                    userRegister = new ArrayList<>();
                }

                // Check if the user ID is already present in the user_register array
                if (!userRegister.contains(userId)) {
                    userRegister.add(userId);

                    // Update the Firestore document with the new user_register array
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("user_register", userRegister);

                    eventRef.update(updateData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(view_specific_event.this, "User ID added to user_register", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(view_specific_event.this, "Failed to update user_register", Toast.LENGTH_SHORT).show();
                            });
                    DocumentReference registerRef = db.collection("users").document(userId);
                    updateFirestoreArray(registerRef, "event_register", eventDocumentId, "User ID added to event_register");
                } else {
                    Toast.makeText(view_specific_event.this, "You have already registered for this event", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateFirestoreArray(DocumentReference docRef, String arrayName, String itemId, String successMessage) {
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> array = (ArrayList<String>) documentSnapshot.get(arrayName);

                if (array == null) {
                    array = new ArrayList<>();
                }

                array.add(itemId);

                Map<String, Object> updateData = new HashMap<>();
                updateData.put(arrayName, array);

                docRef.update(updateData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(view_specific_event.this, successMessage, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(view_specific_event.this, "Failed to update " + arrayName, Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

}