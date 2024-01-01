package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


// 206730@student.upm.edu.my
// niprog-6wAtnu-gocquj

//TODO:
// FUNCTIONS for Homepage with event list will come here [this is for both the admin and attendee view]

public class HomePage extends AppCompatActivity {

    private ImageButton logoutBtn;
    private ImageButton navigBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        logoutBtn = findViewById(R.id.logout_Btn);
        navigBtn = findViewById(R.id.navButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(HomePage.this, MainActivity.class);
                startActivity(logout);
                finish(); // Optional - finishes the current activity to prevent going back to it on back press
            }
        });

        navigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TO MODIFY TO OPEN NAV BAR - NEW ACTIVITY
//                Intent logout = new Intent(HomePage.this, MainActivity.class);
//                startActivity(logout);
//                finish(); // Optional - finishes the current activity to prevent going back to it on back press
            }
        });

    ////////
    }
}