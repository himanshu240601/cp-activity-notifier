package com.example.bfgiactivitynotifier;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.databinding.ActivityEventBinding;

public class EventActivity extends AppCompatActivity {

    ActivityEventBinding activityEventBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_event);

        //change the status bar background color to white
        Utility.changeStatusBarColor(getWindow(), R.color.primary);

        //on pressing the back arrow in the top bar
        activityEventBinding.backButton.setOnClickListener(view -> finish());
    }
}