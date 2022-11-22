package com.example.bfgiactivitynotifier.students.fragment_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.GetPostDataToRecyclerView;
import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    FragmentHomeBinding fragmentHomeBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        //use view to store the outermost view associated with the binding
        View view = fragmentHomeBinding.getRoot();

        GetPostDataToRecyclerView getPostDataToRecyclerView = new GetPostDataToRecyclerView();
        getPostDataToRecyclerView.getFirestoreData(view);

        return view;
    }
}