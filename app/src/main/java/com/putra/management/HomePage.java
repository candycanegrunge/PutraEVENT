package com.putra.management;

import android.media.metrics.Event;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.*;


//TODO:
// FUNCTIONS for Homepage with event list will come here [this is for both the admin and attendee view]

public class HomePage extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton navigBtn;


    private RecyclerView recyclerView;
    //private EventAdapter eventAdapter; // Adapter for the RecyclerView
    private List<Event> eventList; // List to hold event data


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getToken();

        navigBtn = findViewById(R.id.navButton);
        // TODO: USE IF ELSE TO SHOW OR HIDE THE 'CREATE EVENTS'

        navigBtn.setOnClickListener(v -> {
            boolean adminFlag = getIntent().getBooleanExtra("isAdmin", false);

            Class<?> targetClass;
            if (adminFlag) {
                targetClass = HomeNav_Admin.class;
            } else {
                targetClass = HomeNav_Attendee.class;
            }

            Intent nav_intent = new Intent(HomePage.this, targetClass);

            nav_intent.putExtra("openDrawer", true); // Pass a flag to open the drawer
            startActivity(nav_intent);
            finish(); // Optional - finishes the current activity to prevent going back to it on back press
        });


        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        //eventAdapter = new EventAdapter(eventList);
        //recyclerView.setAdapter(eventAdapter);

        // Fetch event data from Firestore and populate the eventList
        fetchEventData();

        ////////208651@student.upm.edu.my
    }

    // Get the token of the device
    private void getToken() {
        // Create token for Firebase Messaging
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        saveToken(token);
                    }
                });
    }


    // Save the token into the database
    private void saveToken(String token) {
        // Create a new user with a first and last name
        Map<String, Object> token_m = new HashMap<>();
        token_m.put("token", token);

        // Get the document with the matching uid
        DocumentReference docRefUser = db.collection("users").
                document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the document exists, update the token
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        db.collection("users")
                                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                .update(token_m)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "Token updated successfully");
                                        Log.d("TAG", "Token: " + token);
                                    }
                                });

                        // If the user is an engineering student, update the token in the engineering collection
                        if (Objects.equals(document.get("faculty"), "Faculty of Engineering")) {
                            captureUserTokenToTokenCollection("engineering", token);
                        } else if (Objects.equals(document.get("faculty"), "Faculty of Design and Architecture")) {
                            captureUserTokenToTokenCollection("design_and_architecture", token);
                        }
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }


    // Capture the user token to a dedicated collection for each faculty
    // This is to allow the admin to send notification to the users of a specific faculty
    // FIXME:
    // Currently not handling the case where a new collection will be created if the
    // collection does not exist
    private void captureUserTokenToTokenCollection(String collectionName, String token) {
        DocumentReference docRefToken = db.collection(collectionName).
                document("user_list");

        docRefToken.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the document exists, update the token
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Replace "arrayFieldName" with the name of the field you want to read
                        List<String> user_token_arr = (List<String>) document.get("user_token");
                        // Check whether the token already exists in the array
                        if (user_token_arr.contains(token)) {
                            return;
                        }
                        // Add the new token to the array
                        user_token_arr.add(token);
                        // Update the array in the database
                        docRefToken.update("user_token", user_token_arr)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void unused) {
                                                              Log.d("TAG", "Token updated successfully");
                                                              Log.d("TAG", "Token: " + token);
                                                          }
                                                      }
                                );
                        Log.d("TAG", "Array field value: " + user_token_arr);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    //TODO: Get info from database
    private void fetchEventData() {
        // Fetch event data from Firestore and add it to eventList
        // Use Firestore queries to retrieve the data and populate eventList
        // For example:
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Create an Event object from document data and add it to eventList
                            // Event event = document.toObject(Event.class); //Class requires API level 31 (current min is 24): `android.media.metrics.Event`
                            //eventList.add(event);
                        }
                        // Notify adapter about data changes
                        //eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }
}



//TODO
/*
SAMPLE CHATGPT EVENT ADAPTER CODE ---- separate 'class' i think

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        // Bind event data to the views in the ViewHolder
        holder.eventTitle.setText(event.getTitle());
        holder.eventVenue.setText(event.getVenue());
        holder.eventDate.setText(event.getDate());
        holder.eventTime.setText(event.getTime());
        // You can add more bindings here based on your Event class structure
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle;
        TextView eventVenue;
        TextView eventDate;
        TextView eventTime;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventVenue = itemView.findViewById(R.id.event_venue);
            eventDate = itemView.findViewById(R.id.event_date);
            eventTime = itemView.findViewById(R.id.event_time);
            // Find and assign other views if available
        }
    }
}

 */



/*
* Sample code - After setting up event adapter to merge with HomePage ---- some parts are ady up in code
    //... (other imports)
import android.media.metrics.Event; // Include your Event class package here

public class HomePage extends AppCompatActivity {
    //... (other variables)

    private EventAdapter eventAdapter; // Adapter for the RecyclerView
    private List<Event> eventList; // List to hold event data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //... (other code)

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList); // Instantiate your adapter here
        recyclerView.setAdapter(eventAdapter);

        // Fetch event data from Firestore and populate the eventList
        fetchEventData();
    }

    //... (other methods)

    private void fetchEventData() {
        db.collection("events")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Create an Event object from document data and add it to eventList
                        Event event = document.toObject(Event.class); // Adjust this line according to your Event class
                        eventList.add(event);
                    }
                    // Notify adapter about data changes
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            });
    }
}

* */