package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.putra.management.HomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

///////////////////////////////////////////////////////////////////
// SIGN IN PAGE

public class MainActivity extends AppCompatActivity {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private MaterialButton submit_btn;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        submit_btn = findViewById(R.id.submit_btn);
    }

    // To write data to the database, call the set() method, passing in a Map of key-value pairs.
    public void saveInfo(View v) {
        String username = editTextUsername.getText().toString();
        // For email registration, is the email has ******@student.upm.edu.my ==> No need request mail address from user, just matric number
        String password = editTextPassword.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put(KEY_USERNAME, username);
        user.put(KEY_PASSWORD, password);

        // Add user to database, handle both success and failure
        db.collection("users").document(username).set(user)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this, "User added to database", Toast.LENGTH_SHORT).show();
                    Log.d("SUCCESS", "User added to database");

                    openHome(); // if Successful, will go to 'next page' --- Home page
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MainActivity.this, "Error adding user to database", Toast.LENGTH_SHORT).show();
                    Log.d("FAIL", "Error adding user to database");
                }
            }
        );



    }


// The class for the home page options (non-admin)
public void openHome() {
        Intent open_homepage= new Intent(this, HomePage.class);
        startActivity(open_homepage);
    }
}