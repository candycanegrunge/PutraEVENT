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
