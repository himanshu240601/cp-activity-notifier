package com.example.bfgiactivitynotifier.fragments.fragment_events;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.models.ModelPost;
import com.example.bfgiactivitynotifier.databinding.FragmentEventsBinding;
import com.example.bfgiactivitynotifier.fragments.fragment_events.adapters.AdapterEvent;
import com.example.bfgiactivitynotifier.fragments.fragment_events.models.DocumentReferenceClass;
import com.example.bfgiactivitynotifier.fragments.fragment_events.models.ModelEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        //inside get events method once the data is fetched
        getEvents(view);

        //filter the recycler view content according to the selected option in the filter chips
        fragmentEventsBinding.allEvents.setOnClickListener(view1 -> Toast.makeText(view.getContext(), "Get All Events", Toast.LENGTH_SHORT).show());
        fragmentEventsBinding.todayEvents.setOnClickListener(view1 -> Toast.makeText(view.getContext(), "Get Today's Events", Toast.LENGTH_SHORT).show());
        fragmentEventsBinding.upcomingEvents.setOnClickListener(view1 -> Toast.makeText(view.getContext(), "Get Upcoming Events", Toast.LENGTH_SHORT).show());
        fragmentEventsBinding.endedEvents.setOnClickListener(view1 -> Toast.makeText(view.getContext(), "Get Ended Events", Toast.LENGTH_SHORT).show());



        return view;
    }

    //get the data for the notifications
    //data will be pushed from the firebase
    private final FirebaseFirestore firebaseFirestoreData = FirebaseFirestore.getInstance();

    public void getEvents(View view){
        SharedPreferences sp = view.getContext().getSharedPreferences("login", MODE_PRIVATE);
        boolean isStudent = sp.getBoolean("student", true);

        List<ModelPost> list = new ArrayList<>();
        List<DocumentReferenceClass> documentReferenceList = new ArrayList<>();

        if(!isStudent){
            firebaseFirestoreData.collection("faculty_data").document(
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
            ).collection("activities")
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if(task.getResult()!=null){
                        fragmentEventsBinding.emptyRecyclerview.setVisibility(View.GONE);

                        for (DocumentSnapshot document : task.getResult()) {
                            documentReferenceList.add(document.toObject(DocumentReferenceClass.class));
                        }
                        for(DocumentReferenceClass docRef : documentReferenceList){
                            firebaseFirestoreData.document(docRef.getId()).get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    ModelPost modelEvent = task1.getResult().toObject(ModelPost.class);
                                    list.add(modelEvent);
                                }
                            });
                        }
                        //set the layout for the recycler view
                        fragmentEventsBinding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

                        //initialize the adapter class
                        AdapterEvent adapterEvent = new AdapterEvent(list, view);

                        //set the adapter for the recycler view
                        fragmentEventsBinding.eventsRecyclerView.setAdapter(adapterEvent);
                    }else{
                        fragmentEventsBinding.emptyRecyclerview.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else{
            fragmentEventsBinding.emptyRecyclerview.setVisibility(View.VISIBLE);
        }
    }
}