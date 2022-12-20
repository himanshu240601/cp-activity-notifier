package com.example.bfgiactivitynotifier.activities.notification.models;

public class NotificationModel {
    private String message;
    private String time;
    private Boolean viewed;

    public NotificationModel(String message, String time, Boolean viewed) {
        this.message = message;
        this.time = time;
        this.viewed = viewed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }
}
