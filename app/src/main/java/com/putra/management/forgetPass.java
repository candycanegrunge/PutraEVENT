package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.putra.management.HomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

//TODO
// Forget password actions

public class forgetPass extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        EditText emailAddressTarget = findViewById(R.id.emailEnter);
        MaterialButton recover_btn = findViewById(R.id.recoverpass_btn);
        ImageButton back_forget_signin_arrow = findViewById(R.id.backBtn_forgetPass_signIn);

        recover_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAddress(emailAddressTarget.getText().toString());
            }
        });

        back_forget_signin_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goback_SignIn = new Intent(forgetPass.this, MainActivity.class);
                startActivity(goback_SignIn);
                finish(); // Optional - finishes the current activity to prevent going back to it on back press
            }
        });
    }

    // Check the email address entered is in the Firebase Auth database or not
    // If it is, send a recovery email to the email address
    // If it is not, display a toast message saying that the email address is not registered
    public void checkEmailAddress(String emailAddress) {
        if (!emailAddress.contains("@student.upm.edu.my")) {
            Toast.makeText(forgetPass.this, "Please insert the valid student email", Toast.LENGTH_SHORT).show();
        }
        else {
            // Check if the email address is registered in the Firebase Auth database
            mAuth.sendPasswordResetEmail(emailAddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(forgetPass.this, "Recovery email sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(forgetPass.this, "Email entered is not registered", Toast.LENGTH_SHORT).show();
                    }
            });
        }
    }
}
