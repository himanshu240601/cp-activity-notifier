package com.example.bfgiactivitynotifier.fragments.fragment_notifications.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.EventActivity;
import com.example.bfgiactivitynotifier.databinding.NotificationAlertBinding;
import com.example.bfgiactivitynotifier.fragments.fragment_notifications.models.ModelNotification;
import com.example.bfgiactivitynotifier.R;

import java.util.List;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.MyViewHolder> {

    //list for storing the data of notifications
    //and view for the current layout
    List<ModelNotification> notifications;
    View view;

    //constructor for the adapter class
    public AdapterNotification(List<ModelNotification> notifications, View view) {
        this.notifications = notifications;
        this.view = view;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        NotificationAlertBinding notificationAlertBinding = DataBindingUtil.inflate(layoutInflater, R.layout.notification_alert, parent, false);
        return new MyViewHolder(notificationAlertBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //change the formatting of the notification
        //if the user hasn't read the notification or viewed == false
        if(!(notifications.get(position).viewed)){
            //change the background color to secondary
            holder.notificationAlertBinding.constraintLayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.secondary));
            //make the text of the notification bold_italic
            holder.notificationAlertBinding.notificationText.setTypeface(
                    holder.notificationAlertBinding.notificationText.getTypeface(), Typeface.BOLD_ITALIC
            );
        }
        //setting the data binding object
        //the data will be updated in the layout file
        //the variables are declared inside the layout file
        //they'll fetch the data according to the object
        holder.notificationAlertBinding.setNotificationObject(notifications.get(position));

        //TODO: open activity on clicking the notification & mark the notification as viewed == true
        holder.notificationAlertBinding.constraintLayout.setOnClickListener(view -> {
            //Todo: send event id to be used in next layout
            //Todo: for fetching the event data
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        NotificationAlertBinding notificationAlertBinding;

        public MyViewHolder(@NonNull NotificationAlertBinding notificationAlertBinding) {
            super(notificationAlertBinding.getRoot());
            this.notificationAlertBinding = notificationAlertBinding;
        }
    }
}
