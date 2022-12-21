package com.example.bfgiactivitynotifier.faculty.tasks_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.ActivityTasksBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.faculty.adapters.UserTasksAdapter;
import com.example.bfgiactivitynotifier.models.UserTasks;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private ActivityTasksBinding activityTasksBinding;

    private final List<UserTasks> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTasksBinding = DataBindingUtil.setContentView(this, R.layout.activity_tasks);
        //set text for top bar
        Intent intent = getIntent();
        String text = intent.getStringExtra("textOnAction");
        activityTasksBinding.actionBarText.setText(text);

        activityTasksBinding.backButton.setOnClickListener(view-> finish());

        activityTasksBinding.progressBar.setVisibility(View.VISIBLE);
        if(text.equals("My Tasks")){
            list.addAll(FacultyActivity.userTasksList);
        }else{
            for(UserTasks userTasks : FacultyActivity.userTasksList){
                if(userTasks.getStatus().equals(text)){
                    list.add(userTasks);
                }
            }
        }
        activityTasksBinding.progressBar.setVisibility(View.GONE);
        if(!list.isEmpty()){
            activityTasksBinding.noTasks.setVisibility(View.GONE);
            activityTasksBinding.recyclerViewAllTasks.setVisibility(View.VISIBLE);

            UserTasksAdapter userTasksAdapter = new UserTasksAdapter(list, this);
            activityTasksBinding.recyclerViewAllTasks.setLayoutManager(new LinearLayoutManager(this));
            activityTasksBinding.recyclerViewAllTasks.setAdapter(userTasksAdapter);
        }else{
            activityTasksBinding.recyclerViewAllTasks.setVisibility(View.GONE);
            activityTasksBinding.noTasks.setVisibility(View.VISIBLE);
        }
    }
}