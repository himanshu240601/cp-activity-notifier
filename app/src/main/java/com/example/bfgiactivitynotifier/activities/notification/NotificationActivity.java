package com.example.bfgiactivitynotifier.activities.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.activities.notification.adapters.NotificationsAdapter;
import com.example.bfgiactivitynotifier.activities.notification.models.NotificationModel;
import com.example.bfgiactivitynotifier.databinding.ActivityNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotificationBinding activityNotificationBinding = DataBindingUtil.setContentView(this,R.layout.activity_notification);

        activityNotificationBinding.backButton.setOnClickListener(view-> finish());

        List<NotificationModel> notificationModels = new ArrayList<>();
        notificationModels.add(
                new NotificationModel("This is a new notification", "Just now", false)
        );
        notificationModels.add(
                new NotificationModel("This is a new notification", "2 hrs ago", false)
        );
        notificationModels.add(
                new NotificationModel("This is a new notification", "Yesterday", true)
        );

        if(notificationModels.isEmpty()){
            activityNotificationBinding.alertsRecyclerView.setVisibility(View.GONE);
            activityNotificationBinding.noNotifications.setVisibility(View.VISIBLE);
        }else{
            activityNotificationBinding.noNotifications.setVisibility(View.GONE);
            activityNotificationBinding.alertsRecyclerView.setVisibility(View.VISIBLE);

            NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notificationModels,this);
            activityNotificationBinding.alertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            activityNotificationBinding.alertsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            activityNotificationBinding.alertsRecyclerView.setAdapter(notificationsAdapter);
        }
    }
}