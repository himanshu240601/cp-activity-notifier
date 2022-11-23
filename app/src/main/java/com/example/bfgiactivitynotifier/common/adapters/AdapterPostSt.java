package com.example.bfgiactivitynotifier.common.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.EventActivity;
import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.PostCardBinding;
import com.example.bfgiactivitynotifier.common.models.ModelPost;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterPostSt extends RecyclerView.Adapter<AdapterPostSt.MyViewHolder>{
    //list for storing the data of notifications
    //and view for the current layout
    List<ModelPost> modelPosts;
    View view;

    //constructor for the adapter class
    public AdapterPostSt(List<ModelPost> modelPosts, View view) {
        this.modelPosts = modelPosts;
        this.view = view;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PostCardBinding postCardBinding = DataBindingUtil.inflate(layoutInflater, R.layout.post_card, parent, false);
        return new AdapterPostSt.MyViewHolder(postCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //setting the data binding object
        //the data will be updated in the layout file
        //the variables are declared inside the layout file
        //they'll fetch the data according to the object

        holder.postCardBinding.setPostObject(modelPosts.get(position));

        holder.postCardBinding.eventsCard.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            intent.putExtra("event_data", modelPosts.get(position).getEvent_id());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        PostCardBinding postCardBinding;

        public MyViewHolder(@NonNull PostCardBinding postCardBinding) {
            super(postCardBinding.getRoot());
            this.postCardBinding = postCardBinding;
        }
    }
}
