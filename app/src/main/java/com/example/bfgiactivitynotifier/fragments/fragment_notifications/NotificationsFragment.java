package com.example.bfgiactivitynotifier.fragments.fragment_notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.FragmentNotificationsBinding;
import com.example.bfgiactivitynotifier.fragments.fragment_notifications.adapters.AdapterNotification;
import com.example.bfgiactivitynotifier.fragments.fragment_notifications.models.ModelNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    //binding class for the fragment
    FragmentNotificationsBinding fragmentNotificationsBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentNotificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        //use view to store the outermost view associated with the binding
        View view = fragmentNotificationsBinding.getRoot();

        //set the layout for the recycler view
        fragmentNotificationsBinding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //initialize the adapter class
        AdapterNotification adapterNotification = new AdapterNotification(getNotifications(), view);

        //set the adapter for the recycler view
        fragmentNotificationsBinding.notificationRecyclerView.setAdapter(adapterNotification);

        return view;
    }


    //get the data for the notifications
    //currently dummy data
    //original will be pushed from the firebase
    public List<ModelNotification> getNotifications(){
        List<ModelNotification> list = new ArrayList<>();
        list.add(new ModelNotification("Roots cultural club is organizing trails for Youth Fest 2022, on Oct 14, 2022", "Just Now", false));
        return list;
    }
}