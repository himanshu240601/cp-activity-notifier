package com.example.bfgiactivitynotifier.faculty.tasks_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.ActivityTasksBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.faculty.adapters.UserTasksAdapter;
import com.example.bfgiactivitynotifier.models.UserTasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TasksActivity extends AppCompatActivity {

    public static int changedPosition = -1;
    private UserTasksAdapter userTasksAdapter;

    public static final List<UserTasks> list = new ArrayList<>();

    @Override
    protected void onRestart() {
        super.onRestart();
        checkFunction(FacultyActivity.userTasksList.get(changedPosition).getDocument_id());
    }

    public void checkFunction(String document_id) {
        if (changedPosition != -1) {

            for (int j = 0; j < list.size(); j++) {
                if (Objects.equals(list.get(j).getDocument_id(), document_id)) {
                    list.set(j, FacultyActivity.userTasksList.get(changedPosition));
                    userTasksAdapter.notifyItemChanged(j);
                    break;
                }
            }

            changedPosition = -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTasksBinding activityTasksBinding = DataBindingUtil.setContentView(this, R.layout.activity_tasks);
        //set text for top bar
        list.clear();
        Intent intent = getIntent();
        String text = intent.getStringExtra("textOnAction");
        activityTasksBinding.actionBarText.setText(text);

        activityTasksBinding.backButton.setOnClickListener(view-> onBackPressed());

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

            userTasksAdapter = new UserTasksAdapter(list, this, true, this);
            activityTasksBinding.recyclerViewAllTasks.setLayoutManager(new LinearLayoutManager(this));
            activityTasksBinding.recyclerViewAllTasks.setAdapter(userTasksAdapter);
        }else{
            activityTasksBinding.recyclerViewAllTasks.setVisibility(View.GONE);
            activityTasksBinding.noTasks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        list.clear();
    }
}