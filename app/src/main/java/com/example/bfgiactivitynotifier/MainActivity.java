package com.example.bfgiactivitynotifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.databinding.ActivityMainBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.students.StudentActivity;
import com.example.bfgiactivitynotifier.models.ModelUserData;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    //declare binding class object
    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //assign the binding class object
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //change status bar color
        Utility.changeStatusBarColor(getWindow(), R.color.white);

        //shared preferences to check if used is
        //logged in app or not
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);

        //get the value from shared preferences
        //the default value will be false always
        boolean is_logged = sp.getBoolean("logged", false);
        boolean is_student = sp.getBoolean("student", true);

        //keep this activity for 2 seconds (as it's a splash screen)
        //then go to next screen according to the value
        //of is_logged variable
        //use Looper.getMainLooper() as Handler is deprecated
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (is_logged) {
                //set the user data model
                //this will be used at various activities
                //or fragments in the application
                //the data will be fetched from the db server
                //here the dummy data is sent
                //after connecting to the database the data will be handled
                //in the setModelUserData itself
                //then we'll just need to call this function without any arguments
                String path = is_student ? "student_data" : "faculty_data";

                FirebaseFirestore.getInstance().collection(path).document(
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                ).addSnapshotListener((value, error) -> {
                    if(value!=null){
                        String name = value.getString("name");
                        String[] name_str = Objects.requireNonNull(name).split(" ");
                        Utility.setModelUserData(new ModelUserData(name_str[1], name, FirebaseAuth.getInstance().getCurrentUser().getEmail(), Utility.getCurrentDate()));
                    }
                });

                //according to the type of user
                //go to the next screen
                if(is_student){
                    startActivity(new Intent(this, StudentActivity.class));
                }else{
                    startActivity(new Intent(this, FacultyActivity.class));
                }
            } else {
                startActivity(new Intent(this, SignInActivity.class));
            }
            finish();
        }, 2000);
    }
}