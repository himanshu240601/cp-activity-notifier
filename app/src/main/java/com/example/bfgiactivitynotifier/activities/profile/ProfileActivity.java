package com.example.bfgiactivitynotifier.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        activityProfileBinding.setUserObject(CommonClass.modelUserData);

        activityProfileBinding.backButton.setOnClickListener(view-> finish());
    }
}