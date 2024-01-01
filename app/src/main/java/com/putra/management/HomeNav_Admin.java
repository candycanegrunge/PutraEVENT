package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;

public class HomeNav_Admin extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_nav_admin);

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

        // Retrieve the flag from the intent
        boolean shouldOpenDrawer = getIntent().getBooleanExtra("openDrawer", false);

        // Check if the flag is true and open the drawer --- the navigation bar
        if (shouldOpenDrawer) {
            drawer.openDrawer(GravityCompat.START);
        }


        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Open Home Page
                Intent homeIntent = new Intent(HomeNav_Admin.this, HomePage.class);
                startActivity(homeIntent);
                finish();
            } else if (id == R.id.nav_savedEvents) {
                // Handle Saved Events click
            } else if (id == R.id.logout_Btn) {
                // Log out from app
                Intent logout = new Intent(HomeNav_Admin.this, MainActivity.class);
                startActivity(logout);
                finish();
            } else if (id == R.id.createEvents_Btn) {
                // Log out from app
                Intent createEvent = new Intent(HomeNav_Admin.this, CreateEvent_Admin.class);
                startActivity(createEvent);
                finish();
            }
            // Add more conditions for other menu items if needed

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}
