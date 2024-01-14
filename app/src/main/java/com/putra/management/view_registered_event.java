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

public class view_registered_event extends AppCompatActivity {
//    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String eventDocumentId;
    private FirebaseFirestore db;
//    private String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_specific_registered_event);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        eventDocumentId = intent.getStringExtra("eventDocumentId");

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


                } else {
                    // Handle the case where the document doesn't exist
                    Toast.makeText(view_registered_event.this, "Event details not found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                // Handle failures in Firestore query
                Toast.makeText(view_registered_event.this, "Failed to retrieve event details", Toast.LENGTH_SHORT).show();
            });
    }
}