package com.example.bfgiactivitynotifier.common.models;

import com.google.firebase.Timestamp;

public class ModelPost {
    String action_taker, activity_name, activity_type, added_by, end_date, follow_up_taken_by, plan_authority, start_date;
    Timestamp last_updated;
    String time_posted;

    public String getTime_posted() {
        return time_posted;
    }

    public void setTime_posted(String time_posted) {
        this.time_posted = time_posted;
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
                     Timestamp last_updated) {
        this.action_taker = action_taker;
        this.activity_name = activity_name;
        this.activity_type = activity_type;
        this.added_by = added_by;
        this.end_date = end_date;
        this.follow_up_taken_by = follow_up_taken_by;
        this.plan_authority = plan_authority;
        this.start_date = start_date;
        this.last_updated = last_updated;
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
