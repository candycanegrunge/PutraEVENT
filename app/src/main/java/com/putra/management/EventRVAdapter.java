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

    private final ArrayList<Event> eventArrayList;
    private final Context context;
    private OnItemClickListener onItemClickListener; // Add this member variable

    // Constructor
    public EventRVAdapter(ArrayList<Event> eventArrayList, Context context) {
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
        Event events = eventArrayList.get(position);
        holder.eventTitleTV.setText(events.getTitle());
        holder.eventDateTV.setText(events.getDate());

        String start_time = insertColon(events.getStart_time());
        String end_time = insertColon(events.getEnd_time());
        String combineTime = start_time + " - " + end_time;
        holder.eventTimeTV.setText(combineTime);
        holder.eventVenueTV.setText(events.getVenue());

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
        private final TextView eventVenueTV;
        private final ImageView eventImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTV = itemView.findViewById(R.id.event_title);
            eventDateTV = itemView.findViewById(R.id.event_date);
            eventTimeTV = itemView.findViewById(R.id.event_time);
            eventImageView = itemView.findViewById(R.id.event_image);
            eventVenueTV = itemView.findViewById(R.id.event_venue);
        }
    }

    private String insertColon(String time) {
        if (time.length() == 4) {
            return time.substring(0, 2) + ":" + time.substring(2);
        }
        // handle other cases as needed
        return time;
    }
}