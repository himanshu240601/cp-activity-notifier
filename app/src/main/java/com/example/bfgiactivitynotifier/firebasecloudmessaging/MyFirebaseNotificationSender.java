package com.example.bfgiactivitynotifier.firebasecloudmessaging;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseNotificationSender {
    private final String NOTIFICATION_TITLE;
    private final String NOTIFICATION_MESSAGE;
    private final String NOTIFICATION_TOPIC;
    private final Context context;

    private final RequestQueue requestQueue;
    private final String url = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "key=";

    public MyFirebaseNotificationSender(String NOTIFICATION_TITLE, String NOTIFICATION_MESSAGE, String NOTIFICATION_TOPIC, Context context) {
        this.NOTIFICATION_TITLE = NOTIFICATION_TITLE;
        this.NOTIFICATION_MESSAGE = NOTIFICATION_MESSAGE;
        this.NOTIFICATION_TOPIC = NOTIFICATION_TOPIC;
        this.context = context;

        getMetadata();

        requestQueue = Volley.newRequestQueue(context);
    }

    private void getMetadata() {
        try {
            Bundle metaData = context.getApplicationContext().getPackageManager()
                    .getApplicationInfo(context.getApplicationContext().getPackageName(),
                            PackageManager.GET_META_DATA).metaData;
            serverKey += metaData.get("fcmKeyValue").toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        serverKey += "";
    }

    public void sendNotification() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to","/topics/"+NOTIFICATION_TOPIC);
            JSONObject jsonObjectNotify = new JSONObject();
            jsonObjectNotify.put("title", NOTIFICATION_TITLE);
            jsonObjectNotify.put("body", NOTIFICATION_MESSAGE);
            jsonObject.put("notification", jsonObjectNotify);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject,
                    response -> {
                    },
                    error -> error.getLocalizedMessage()
            ){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", serverKey);

                    return header;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
