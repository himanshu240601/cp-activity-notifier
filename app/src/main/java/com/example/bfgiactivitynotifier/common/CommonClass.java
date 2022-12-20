package com.example.bfgiactivitynotifier.common;

import android.annotation.SuppressLint;

import com.example.bfgiactivitynotifier.models.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CommonClass {
    //user data model
    public static UserModel modelUserData;

    //setting the user model data
    //this modelUserData object will
    //help in setting the xml variables
    public void setModelUserData(UserModel model) {
        modelUserData = model;
        modelUserData.setDate(getCurrentDate());
    }

    //get current date
    //note: this method seems deprecated
    public String getCurrentDate(){
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("EEEE, dd MMM").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    //compare dates to get the status of tasks
    //1. task start date after current date (status = upcoming)
    //2. task start_date equal or before current date
    // end_date after current date (status = in progress)
    //3. task_start_date equal or before current_date
    // end_date before current date (status = not complete)
    public String getTasksStatus(String dateStart, String dateEnd) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start_date = format.parse(dateStart);
        Date end_date = format.parse(dateEnd);
        Date current = format.parse(getCurrentDate());
        if(start_date!=null){
            if(start_date.compareTo(current) > 0) {
                return "Upcoming";
            } else if(start_date.compareTo(current) <= 0) {
                if (end_date != null && end_date.compareTo(current) > 0) {
                    return "In Progress";
                }else{
                    return "Not Complete";
                }
            }
        }
        return "";
    }
}
