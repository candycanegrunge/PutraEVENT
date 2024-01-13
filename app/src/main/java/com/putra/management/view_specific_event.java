package com.putra.management;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class view_specific_event extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_specific_event);

        // Retrieve data from the intent
        Intent intent = getIntent();
        String eventTitle = intent.getStringExtra("eventTitle");
        String eventDate = intent.getStringExtra("eventDate");
        String eventStartTime = intent.getStringExtra("eventStartTime");
        String eventEndTime = intent.getStringExtra("eventEndTime");
        String eventVenue = intent.getStringExtra("eventVenue");
        String eventDescription = intent.getStringExtra("eventDescription");
        String eventSpeaker = intent.getStringExtra("eventSpeaker");
        String eventOrganizer = intent.getStringExtra("eventOrganizer");
        String eventSeat = intent.getStringExtra("eventSeat");
        String eventAvailableSeat = intent.getStringExtra("eventAvailableSeat");
        String eventImageUrl = intent.getStringExtra("eventImageUrl");

        // Set the values to display in view_specific_event.xml
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView startTimeTextView = findViewById(R.id.startTimeTextView);
//        TextView endTimeTextView = findViewById(R.id.endTimeTextView);
        TextView venueTextView = findViewById(R.id.venueTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView speaker_nameTextView = findViewById(R.id.speaker_nameTextView);
        TextView organizerTextView = findViewById(R.id.organizerTextView);
        TextView seatTextView = findViewById(R.id.seatTextView);
//        TextView availableSeatTextView = findViewById(R.id.availableSeatTextView);


        ImageView imageView = findViewById(R.id.imageView);

        titleTextView.setText(eventTitle);
        dateTextView.setText(eventDate);
        descriptionTextView.setText(eventDescription);
        speaker_nameTextView.setText(eventSpeaker);
        startTimeTextView.setText(eventStartTime);
//        endTimeTextView.setText(eventEndTime);
        venueTextView.setText(eventVenue);
        descriptionTextView.setText(eventDescription);
        speaker_nameTextView.setText(eventSpeaker);
        organizerTextView.setText(eventOrganizer);
        seatTextView.setText( eventSeat);
//        availableSeatTextView.setText( eventAvailableSeat);

        Picasso.get().load(eventImageUrl).into(imageView);
    }
}
