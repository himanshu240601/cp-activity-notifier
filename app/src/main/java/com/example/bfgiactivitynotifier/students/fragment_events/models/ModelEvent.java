package com.example.bfgiactivitynotifier.students.fragment_events.models;

public class ModelEvent {
    String id;
    String title;
    String description;
    String event_date;
    String event_time;
    String venue;

    public ModelEvent(String id, String title, String description, String event_date, String event_time, String venue) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.event_date = event_date;
        this.event_time = event_time;
        this.venue = venue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
