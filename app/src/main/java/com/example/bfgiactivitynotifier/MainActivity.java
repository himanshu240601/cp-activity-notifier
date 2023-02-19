package com.example.bfgiactivitynotifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.models.UserModel;
import com.example.bfgiactivitynotifier.signin.SignInActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //assign the binding class object
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        //shared preferences to check if used is
        //logged in app or not
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);

        //get the value from shared preferences
        //the default value will be false always
        boolean is_logged = sp.getBoolean("logged", false);

        //keep this activity for 2 seconds (as it's a splash screen)
        //then go to next screen according to the value
        //of is_logged variable
        //use Looper.getMainLooper() as Handler is deprecated
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (is_logged) {
                //set the user data model
                //this will be used at various activities
                //or fragments in the application
                //the data will be fetched from the shared prefs

                UserModel userModel = new UserModel(
                        sp.getString("user_id", ""),
                        sp.getString("full_name", "default"),
                        sp.getString("first_name", "default"),
                        sp.getString("designation", ""),
                        sp.getString("department", "")
                );
                new CommonClass().setModelUserData(userModel);

                //go to the next screen
                startActivity(new Intent(this, FacultyActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            }
        }, 2000);
    }
}