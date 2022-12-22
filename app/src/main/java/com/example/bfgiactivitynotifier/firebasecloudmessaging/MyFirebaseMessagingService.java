package com.example.bfgiactivitynotifier.firebasecloudmessaging;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.activities.notification.models.NotificationModel;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        getFirebaseMessage(Objects.requireNonNull(message.getNotification()).getTitle(), message.getNotification().getBody());
    }

    private void saveNotificationToSp(String message) {
        SharedPreferences sp = getSharedPreferences("notifications_sp", MODE_PRIVATE);
        Gson gson = new Gson();
        String current_time = new CommonClass().getCurrentDate();
        String json = gson.toJson(new NotificationModel(message,  current_time));
        sp.edit().putString("notifications_sp"+json, json).apply();
    }

    public void getFirebaseMessage(String title, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CH_FIREBASE")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(101, builder.build());

        saveNotificationToSp(message);
    }
}
