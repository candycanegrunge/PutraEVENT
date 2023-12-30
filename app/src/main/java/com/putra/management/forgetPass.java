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
// Forget password actions

public class forgetPass extends AppCompatActivity {

    private EditText enterEmail_forgetPass;
    private MaterialButton recover_btn;
    private ImageButton back_forget_signin_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        enterEmail_forgetPass = findViewById(R.id.emailEnter);
        recover_btn = findViewById(R.id.recoverpass_btn);
        back_forget_signin_arrow = findViewById(R.id.backBtn_forgetPass_signIn);

        recover_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(forgetPass.this, "Recovery email sent", Toast.LENGTH_SHORT).show();
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

    ///////////////////////////
    }
}
