package com.example.bfgiactivitynotifier.common.models;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class ModelPost {
    String action_taker, activity_name, activity_type, added_by, end_date, follow_up_taken_by, plan_authority, start_date;
    Timestamp last_updated;
    String time_posted;
    String event_time;
    String event_venue;
    String posted_by;
    String event_id;

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by() {
        FirebaseFirestore.getInstance().collection("faculty_data").document(added_by).addSnapshotListener((value, error) -> {
            if(value!=null){
                posted_by = value.getString("name");
            }
        });
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_venue() {
        return event_venue;
    }

    public void setEvent_venue(String event_venue) {
        this.event_venue = event_venue;
    }

    public String getTime_posted() {
        return time_posted;
    }

    public void setTime_posted() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        time_posted = simpleDateFormat.format(last_updated.toDate());
    }

    public Timestamp getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Timestamp last_updated) {
        this.last_updated = last_updated;
    }

    public ModelPost(){
    }

    public ModelPost(String action_taker,
                     String activity_name,
                     String activity_type,
                     String added_by,
                     String end_date,
                     String follow_up_taken_by,
                     String plan_authority,
                     String start_date,
                     Timestamp last_updated,
                     String event_id) {
        this.action_taker = action_taker;
        this.activity_name = activity_name;
        this.activity_type = activity_type;
        this.added_by = added_by;
        this.end_date = end_date;
        this.follow_up_taken_by = follow_up_taken_by;
        this.plan_authority = plan_authority;
        this.start_date = start_date;
        this.last_updated = last_updated;
        this.event_id = event_id;
    }

    public String getAction_taker() {
        return action_taker;
    }

    public void setAction_taker(String action_taker) {
        this.action_taker = action_taker;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getFollow_up_taken_by() {
        return follow_up_taken_by;
    }

    public void setFollow_up_taken_by(String follow_up_taken_by) {
        this.follow_up_taken_by = follow_up_taken_by;
    }

    public String getPlan_authority() {
        return plan_authority;
    }

    public void setPlan_authority(String plan_authority) {
        this.plan_authority = plan_authority;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
}
