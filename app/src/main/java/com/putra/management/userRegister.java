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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.putra.management.HomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//TODO
// User Registration - SIGN UP
public class userRegister extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;

    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MATRIC = "matric";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_YEARSTUDY = "yearStudy";
    private static final String KEY_FACULTY = "faculty";
    private static final String KEY_COURSE = "course";

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

        MaterialButton submitButton = findViewById(R.id.submit_btn);
        ImageButton backToSignInButton = findViewById(R.id.backBtn_signUp_signIn);

        // Add functionality for the submit button or any other operations you need
        // For example:
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action upon button click
                if (validateInput()) {
                    registerNewUser(editTextEmail.getText().toString(), editTextEnterPass.getText().toString());
                    saveUserDetail(mapUserDetail());
                }
            }
        });

        backToSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToSignIn = new Intent(userRegister.this, MainActivity.class);
                startActivity(backToSignIn);
                finish(); // Optional - finishes the current activity to prevent going back to it on back press
            }
        });
    }

    // To check if the user's input is valid
    public boolean validateInput() {
        boolean status = false;
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String matric = editTextMatric.getText().toString();
        String phone = editTextPhone.getText().toString();
        String enterPass = editTextEnterPass.getText().toString();
        String confirmPass = editTextConfirmPass.getText().toString();
        String yearStudy = editTextYearStudy.getText().toString();
        String faculty = editTextFaculty.getText().toString();
        String course = editTextCourse.getText().toString();

        // Check if all fields are filled
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                matric.isEmpty() || phone.isEmpty() || enterPass.isEmpty() || confirmPass.isEmpty() ||
                yearStudy.isEmpty() || faculty.isEmpty() || course.isEmpty()) {
            Toast.makeText(userRegister.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
        // Check if email is valid
        else if (!email.matches("^\\d{6}@student.upm.edu.my")) {
            Toast.makeText(userRegister.this, "Please insert the valid student email", Toast.LENGTH_SHORT).show();
        }
        // Check if password is valid
        else if (enterPass.length() < 6) {
            Toast.makeText(userRegister.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        // Check if password and confirm password is the same
        else if (!enterPass.equals(confirmPass)) {
            Toast.makeText(userRegister.this, "Password and confirm password must be the same", Toast.LENGTH_SHORT).show();
        }
        // TODO: Need to add a hints at the side of the text field to tell the user what is the format of the input for phone
        else if (!phone.matches("^\\d{10,11}$")) {
            Toast.makeText(userRegister.this, "Phone number must be in the format 01234567890 or 0123456789", Toast.LENGTH_SHORT).show();
        }
        // Matric number should be 6 digits
        else if (matric.length() != 6) {
            Toast.makeText(userRegister.this, "Matric number must be 6 digits", Toast.LENGTH_SHORT).show();
        }
        else
            status = true;

        return status;
    }

    // Map the user's input to a Map object
    public Map<String, Object> mapUserDetail() {
        Map<String, Object> userDetail = new HashMap<>();

        userDetail.put(KEY_FIRSTNAME, editTextFirstName.getText().toString().toUpperCase());
        userDetail.put(KEY_LASTNAME, editTextLastName.getText().toString().toUpperCase());
        userDetail.put(KEY_USERNAME, editTextUsername.getText().toString());
        userDetail.put(KEY_EMAIL, editTextEmail.getText().toString());
        userDetail.put(KEY_MATRIC, editTextMatric.getText().toString());
        userDetail.put(KEY_PHONE, editTextPhone.getText().toString());
        userDetail.put(KEY_YEARSTUDY, editTextYearStudy.getText().toString());
        userDetail.put(KEY_FACULTY, editTextFaculty.getText().toString());
        userDetail.put(KEY_COURSE, editTextCourse.getText().toString());
        // All user registered via the app will be student
        // Admin need to be registered manually via Firebase Console
        userDetail.put("role", "student");

        return userDetail;
    }

    // Register user's email and password to Firebase Authentication
    public void registerNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(userRegister.this, "Failed to register user: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            // TODO: Add what to do next
                        }
                    }
                });
    }

    // Store user's detail to Firestore Database
    private void saveUserDetail(Map<String, Object> userDetail) {
        // Add a new document with a generated ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(Objects.requireNonNull(userDetail.get(KEY_MATRIC)).toString())
                .set(userDetail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(userRegister.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(userRegister.this, "Error saving user detail", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                        // TODO: Maybe just prompt check internet connection/try again
                    }
                });
    }
    // ... (other methods or functions)
}
