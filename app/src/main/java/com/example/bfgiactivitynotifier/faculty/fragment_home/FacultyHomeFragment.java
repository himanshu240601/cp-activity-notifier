package com.example.bfgiactivitynotifier.faculty.fragment_home;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.FragmentFacultyHomeBinding;
import com.example.bfgiactivitynotifier.faculty.AddNewPostActivity;

public class FacultyHomeFragment extends Fragment {

    FragmentFacultyHomeBinding fragmentFacultyHomeBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentFacultyHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_faculty_home, container, false);
        //use view to store the outermost view associated with the binding
        View view = fragmentFacultyHomeBinding.getRoot();

        fragmentFacultyHomeBinding.addNewPost.setOnClickListener(view1 -> view1.getContext().startActivity(new Intent(view1.getContext(), AddNewPostActivity.class)));

        return view;
    }
}