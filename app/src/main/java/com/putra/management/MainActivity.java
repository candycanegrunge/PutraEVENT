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


// Keeping this temporarily to copy paste, I am too damn lazy to type that out
// 206730@student.upm.edu.my
// niprog-6wAtnu-gocquj


// SIGN IN PAGE
///////////////////////////////

public class MainActivity extends AppCompatActivity {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private MaterialButton submit_btn;
    private TextView signUpTextView;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        submit_btn = findViewById(R.id.submit_btn);
        signUpTextView = findViewById(R.id.signup);

        // Set an onClickListener for the signUpTextView to navigate to the registration page
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister(); // Call the userRegister() method to open the registration page
            }
        });
    }

    // To write data to the database, call the set() method, passing in a Map of key-value pairs.
    public void loginAuth(View v) {
        String email = editTextUsername.getText().toString();
        // For email registration, is the email has ******@student.upm.edu.my ==> No need request mail address from user, just matric number
        String password = editTextPassword.getText().toString();

        // Check if email and password is empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if email is valid
        else if (!email.contains("@student.upm.edu.my")) {
            Toast.makeText(MainActivity.this, "Please insert the valid student email", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if password is valid
        else if (password.length() < 6) {
            Toast.makeText(MainActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Check if email and password is valid
            FirebaseUser currentUser = mAuth.getCurrentUser();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onSuccess(com.google.firebase.auth.AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            openHome();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(Exception e) {
                           Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                       }
                    });
        }
    }




    // The class for the home page options (admin & user)
    public void openHome() {
        Intent open_homepage= new Intent(this, HomePage.class);
        startActivity(open_homepage);
    }

    // The class for the sign up / regs of user
    public void userRegister() {
        Intent open_signup = new Intent(this, userRegister.class);
        startActivity(open_signup);
    }

/////////////////////////////
}
