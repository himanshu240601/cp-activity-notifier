package com.example.bfgiactivitynotifier.students.fragment_settings;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.SignInActivity;
import com.example.bfgiactivitynotifier.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding fragmentSettingsBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        View view = fragmentSettingsBinding.getRoot();

        //logout the user from the application
        //redirect to the login page
        //clear all the logged in data of the user
        fragmentSettingsBinding.logout.setOnClickListener(view1 -> new AlertDialog.Builder(view.getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout").setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Context context = view.getContext();

                    //clearing data from shared preferences
                    SharedPreferences sp = context.getSharedPreferences("login", MODE_PRIVATE);
                    sp.edit().clear().apply();

                    //sign out user from firebase
                    FirebaseAuth.getInstance().signOut();

                    //function to clear all the activities and fragments
                    clearActivitiesFragments();

                    context.startActivity(new Intent(context, SignInActivity.class));

                })
                .setNegativeButton("No", null)
                .show());

        return view;
    }

    private void clearActivitiesFragments() {
        //clear all the fragments
        List<Fragment> all_fragments = getChildFragmentManager().getFragments();
        for(Fragment fragment : all_fragments){
            getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }

        //clear all the activities
        requireActivity().finishAffinity();
    }
}