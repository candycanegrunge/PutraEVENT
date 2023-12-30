package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.putra.management.HomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//TODO
// User Registration - SIGN UP
public class userRegister extends AppCompatActivity {
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextMatric;
    private EditText editTextPhone;
    private EditText editTextEnterPass;
    private EditText editTextConfirmPass;
    private EditText editTextYearStudy;
    private EditText editTextFaculty;
    private EditText editTextCourse;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        editTextFirstName = findViewById(R.id.firstname);
        editTextLastName = findViewById(R.id.lastname);
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextMatric = findViewById(R.id.matric);
        editTextPhone = findViewById(R.id.phone);
        editTextEnterPass = findViewById(R.id.enterpass);
        editTextConfirmPass = findViewById(R.id.confirmpass);
        editTextYearStudy = findViewById(R.id.yearStudy);
        editTextFaculty = findViewById(R.id.faculty);
        editTextCourse = findViewById(R.id.course);
        submitButton = findViewById(R.id.submit_btn);

        // Add functionality for the submit button or any other operations you need
        // For example:
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action upon button click
            }
        });
    }

    // ... (other methods or functions)
}
