package com.example.bfgiactivitynotifier.fragments.fragment_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.FragmentEventsBinding;
import com.example.bfgiactivitynotifier.fragments.fragment_events.adapters.AdapterEvent;
import com.example.bfgiactivitynotifier.fragments.fragment_events.models.ModelEvent;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    FragmentEventsBinding fragmentEventsBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentEventsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false);
        //use view to store the outermost view associated with the binding
        View view = fragmentEventsBinding.getRoot();

        //set the layout for the recycler view
        fragmentEventsBinding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //initialize the adapter class
        AdapterEvent adapterEvent = new AdapterEvent(getEvents(), view);

        //set the adapter for the recycler view
        fragmentEventsBinding.eventsRecyclerView.setAdapter(adapterEvent);

        //filter the recycler view content according to the selected option in the filter chips
        fragmentEventsBinding.allEvents.setOnClickListener(view1 -> {
            Toast.makeText(view.getContext(), "Get All Events", Toast.LENGTH_SHORT).show();
        });
        fragmentEventsBinding.todayEvents.setOnClickListener(view1 -> {
            Toast.makeText(view.getContext(), "Get Today's Events", Toast.LENGTH_SHORT).show();
        });
        fragmentEventsBinding.upcomingEvents.setOnClickListener(view1 -> {
            Toast.makeText(view.getContext(), "Get Upcoming Events", Toast.LENGTH_SHORT).show();
        });
        fragmentEventsBinding.endedEvents.setOnClickListener(view1 -> {
            Toast.makeText(view.getContext(), "Get Ended Events", Toast.LENGTH_SHORT).show();
        });



        return view;
    }

    //get the data for the notifications
    //currently dummy data
    //original will be pushed from the firebase
    public List<ModelEvent> getEvents(){
        List<ModelEvent> list = new ArrayList<>();

        list.add(new ModelEvent("ROOTS_2022_CSE_BTECH", "Tech 'O' Fest", "CSI club is organising 'Tech O Fest', Including three events. FASTECH (coding) TECH'O'LAUGH (memes) PRESENTECH (presentation & pitching). All the students of btech can register below.", "Nov 1, 2022 - Nov 2, 2022", "9:30 AM", "Innovation Center"));

        list.add(new ModelEvent("ROOTS_2022_CSE_BTECH", "Tech 'O' Fest", "CSI club is organising 'Tech O Fest', Including three events. FASTECH (coding) TECH'O'LAUGH (memes) PRESENTECH (presentation & pitching). All the students of btech can register below.", "Nov 1, 2022 - Nov 2, 2022", "9:30 AM", "Innovation Center"));

        list.add(new ModelEvent("ROOTS_2022_CSE_BTECH", "Tech 'O' Fest", "CSI club is organising 'Tech O Fest', Including three events. FASTECH (coding) TECH'O'LAUGH (memes) PRESENTECH (presentation & pitching). All the students of btech can register below.", "Nov 1, 2022 - Nov 2, 2022", "9:30 AM", "Innovation Center"));

        return list;
    }
}