package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


//TODO:
// FUNCTIONS for Homepage with event list will come here [this is for both the admin and attendee view]

public class HomePage extends AppCompatActivity {

    private ImageButton navigBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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
}