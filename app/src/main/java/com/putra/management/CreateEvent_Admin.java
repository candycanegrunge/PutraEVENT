package com.putra.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateEvent_Admin extends AppCompatActivity {

    private TextInputEditText datePickerEditText;
    private ImageButton backButton_EventCreate_Home;
    private TextInputEditText startTimeEditText;
    private TextInputEditText endTimeEditText;
    private TextInputEditText venueEnter;
    private TextInputEditText totalSeatsEditText;
    private TextInputEditText organiserNameEditText;
    private TextInputEditText speakerNameEditText;
    private MaterialButton eventSubmit;

    private ImageButton uploadImageButton;
    private ImageView uploadedImageView;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_admin);

        backButton_EventCreate_Home = findViewById(R.id.backBtn_createEvt_Home);
        datePickerEditText = findViewById(R.id.datePickerEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        venueEnter = findViewById(R.id.venue);
        totalSeatsEditText = findViewById(R.id.totalSeats);
        organiserNameEditText = findViewById(R.id.organiser);
        speakerNameEditText = findViewById(R.id.speakerName);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadedImageView = findViewById(R.id.uploadedImageView);
        eventSubmit = findViewById(R.id.submit_btn_eventCreate);

        uploadImageButton.setOnClickListener(v -> openFileChooser());

        backButton_EventCreate_Home.setOnClickListener(v -> {
            Intent backToAdminHomeNav = new Intent(CreateEvent_Admin.this, HomeNav_Admin.class);
            startActivity(backToAdminHomeNav);
            finish(); // Optional - finishes the current activity to prevent going back to it on back press
        });

        datePickerEditText.setFocusable(false);
        datePickerEditText.setClickable(true);
        datePickerEditText.setOnClickListener(this::showDatePicker);


        setupTimePicker(startTimeEditText, "TIME_PICKER_TAG");
        setupTimePicker(endTimeEditText, "END_TIME_PICKER_TAG");

        //TODO: To add function for creating event after click submit, not just shows toast. Can add auth if all field is ar not full
        eventSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a toast message
                Toast.makeText(CreateEvent_Admin.this, "Created Event", Toast.LENGTH_SHORT).show();
            }
        });
        /////////// Sample
//        eventSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (fieldsAreFilled()) {
//                    // Perform action upon button click when fields are filled
//                    // Add your action logic here
//                } else {
//                    Toast.makeText(CreateEvent_Admin.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//// Function to check if fields are filled
//        private boolean fieldsAreFilled() {
//            // Add your logic here to check if fields are filled
//            // Return true if all fields are filled; otherwise, return false
//        }

        /////////////
    }

    // TODO: Need to add the data extraction for all fields and also take the 'total seats' from here to use in the other functions
    public void showDatePicker(View view) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTheme(R.style.CustomMaterialCalendarTheme);

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = formatDate(selection);
            datePickerEditText.setText(formattedDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
    }

    private String formatDate(Long dateInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formatter.format(new Date(dateInMillis));
    }

    private void setupTimePicker(TextInputEditText editText, String tag) {
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(v -> {
            MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
            builder.setTimeFormat(TimeFormat.CLOCK_12H);
            MaterialTimePicker timePicker = builder.build();

            timePicker.addOnPositiveButtonClickListener(dialog -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Format the selected time
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                        hour % 12 == 0 ? 12 : hour % 12, minute, hour < 12 ? "AM" : "PM");

                // Set the formatted time in the EditText
                editText.setText(formattedTime);
            });

            timePicker.show(getSupportFragmentManager(), tag);
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Set the selected image to the ImageView
            uploadedImageView.setImageURI(imageUri);
            uploadedImageView.setVisibility(View.VISIBLE);
        }
    }

///////////////
}