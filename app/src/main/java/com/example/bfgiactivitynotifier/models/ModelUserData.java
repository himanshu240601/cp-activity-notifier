package com.example.bfgiactivitynotifier.models;

public class ModelUserData {
    String first_name;
    String full_name;
    String id;
    String date;

    public ModelUserData(String first_name, String full_name, String id, String date) {
        this.first_name = first_name;
        this.full_name = full_name;
        this.id = id;
        this.date = date;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
