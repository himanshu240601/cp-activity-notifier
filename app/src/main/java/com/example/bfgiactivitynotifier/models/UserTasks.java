package com.example.bfgiactivitynotifier.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserTasks {
    private String document_id;

    public String getDocument_id() {
        return document_id;
    }

    private ArrayList<String> delay_reason;

    public ArrayList<String> getDelay_reason() {
        return delay_reason;
    }

    public void setDelay_reason(ArrayList<String> delay_reason) {
        this.delay_reason = delay_reason;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    private String task_plan_authority;
    private String task_name;
    private String task_type;
    private String action_taker;
    private String follow_up_taken_by;
    private String start_date;
    private String end_date;
    private Timestamp added_on;
    private String added_by;
    private Timestamp last_updated;
    private boolean completed;
    private String department;

    private String added_by_name;

    private String req_change;

    public String getReq_change() {
        return req_change;
    }

    public void setReq_change(String req_change) {
        this.req_change = req_change;
    }

    public String getAdded_by_name() {
        return added_by_name;
    }

    public void setAdded_by_name(String added_by_name) {
        this.added_by_name = added_by_name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    private String status;

    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserTasks(){
    }

    public UserTasks(ArrayList<String> delay_reason, String task_plan_authority,
                     String task_name, String task_type,
                     String action_taker, String follow_up_taken_by,
                     String start_date, String end_date, Timestamp added_on, String added_by, Timestamp last_updated, boolean completed, String department) {
        this.delay_reason = delay_reason;
        this.task_plan_authority = task_plan_authority;
        this.task_name = task_name;
        this.task_type = task_type;
        this.action_taker = action_taker;
        this.follow_up_taken_by = follow_up_taken_by;
        this.start_date = start_date;
        this.end_date = end_date;
        this.added_on = added_on;
        this.added_by = added_by;
        this.last_updated = last_updated;
        this.completed = completed;
        this.department = department;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTask_plan_authority() {
        return task_plan_authority;
    }

    public void setTask_plan_authority(String task_plan_authority) {
        this.task_plan_authority = task_plan_authority;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getAction_taker() {
        return action_taker;
    }

    public void setAction_taker(String action_taker) {
        this.action_taker = action_taker;
    }

    public String getFollow_up_taken_by() {
        return follow_up_taken_by;
    }

    public void setFollow_up_taken_by(String follow_up_taken_by) {
        this.follow_up_taken_by = follow_up_taken_by;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Timestamp getAdded_on() {
        return added_on;
    }

    public void setAdded_on(Timestamp added_on) {
        this.added_on = added_on;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
        FirebaseFirestore.getInstance().collection("faculty_data")
                .document(added_by)
                .get().addOnCompleteListener(task -> setAdded_by_name((String) task.getResult().get("name")));
    }

    public Timestamp getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Timestamp last_updated) {
        this.last_updated = last_updated;
    }

    private String req_doc_id;

    public String getReq_doc_id() {
        return req_doc_id;
    }

    public void setReq_doc_id(String req_doc_id) {
        this.req_doc_id = req_doc_id;
    }
}
