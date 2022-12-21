package com.example.bfgiactivitynotifier.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.activities.notification.NotificationActivity;
import com.example.bfgiactivitynotifier.activities.profile.ProfileActivity;
import com.example.bfgiactivitynotifier.activities.settings.SettingsActivity;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.ActivityFacultyBinding;
import com.example.bfgiactivitynotifier.faculty.adapters.UserTasksAdapter;
import com.example.bfgiactivitynotifier.faculty.add_new_post.AddNewPostActivity;
import com.example.bfgiactivitynotifier.faculty.models.TasksCount;
import com.example.bfgiactivitynotifier.faculty.tasks_activity.TasksActivity;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FacultyActivity extends AppCompatActivity {

    private ActivityFacultyBinding activityFacultyBinding;

    private final CommonClass commonClass = new CommonClass();

    private static final TasksCount tasksCount = new TasksCount();

    public static final List<UserTasks> userTasksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFacultyBinding = DataBindingUtil.setContentView(this, R.layout.activity_faculty);

        subscribeToFirebaseTopic();

        activityFacultyBinding.setUserObject(CommonClass.modelUserData);

        activityFacultyBinding.setTaskCount(tasksCount);

        createRecyclerView();

        activityFacultyBinding.openProfile.setOnClickListener(view-> startActivity(new Intent(this, ProfileActivity.class)));

        activityFacultyBinding.openNotifications.setOnClickListener(view-> startActivity(new Intent(this, NotificationActivity.class)));

        activityFacultyBinding.openSettings.setOnClickListener(view-> startActivity(new Intent(this, SettingsActivity.class)));

        activityFacultyBinding.inProgress.setOnClickListener(view-> openTasksActivity("In Progress"));
        
        activityFacultyBinding.upcoming.setOnClickListener(view-> openTasksActivity("Upcoming Tasks"));
        
        activityFacultyBinding.completed.setOnClickListener(view-> openTasksActivity("Completed Tasks"));

        activityFacultyBinding.notComplete.setOnClickListener(view-> openTasksActivity("Not Complete"));

        activityFacultyBinding.viewAllTasks.setOnClickListener(view-> openTasksActivity("My Tasks"));

        activityFacultyBinding.addNewTask.setOnClickListener(view-> startActivity(new Intent(this, AddNewPostActivity.class)));
    }

    private void openTasksActivity(String message){
        Intent intent = new Intent(this, TasksActivity.class);
        intent.putExtra("textOnAction", message);
        startActivity(intent);
    }

    private void createRecyclerView() {
        //set the layout for the recycler view
        activityFacultyBinding.taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getTasksData();
    }

    private void getTasksData() {
        activityFacultyBinding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query collectionReference = firebaseFirestore.collection("activities_data")
                .orderBy("last_updated", Query.Direction.DESCENDING);

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    if(
                            (Objects.equals(document.get("department"), CommonClass.modelUserData.getDepartment()))
                            && (Objects.equals(document.get("task_plan_authority"), CommonClass.modelUserData.getFull_name())
                            || Objects.equals(document.get("action_taker"), "All Faculty")
                            || Objects.equals(document.get("action_taker"), CommonClass.modelUserData.getFull_name())
                            || Objects.equals(document.get("added_by"), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                    ){
                        UserTasks userTasks = document.toObject(UserTasks.class);
                        int colorTask = R.color.completed;
                        if(userTasks!=null && !userTasks.isCompleted()){
                            try {
                                String start = userTasks.getStart_date();
                                String end = userTasks.getEnd_date();
                                String status = commonClass.getTasksStatus(start, end);
                                switch (status){
                                    case "Upcoming Tasks":
                                        colorTask = R.color.upcoming;
                                        tasksCount.setUpcoming(Integer.parseInt(tasksCount.getUpcoming())+1);
                                        break;
                                    case "In Progress":
                                        colorTask = R.color.inProgress;
                                        tasksCount.setIn_progress(Integer.parseInt(tasksCount.getIn_progress())+1);
                                        break;
                                    case "Not Complete":
                                        colorTask = R.color.notComplete;
                                        tasksCount.setNot_completed(Integer.parseInt(tasksCount.getNot_completed())+1);
                                }
                                userTasks.setColor(ContextCompat.getColor(this, colorTask));
                                userTasks.setStatus(status);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Objects.requireNonNull(userTasks).setStatus("Completed Tasks");
                            userTasks.setColor(ContextCompat.getColor(this, colorTask));
                            tasksCount.setCompleted(Integer.parseInt(tasksCount.getCompleted())+1);
                        }

                        userTasksList.add(userTasks);

                    }
                }

                activityFacultyBinding.progressBar.setVisibility(View.GONE);

                if(userTasksList.isEmpty()){
                    activityFacultyBinding.taskRecyclerView.setVisibility(View.GONE);
                    activityFacultyBinding.noTasks.setVisibility(View.VISIBLE);
                }else{
                    activityFacultyBinding.noTasks.setVisibility(View.GONE);
                    activityFacultyBinding.taskRecyclerView.setVisibility(View.VISIBLE);
                    //initialize the adapter class
                    UserTasksAdapter userTasksAdapter = new UserTasksAdapter(userTasksList, this);

                    //set the adapter for the recycler view
                    activityFacultyBinding.taskRecyclerView.setAdapter(userTasksAdapter);
                }
            }
        });
    }

    private void subscribeToFirebaseTopic() {
        //get firebase notifications
        FirebaseMessaging.getInstance().subscribeToTopic("All Faculty"+CommonClass.modelUserData.getDepartment())
                .addOnCompleteListener(task -> {
                    String msg = "Done";
                    if (!task.isSuccessful()) {
                        msg = "Failed";
                    }
                    Log.e("FacultyActivity", msg);
                });
        FirebaseMessaging.getInstance().subscribeToTopic(CommonClass.modelUserData.getFull_name()+CommonClass.modelUserData.getDepartment())
                .addOnCompleteListener(task -> {
                    String msg = "Done";
                    if (!task.isSuccessful()) {
                        msg = "Failed";
                    }
                    Log.e("FacultyActivity", msg);
                });
    }

    //remove all previous activities from stack if any and close the application
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}