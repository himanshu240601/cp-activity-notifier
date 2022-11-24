package com.example.bfgiactivitynotifier.common;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.adapters.AdapterPostSt;
import com.example.bfgiactivitynotifier.common.models.ModelPost;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetPostDataToRecyclerView {
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = firebaseFirestore.collection("activities_data");
    public void getFirestoreData(View view){
        List<ModelPost> modelPostArrayList = new ArrayList<>();
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    ModelPost modelPost = document.toObject(ModelPost.class);
                    Objects.requireNonNull(modelPost).setTime_posted();
                    modelPost.setPosted_by();
                    modelPostArrayList.add(modelPost);
                }
                //set the layout for the recycler view
                RecyclerView recyclerView = view.findViewById(R.id.postRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

                //initialize the adapter class
                AdapterPostSt adapterPostSt = new AdapterPostSt(modelPostArrayList, view);

                //set the adapter for the recycler view
                recyclerView.setAdapter(adapterPostSt);
            }
        });
    }
}
