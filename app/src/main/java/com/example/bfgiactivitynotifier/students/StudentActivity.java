package com.example.bfgiactivitynotifier.students;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.databinding.ActivityStudentBinding;
import com.example.bfgiactivitynotifier.students.fragment_events.EventsFragment;
import com.example.bfgiactivitynotifier.students.fragment_home.HomeFragment;
import com.example.bfgiactivitynotifier.students.fragment_notifications.NotificationsFragment;
import com.example.bfgiactivitynotifier.students.fragment_settings.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class StudentActivity extends AppCompatActivity {

    ActivityStudentBinding activityHomeBinding;

    //initializing the fragment variables
    //so that we can replace the fragments
    //instead of re creating them
    //each time changing the screen
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment eventsFragment = new EventsFragment();
    private final Fragment notificationsFragment = new NotificationsFragment();
    private final Fragment settingsFragment = new SettingsFragment();
    //set the default fragment as home fragment
    private Fragment active = homeFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_student);

        //change the status bar background color to white
        Utility.changeStatusBarColor(getWindow(), R.color.primary);

        //create the fragments and add them to the stack
        createFragment(homeFragment);
        createFragment(eventsFragment);
        createFragment(notificationsFragment);
        createFragment(settingsFragment);

        //show the first fragment by default (which is the homeFragment)
        changeFragment(homeFragment);

        //bottom navigation bar functions
        //changing screens according to the
        //current active fragment
        activityHomeBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            //call the changeFragment function on
            //navigation bar item change listener
            //and set the current fragment as active
                    switch (item.getItemId()) {
                        case R.id.home_page:
                            changeFragment(homeFragment);
                            active = homeFragment;
                            return true;
                        case R.id.events_page:
                            changeFragment(eventsFragment);
                            active = eventsFragment;
                            return true;
                        case R.id.notifications_page:
                            changeFragment(notificationsFragment);
                            active =  notificationsFragment;
                            return true;
                        case R.id.settings_page:
                            changeFragment(settingsFragment);
                            active = settingsFragment;
                            return true;
                    }
                    return false;
                }
        );

        //get firebase notifications
        FirebaseMessaging.getInstance().subscribeToTopic("events")
                .addOnCompleteListener(task -> {
                    String msg = "Done";
                    if (!task.isSuccessful()) {
                        msg = "Failed";
                    }
                });
    }

    //function to create fragments
    private void createFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, fragment)
                .hide(fragment)
                .commit();
    }

    //change the fragments
    private void changeFragment(Fragment fragment){
        //first hide the already active fragment
        getSupportFragmentManager().beginTransaction()
                .hide(active)
                .commit();
        //then show the fragment according to the
        //current bottom selected item
        getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commit();
    }

    //remove all previous activities from stack if any and close the application
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}