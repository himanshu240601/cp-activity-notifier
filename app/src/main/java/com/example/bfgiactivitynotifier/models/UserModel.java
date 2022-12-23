package com.example.bfgiactivitynotifier.models;

public class UserModel {
    private String user_id;
    private String full_name;
    private String first_name;
    private String designation;
    private String department;

    private String date;

    public UserModel(String full_name, String designation, String department){
        this.full_name = full_name;
        this.designation = designation;
        this.department = department;
    }

    public UserModel(String id, String full_name, String first_name, String designation, String department) {
        this.user_id = id;
        this.full_name = full_name;
        this.first_name = first_name;
        this.designation = designation;
        this.department = department;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
