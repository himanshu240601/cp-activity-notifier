package com.example.bfgiactivitynotifier.common;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.Window;

import androidx.core.content.ContextCompat;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.models.ModelUserData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utility {
    //user data model
    public static ModelUserData modelUserData;

    //setting the user model data
    //this modelUserData object will
    //help in setting the xml variables
    public static void setModelUserData(ModelUserData modelUserData) {
        //Todo: here the data from the server is fetched
        //Todo: and then the data in the model is set up
        //Todo: get first_name from the name itself
        //Todo: replace the arguments
        Utility.modelUserData = modelUserData;
    }

    //set status bar color
    public static void changeStatusBarColor(Window window, int color){
        //setting the background color of the status bar
        window.setStatusBarColor(ContextCompat.getColor(window.getContext() , color));
        //setting the text color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(color == R.color.primary){
                window.getDecorView().setSystemUiVisibility(BottomNavigationView.SYSTEM_UI_FLAG_IMMERSIVE);
            }else{
                window.getDecorView().setSystemUiVisibility(BottomNavigationView.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    //get current date
    //note: this method seems deprecated
    public static String getCurrentDate(){
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("MMM dd, yyy").format(Calendar.getInstance().getTime());
        return timeStamp;
    }
}
