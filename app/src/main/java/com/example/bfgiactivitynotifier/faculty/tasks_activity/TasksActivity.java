package com.example.bfgiactivitynotifier.faculty.tasks_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.ActivityTasksBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.faculty.adapters.UserTasksAdapter;

public class TasksActivity extends AppCompatActivity {

    private ActivityTasksBinding activityTasksBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTasksBinding = DataBindingUtil.setContentView(this, R.layout.activity_tasks);
        //set text for top bar
        setActionBarTitle();

        UserTasksAdapter userTasksAdapter = new UserTasksAdapter(FacultyActivity.userTasksList, this);
        activityTasksBinding.recyclerViewAllTasks.setLayoutManager(new LinearLayoutManager(this));
        activityTasksBinding.recyclerViewAllTasks.setAdapter(userTasksAdapter);
    }

    private void setActionBarTitle() {
        Intent intent = getIntent();
        activityTasksBinding.actionBarText.setText(intent.getStringExtra("textOnAction"));
    }
}