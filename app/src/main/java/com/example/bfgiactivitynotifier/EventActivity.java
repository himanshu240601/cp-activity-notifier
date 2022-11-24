package com.example.bfgiactivitynotifier;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.common.models.ModelPost;
import com.example.bfgiactivitynotifier.databinding.ActivityEventBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventActivity extends AppCompatActivity {

    ActivityEventBinding activityEventBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_event);

        //change the status bar background color to white
        Utility.changeStatusBarColor(getWindow(), R.color.primary);

        Intent intent = getIntent();
        //get data from firebase
        FirebaseFirestore.getInstance().document(
                intent.getStringExtra("event_data")
        ).addSnapshotListener((value, error) -> {
            if(value!=null){
                ModelPost modelPost = value.toObject(ModelPost.class);
                activityEventBinding.setEventObject(modelPost);
            }
        });

        //on pressing the back arrow in the top bar
        activityEventBinding.backButton.setOnClickListener(view -> finish());
    }
}