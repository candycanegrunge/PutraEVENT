package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class CreateEvent_Admin extends AppCompatActivity {

    private ImageButton backButton_EventCreate_Home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_admin);

        backButton_EventCreate_Home = findViewById(R.id.backBtn_createEvt_Home);

        backButton_EventCreate_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToHome = new Intent(CreateEvent_Admin.this, HomePage.class);
                startActivity(backToHome);
                finish(); // Optional - finishes the current activity to prevent going back to it on back press
            }
        });

    //////
    }
}