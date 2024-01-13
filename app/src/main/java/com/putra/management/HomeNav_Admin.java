package com.putra.management;

import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeNav_Admin extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private NavigationView navigationView;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_nav_admin);

        navigationView = findViewById(R.id.nav_view_admin);
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
                logout();
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

        // Prevent closing the drawer on touch outside
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawer.setClickable(true);

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(android.view.View drawerView) {
                super.onDrawerClosed(drawerView);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(HomeNav_Admin.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeNav_Admin.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
