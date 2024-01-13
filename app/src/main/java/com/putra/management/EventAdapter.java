//package com.putra.management;
//
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.google.firebase.events.Event;
//import com.putra.management.EventAdapter; // Replace this with the correct package for your Event class
//import java.util.List;
//
//public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
//    private List<Event> eventList;
//
//    public EventAdapter(List<Event> eventList) {
//        this.eventList = eventList;
//    }
//
//    @NonNull
//    @Override
//    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
//        return new EventViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
//        Event event = eventList.get(position);
//        // Bind event data to the views in the ViewHolder
//        holder.eventTitle.setText(event.getTitle());
//        holder.eventVenue.setText(event.getVenue());
//        holder.eventDate.setText(event.getDate());
//        holder.eventTime.setText(event.getTime());
//        // You can add more bindings here based on your Event class structure
//    }
//
//    @Override
//    public int getItemCount() {
//        return eventList.size();
//    }
//
//    public static class EventViewHolder extends RecyclerView.ViewHolder {
//        TextView eventTitle;
//        TextView eventVenue;
//        TextView eventDate;
//        TextView eventTime;
//
//        public EventViewHolder(@NonNull View itemView) {
//            super(itemView);
//            eventTitle = itemView.findViewById(R.id.event_title);
//            eventVenue = itemView.findViewById(R.id.event_venue);
//            eventDate = itemView.findViewById(R.id.event_date);
//            eventTime = itemView.findViewById(R.id.event_time);
//            // Find and assign other views if available
//        }
//    }
//}

package com.putra.management;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private ArrayList<event> eventArrayList;
    private Context context;

    public EventAdapter(ArrayList<event> eventArrayList, Context context) {
        this.eventArrayList = eventArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        event events = eventArrayList.get(position);
        holder.eventTitleTV.setText(events.getTitle());
        holder.eventDateTV.setText(events.getDate());
        holder.eventDescriptionTV.setText(events.getDescription());

        String imageUrl = events.getImage();
        Picasso.get()
                .load(imageUrl)
                .into(holder.eventImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, navigate to another page
                Intent intent = new Intent(context, view_specific_event.class);

                // Pass the extracted data into view_specific_event
                intent.putExtra("eventTitle", events.getTitle());
                intent.putExtra("eventDate", events.getDate());
                intent.putExtra("eventDescription", events.getDescription());
                intent.putExtra("eventImageUrl", events.getImage());
                intent.putExtra("eventVenue", events.getVenue());
                intent.putExtra("eventSpeaker", events.getSpeaker_name());
                intent.putExtra("eventOrganizer", events.getOrganizer());
                intent.putExtra("eventStartTime", events.getStart_time());
                intent.putExtra("eventEndTime", events.getEnd_time());

                // Start the activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventTitleTV;
        private final TextView eventDateTV;
        private final TextView eventDescriptionTV;
        private final ImageView eventImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTV = itemView.findViewById(R.id.idTVTitle);
            eventDateTV = itemView.findViewById(R.id.idTVDate);
            eventDescriptionTV = itemView.findViewById(R.id.idTVDescription);
            eventImageView = itemView.findViewById(R.id.idEventImageView);
        }
    }
}