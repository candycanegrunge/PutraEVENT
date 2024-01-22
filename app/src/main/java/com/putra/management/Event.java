package com.putra.management;

public class Event {
    private String title, venue, date, description, image, speaker_name, organizer, start_time, end_time;
    private String id;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public Event() {}

    // Constructor
    public Event(String title, String venue, String date, String description, String image,
                 String speaker_name, String organizer, String start_time, String end_time, String id) {
        this.title = title;
        this.venue = venue;
        this.date = date;
        this.description = description;
        this.image = image;
        this.speaker_name = speaker_name;
        this.organizer = organizer;
        this.start_time = start_time;
        this.end_time = end_time;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
