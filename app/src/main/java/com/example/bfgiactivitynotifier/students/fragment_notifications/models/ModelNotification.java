package com.example.bfgiactivitynotifier.students.fragment_notifications.models;

//model class for the notifications
//'message' contains the notification text body
//'time' contains the estimated time of the notification after received by the user
//'viewed' is used to check whether the user has viewed the notification
//'viewed' will be true if the notification is read by the user otherwise false

public class ModelNotification {
    String message;
    String time;
    public boolean viewed;

    public ModelNotification(String message, String time, Boolean viewed) {
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
}
