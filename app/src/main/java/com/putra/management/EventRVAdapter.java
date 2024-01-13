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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventRVAdapter extends RecyclerView.Adapter<EventRVAdapter.ViewHolder> {

    private ArrayList<event> eventArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Add this member variable

    // Constructor
    public EventRVAdapter(ArrayList<event> eventArrayList, Context context) {
        this.eventArrayList = eventArrayList;
        this.context = context;
    }

    // Interface to handle item click events
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_event_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        event events = eventArrayList.get(position);
        holder.eventTitleTV.setText(events.getTitle());
        holder.eventDateTV.setText(events.getDate());
        holder.eventTimeTV.setText(events.getDescription());

        String imageUrl = events.getImage();
        Picasso.get()
                .load(imageUrl)
                .into(holder.eventImageView);

        // Set click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, v);
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
        private final TextView eventTimeTV;
        private final ImageView eventImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTV = itemView.findViewById(R.id.event_title);
            eventDateTV = itemView.findViewById(R.id.event_date);
            eventTimeTV = itemView.findViewById(R.id.event_time);
            eventImageView = itemView.findViewById(R.id.event_image);
        }
    }
}