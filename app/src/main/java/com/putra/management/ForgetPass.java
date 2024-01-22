package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;

//TODO
// Forget password actions

public class ForgetPass extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        TextInputEditText emailAddressTarget = findViewById(R.id.emailEnter);
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
                Intent goback_SignIn = new Intent(ForgetPass.this, MainActivity.class);
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
            Toast.makeText(ForgetPass.this, "Please insert the valid student email", Toast.LENGTH_SHORT).show();
        }
        else {
            // Check if the email address is registered in the Firebase Auth database
            mAuth.sendPasswordResetEmail(emailAddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ForgetPass.this, "Recovery email sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ForgetPass.this, "Email entered is not registered", Toast.LENGTH_SHORT).show();
                    }
            });
        }
    }
}
