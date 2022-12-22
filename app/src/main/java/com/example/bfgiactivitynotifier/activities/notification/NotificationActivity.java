package com.example.bfgiactivitynotifier.activities.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.activities.notification.adapters.NotificationsAdapter;
import com.example.bfgiactivitynotifier.activities.notification.models.NotificationModel;
import com.example.bfgiactivitynotifier.databinding.ActivityNotificationBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotificationBinding activityNotificationBinding = DataBindingUtil.setContentView(this,R.layout.activity_notification);

        activityNotificationBinding.backButton.setOnClickListener(view-> finish());

        List<NotificationModel> notificationModels = new ArrayList<>();

        try{
            SharedPreferences sp = getSharedPreferences("notifications_sp", MODE_PRIVATE);
            Map<String,?> keys = sp.getAll();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                Gson gson = new Gson();
                String json = entry.getValue().toString();
                NotificationModel obj = gson.fromJson(json, NotificationModel.class);
                notificationModels.add(obj);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(notificationModels.isEmpty()){
            activityNotificationBinding.alertsRecyclerView.setVisibility(View.GONE);
            activityNotificationBinding.noNotifications.setVisibility(View.VISIBLE);
        }else{
            activityNotificationBinding.noNotifications.setVisibility(View.GONE);
            activityNotificationBinding.alertsRecyclerView.setVisibility(View.VISIBLE);

            Collections.reverse(notificationModels);

            NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notificationModels,this);
            activityNotificationBinding.alertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            activityNotificationBinding.alertsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            activityNotificationBinding.alertsRecyclerView.setAdapter(notificationsAdapter);
        }
    }
}