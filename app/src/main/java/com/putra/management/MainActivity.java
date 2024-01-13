package com.putra.management;

import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.putra.management.HomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


// SIGN IN PAGE
///////////////////////////////

interface LoginInterface {
    boolean emailAddressPasswordCheck(String email, String password);
}

public class MainActivity extends AppCompatActivity implements LoginInterface {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static Boolean isAdmin;

    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private MaterialButton submit_btn;
    private TextView signUpTextView;
    private TextView forgetPasswd;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This ID is referring to the one in the UI (xml files)
        editTextUsername = findViewById(R.id.loginemail);
        editTextPassword = findViewById(R.id.password);
        submit_btn = findViewById(R.id.submit_btn);
        signUpTextView = findViewById(R.id.signup);
        forgetPasswd = findViewById(R.id.forget);

        // Set an onClickListener for the signUpTextView to navigate to the registration page
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister(); // Call the userRegister() method to open the registration page
            }
        });

        forgetPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPass(); // Call the userRegister() method to open the registration page
            }
        });

        // Set an onClickListener for the submit button to handle login
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loginAuth(v); // Call the loginAuth() method to perform login
                openHome();
            }
        });

    }

    // This method is to check if the email is valid or not
    @Override
    public boolean emailAddressPasswordCheck(@NonNull String email, String password) {
        // Check if email and password is empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check if email is valid
        else if (!email.contains("@student.upm.edu.my")) {
            Toast.makeText(MainActivity.this, "Please insert the valid student email", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check if password is valid
        else if (password.length() < 6) {
            Toast.makeText(MainActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    // To write data to the database, call the set() method, passing in a Map of key-value pairs.
    public void loginAuth(View v) {
        String email = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (emailAddressPasswordCheck(email, password)) {
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
        Intent open_home= new Intent(this, HomePage.class);
        startActivity(open_home);
    }

    // This class for the forget password at sign in page
    public void forgetPass() {
        Intent open_forgetPassword= new Intent(this, forgetPass.class);
        startActivity(open_forgetPassword);
    }

    // The class for the sign up / regs of user
    public void userRegister() {
        Intent open_signup = new Intent(this, userRegister.class);
        startActivity(open_signup);
    }
/////////////////////////////
}
