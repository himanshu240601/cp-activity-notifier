package com.example.bfgiactivitynotifier.activities.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.activities.profile.ProfileActivity;
import com.example.bfgiactivitynotifier.databinding.ActivitySettingsBinding;
import com.example.bfgiactivitynotifier.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        activitySettingsBinding.backButton.setOnClickListener(view-> finish());

        activitySettingsBinding.openProfile.setOnClickListener(view-> startActivity(new Intent(this, ProfileActivity.class)));

        activitySettingsBinding.logOut.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Do you really want to logout?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    sharedPreferences = getSharedPreferences("notifications_sp", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    finishAffinity();
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(android.R.string.no, null)
                .show());
    }
}