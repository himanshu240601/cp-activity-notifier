package com.example.bfgiactivitynotifier.students.fragment_events.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.EventActivity;
import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.EventCardBinding;
import com.example.bfgiactivitynotifier.students.fragment_events.models.ModelEvent;

import java.util.List;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.MyViewHolder> {

    //list for storing the data of notifications
    //and view for the current layout
    List<ModelEvent> events;
    View view;

    //constructor for the adapter class
    public AdapterEvent(List<ModelEvent> events, View view) {
        this.events = events;
        this.view = view;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        EventCardBinding eventCardBinding = DataBindingUtil.inflate(layoutInflater, R.layout.event_card, parent, false);
        return new MyViewHolder(eventCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //setting the data binding object
        //the data will be updated in the layout file
        //the variables are declared inside the layout file
        //they'll fetch the data according to the object
        holder.eventCardBinding.setEventObject(events.get(position));

        holder.eventCardBinding.eventsCard.setOnClickListener(view -> {
            //Todo: send event id to be used in next layout
            //Todo: for fetching the event data
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        EventCardBinding eventCardBinding;

        public MyViewHolder(@NonNull EventCardBinding eventCardBinding) {
            super(eventCardBinding.getRoot());
            this.eventCardBinding = eventCardBinding;
        }
    }
}
