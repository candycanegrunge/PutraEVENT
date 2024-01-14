package com.putra.management;

import static com.google.common.io.Files.getFileExtension;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CreateEvent_Admin extends AppCompatActivity {
    // Data field for firestore
    private static final String KEY_TITLE           = "title";
    private static final String KEY_DATE            = "date";
    private static final String KEY_START_TIME      = "start_time";
    private static final String KEY_END_TIME        = "end_time";
    private static final String KEY_IMAGE           = "image";
    private static final String KEY_VENUE           = "venue";
    private static final String KEY_SPEAKER_NAME    = "speaker_name";
    private static final String KEY_ORGANIZER       = "organizer";
    private static final String KEY_DESCRIPTION     = "description";
    private static final String KEY_SEAT            = "seat";
    private static final String KEY_USER_REGISTER   = "user_register"; // New field for the array


    private TextInputEditText titleEnter;
    private TextInputEditText descEnter;
    private TextInputEditText datePickerEditText;
    private TextInputEditText startTimeEditText;
    private TextInputEditText endTimeEditText;
    private TextInputEditText venueEnter;
    private TextInputEditText speakerNameEditText;
    private TextInputEditText organiserNameEditText;
    private TextInputEditText totalSeatsEditText;

    private MaterialButton eventSubmit;
   
    private ImageButton uploadImageButton;
    private ImageView uploadedImageView;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;
    private ImageView imageView;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_admin);

        ImageButton backButton_EventCreate_Home = findViewById(R.id.backBtn_createEvt_Home);

        titleEnter = findViewById(R.id.eventTitle);
        descEnter = findViewById(R.id.descriptionEditText);
        datePickerEditText = findViewById(R.id.datePickerEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        venueEnter = findViewById(R.id.venue);
        speakerNameEditText = findViewById(R.id.speakerName);
        organiserNameEditText = findViewById(R.id.organiser);
        totalSeatsEditText = findViewById(R.id.totalSeats);

        eventSubmit = findViewById(R.id.submit_btn_eventCreate);

        uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadedImageView = findViewById(R.id.uploadedImageView);

        uploadImageButton.setOnClickListener(v -> openFileChooser());
        // Change the image size to 50dp
        adjustLayoutParameters();

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
                // TODO: Check the schedule before add the event to Firestore
                checkAndCreateEvent();
            }
        });
    }

    // TODO: Need to add the data extraction for all fields and also take the 'total seats' from here to use in the other functions
    public void showDatePicker(View view) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTheme(R.style.CustomMaterialCalendarTheme);

        // Set the initial selection to today
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());

        // Restrict past and future dates
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setStart(MaterialDatePicker.todayInUtcMilliseconds());

        builder.setCalendarConstraints(constraintsBuilder.build());

        datePicker.addOnPositiveButtonClickListener(selection -> {
            long selectedDate = selection;
            long currentDate = MaterialDatePicker.todayInUtcMilliseconds();

            if (selectedDate >= currentDate) {
                String formattedDate = formatDate(selectedDate);
                datePickerEditText.setText(formattedDate);
            } else {
                Toast.makeText(this, "Please select a future date", Toast.LENGTH_SHORT).show();
            }
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
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setInputMode(com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setTitleText("Pick Time")
                    .build();

            timePicker.addOnPositiveButtonClickListener(dialog -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Check if the selected time is within the range of 8 AM to 5 PM
                if (hour < 8 || (hour == 8 && minute < 0) || hour > 17) {
                    Toast.makeText(CreateEvent_Admin.this, "Select a time between 8 AM and 5 PM", Toast.LENGTH_SHORT).show();
                } else {
                    // Format the selected time correctly
                    String formattedTime = String.format(Locale.getDefault(), "%02d%02d", hour, minute);

                    // Set the formatted time in the EditText
                    editText.setText(formattedTime);
                }
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
            imageUri = data.getData();

            // Set the selected image to the ImageView
            uploadedImageView.setImageURI(imageUri);
            uploadedImageView.setVisibility(View.VISIBLE);
        }
    }

    private void adjustLayoutParameters() {
        // Get the current layout parameters of the ImageButton and ImageView
        ViewGroup.LayoutParams imageButtonParams = uploadImageButton.getLayoutParams();
        ViewGroup.LayoutParams imageViewParams = uploadedImageView.getLayoutParams();

        // Update the height of both views to 50dp
        imageButtonParams.height = (int) getResources().getDimension(R.dimen.image_button_height);
        imageViewParams.height = (int) getResources().getDimension(R.dimen.image_button_height);

        // Set the layout parameters back to the views
        uploadImageButton.setLayoutParams(imageButtonParams);
        uploadedImageView.setLayoutParams(imageViewParams);
    }

    // Write event data to the database
    public void saveEventInfo() {
        Map<String, Object> event = new HashMap<>();

        String title = Objects.requireNonNull(titleEnter.getText()).toString();
        String date = Objects.requireNonNull(datePickerEditText.getText()).toString();
        String startTime = Objects.requireNonNull(startTimeEditText.getText()).toString();
        String endTime = Objects.requireNonNull(endTimeEditText.getText()).toString();
        String venue = Objects.requireNonNull(venueEnter.getText()).toString();
        String speakerName = Objects.requireNonNull(speakerNameEditText.getText()).toString();
        String organizer = Objects.requireNonNull(organiserNameEditText.getText()).toString();
        String description = Objects.requireNonNull(descEnter.getText()).toString();
        int seat = Integer.parseInt(Objects.requireNonNull(totalSeatsEditText.getText()).toString());

        // Image upload handling
        String folderName = "PutraEVENT";
        StorageReference fileRef = reference.child(folderName + "/" + title + getFileExtension(String.valueOf(imageUri)));

        if (imageUri != null) {
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(CreateEvent_Admin.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                // Get the download URL of the uploaded image
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageUriFirebase) {
                        // Add the download URL to the event object
                        event.put(KEY_IMAGE, imageUriFirebase.toString());
                        Log.d("TAG", "Download URL: " + imageUri.toString());

                        // Add other event details to the event object
                        event.put(KEY_TITLE, title);
                        event.put(KEY_DATE, date);
                        event.put(KEY_START_TIME, startTime);
                        event.put(KEY_END_TIME, endTime);
                        event.put(KEY_VENUE, venue);
                        event.put(KEY_SPEAKER_NAME, speakerName);
                        event.put(KEY_ORGANIZER, organizer);
                        event.put(KEY_DESCRIPTION, description);
                        event.put(KEY_SEAT, seat);
                        event.put(KEY_USER_REGISTER, new ArrayList<String>());

                        // Add event to the "events" collection in the database
                        db.collection("event").add(event)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String documentId = documentReference.getId();
                                    Toast.makeText(CreateEvent_Admin.this, "Event added to database", Toast.LENGTH_SHORT).show();
                                    saveToScheduleCollection(documentId);
                                    Log.d("SUCCESS", "Event added to database");
                                    Intent backToAdminHomeNav = new Intent(CreateEvent_Admin.this, HomePage.class);
                                    startActivity(backToAdminHomeNav);
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(CreateEvent_Admin.this, "Error adding event to database", Toast.LENGTH_SHORT).show();
                                    Log.d("FAIL", "Error adding event to database");
                                }
                            });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateEvent_Admin.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(CreateEvent_Admin.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(CreateEvent_Admin.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndCreateEvent() {
        String date = datePickerEditText.getText().toString();
        String startTime = startTimeEditText.getText().toString();
        String endTime = endTimeEditText.getText().toString();
        String venue = venueEnter.getText().toString();

        db.collection("schedule")
                .whereEqualTo(KEY_DATE, date)
                .whereEqualTo(KEY_VENUE, venue)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Check if any documents match the query
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Check for overlapping start/end times
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String existingStartTime = document.getString(KEY_START_TIME);
                            String existingEndTime = document.getString(KEY_END_TIME);

                            // Check for time overlap using isTimeOverlap function
                            if (isTimeOverlap(startTime, endTime, existingStartTime, existingEndTime)) {
                                // There is an overlap, inform the user
                                Toast.makeText(CreateEvent_Admin.this, "Event overlaps with an existing event", Toast.LENGTH_SHORT).show();
                                return; // Exit the method as there is an overlap
                            }
                        }
                    }

                    // No overlapping events, proceed to save the new event
                    saveEventInfo();
                })
                .addOnFailureListener(e -> {
                    // Handle failures in Firestore query
                    Toast.makeText(CreateEvent_Admin.this, "Error checking existing events", Toast.LENGTH_SHORT).show();
                    Log.d("FAIL", "Error checking existing events", e);
                });
    }

    private boolean isTimeOverlap(String newStartTime, String newEndTime, String existingStartTime, String existingEndTime) {
        int start1 = Integer.parseInt(newStartTime);
        int end1 = Integer.parseInt(newEndTime);

        int start2 = Integer.parseInt(existingStartTime);
        int end2 = Integer.parseInt(existingEndTime);

        // if overlap, then true and event cannot be created
        return (start1 < end2 && end1 > start2);
    }

    private void saveToScheduleCollection(String documentId) {
        String date = datePickerEditText.getText().toString();
        String startTime = startTimeEditText.getText().toString();
        String endTime = endTimeEditText.getText().toString();
        String venue = venueEnter.getText().toString();

        Map<String, Object> scheduleEvent = new HashMap<>();
        scheduleEvent.put(KEY_DATE, date);
        scheduleEvent.put(KEY_START_TIME, startTime);
        scheduleEvent.put(KEY_END_TIME, endTime);
        scheduleEvent.put(KEY_VENUE, venue);

        // Use documentId as the document reference
        db.collection("schedule").document(documentId).set(scheduleEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SUCCESS", "Details added to schedule collection");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d("FAIL", "Error adding details to schedule collection", e);
                    }
                });
    }
}
