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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        Date start_date = format.parse(dateStart);
        Date end_date = format.parse(dateEnd);
        @SuppressLint("SimpleDateFormat") Date current = format.parse(new SimpleDateFormat("dd MMM yyyy").format(Calendar.getInstance().getTime()));
        if(start_date!=null){
            if(start_date.compareTo(current) > 0) {
                return "Upcoming Tasks";
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

    public static boolean checkDateRange(String start_date, String end_date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date start = format.parse(start_date);
            Date end = format.parse(end_date);
            @SuppressLint("SimpleDateFormat") Date current =
                    format.parse(
                            new SimpleDateFormat("dd MMM yyyy").format(Calendar.getInstance().getTime())
                    );

            Calendar cal = Calendar.getInstance();
            if (current != null) {
                cal.setTime(current);
                cal.add(Calendar.DATE, 7);
            }
            Date seven_days = format.parse(format.format(cal.getTime()));

            if(start!=null && start.compareTo(current)>0 && start.compareTo(seven_days)<=0){
                return true;
            }

            if(end!=null && end.compareTo(current)>0 && end.compareTo(seven_days)<=0){
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
