package com.example.bfgiactivitynotifier.faculty;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.databinding.ActivityFacultyBinding;
import com.example.bfgiactivitynotifier.faculty.fragment_home.FacultyHomeFragment;
import com.example.bfgiactivitynotifier.fragments.fragment_events.EventsFragment;
import com.example.bfgiactivitynotifier.fragments.fragment_notifications.NotificationsFragment;
import com.example.bfgiactivitynotifier.fragments.fragment_settings.SettingsFragment;
import com.google.firebase.messaging.FirebaseMessaging;

public class FacultyActivity extends AppCompatActivity {

    ActivityFacultyBinding activityFacultyBinding;

    //initializing the fragment variables
    //so that we can replace the fragments
    //instead of re creating them
    //each time changing the screen
    private final Fragment homeFragment = new FacultyHomeFragment();
    private final Fragment eventsFragment = new EventsFragment();
    private final Fragment notificationsFragment = new NotificationsFragment();
    private final Fragment settingsFragment = new SettingsFragment();
    //set the default fragment as home fragment
    private Fragment active = homeFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFacultyBinding = DataBindingUtil.setContentView(this, R.layout.activity_faculty);

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
        activityFacultyBinding.bottomNavigation.setOnItemSelectedListener(item -> {
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