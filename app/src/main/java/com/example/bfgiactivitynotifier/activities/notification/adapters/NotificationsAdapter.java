package com.example.bfgiactivitynotifier.activities.notification.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.activities.notification.models.NotificationModel;
import com.example.bfgiactivitynotifier.databinding.NotificationCardBinding;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private final List<NotificationModel> notificationModels;
    private final Context context;

    public NotificationsAdapter(List<NotificationModel> notificationModels, Context context) {
        this.notificationModels = notificationModels;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        NotificationCardBinding notificationCardBinding = DataBindingUtil.inflate(layoutInflater, R.layout.notification_card, parent, false);
        return new NotificationsViewHolder(notificationCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        holder.notificationCardBinding.setNotificationObject(notificationModels.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {
        NotificationCardBinding notificationCardBinding;
        public NotificationsViewHolder(@NonNull NotificationCardBinding itemView) {
            super(itemView.getRoot());
            notificationCardBinding = itemView;
        }
    }
}
