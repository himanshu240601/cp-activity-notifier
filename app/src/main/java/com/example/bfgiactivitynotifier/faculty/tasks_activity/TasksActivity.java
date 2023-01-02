package com.example.bfgiactivitynotifier.faculty.tasks_activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

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

    private ActivityTasksBinding activityTasksBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTasksBinding = DataBindingUtil.setContentView(this, R.layout.activity_tasks);
        //set text for top bar
        list.clear();
        Intent intent = getIntent();
        String text = intent.getStringExtra("textOnAction");
        activityTasksBinding.actionBarText.setText(text);

        activityTasksBinding.backButton.setOnClickListener(view-> onBackPressed());

        activityTasksBinding.sortButton.setOnClickListener(view-> showSortByPopUp());

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

    @SuppressLint("NonConstantResourceId")
    private void showSortByPopUp() {
        PopupMenu popupMenu = new PopupMenu(this, activityTasksBinding.sortButton);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.sortByLatest:
                    Toast.makeText(this, "sort by latest", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.sortByOldest:
                    Toast.makeText(this, "sort by oldest", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });

        popupMenu.inflate(R.menu.sort_by);
        popupMenu.setGravity(Gravity.END);
        popupMenu.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        list.clear();
    }
}