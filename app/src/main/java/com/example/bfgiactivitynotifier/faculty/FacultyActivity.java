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
import com.google.firebase.firestore.DocumentChange;
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

    public static TasksCount tasksCount;

    public static final List<UserTasks> userTasksList = new ArrayList<>();

    private static boolean loadDataFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFacultyBinding = DataBindingUtil.setContentView(this, R.layout.activity_faculty);

        tasksCount = new TasksCount();

        loadDataFirstTime = true;

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

    private UserTasksAdapter userTasksAdapter;

    private void getTasksData(){
        if (loadDataFirstTime){
            activityFacultyBinding.progressBar.setVisibility(View.VISIBLE);
        }
        FirebaseFirestore.getInstance().collection("activities_data").orderBy("added_on", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    List<DocumentSnapshot> documentSnapshots = Objects.requireNonNull(value).getDocuments();
                    if(loadDataFirstTime){
                        userTasksList.clear();

                        for (DocumentSnapshot document : documentSnapshots) {
                            UserTasks userTasks = getData(document, true);
                            if(userTasks!=null){
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
                            userTasksAdapter = new UserTasksAdapter(userTasksList, this, false);

                            //set the adapter for the recycler view
                            activityFacultyBinding.taskRecyclerView.setAdapter(userTasksAdapter);
                        }

                        loadDataFirstTime = false;
                    }else{
                        DocumentChange documentChange = value.getDocumentChanges().get(0);

                        DocumentSnapshot doc_snapshot = null;
                        String path = documentChange.getDocument().getId();
                        for(DocumentSnapshot snapshot : documentSnapshots){
                            String id = snapshot.getId();
                            if(path.contains(id)){
                                doc_snapshot = snapshot;
                                break;
                            }
                        }

                        if(doc_snapshot!=null){
                            int i;
                            boolean add = true;
                            for(i=0;i<userTasksList.size();i++) {
                                if (Objects.equals(doc_snapshot.get("added_on"), userTasksList.get(i).getAdded_on())) {
                                    add =false;
                                    break;
                                }
                            }
                            UserTasks userTasks = getData(doc_snapshot, add);
                            if(userTasks!=null){
                                if(add){
                                    userTasksList.add(0, userTasks);
                                    userTasksAdapter.notifyItemInserted(0);
                                    activityFacultyBinding.taskRecyclerView.scrollToPosition(0);
                                }else{
                                    userTasksList.set(i, userTasks);
                                    userTasksAdapter.notifyItemChanged(i);
                                    activityFacultyBinding.taskRecyclerView.scrollToPosition(i);
                                }
                            }
                        }
                    }
                });
    }

    private UserTasks getData(DocumentSnapshot document, boolean count) {
        UserTasks userTasks = null;
        if(
                (Objects.equals(document.get("task_plan_authority"), CommonClass.modelUserData.getFull_name())
                        || Objects.equals(document.get("action_taker"), "All Faculty")
                        || Objects.equals(document.get("action_taker"), CommonClass.modelUserData.getFull_name())
                        || Objects.equals(document.get("added_by"), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
        ){
            userTasks = document.toObject(UserTasks.class);

            int colorTask = R.color.completed;
            if(userTasks!=null && !userTasks.isCompleted()){
                userTasks.setDocument_id(document.getId());
                try {
                    String start = userTasks.getStart_date();
                    String end = userTasks.getEnd_date();
                    String status = commonClass.getTasksStatus(start, end);
                    switch (status){
                        case "Upcoming Tasks":
                            colorTask = R.color.upcoming;
                            if(count){
                                tasksCount.setUpcoming(Integer.parseInt(tasksCount.getUpcoming())+1);
                            }
                            break;
                        case "In Progress":
                            colorTask = R.color.inProgress;
                            if(count){
                                tasksCount.setIn_progress(Integer.parseInt(tasksCount.getIn_progress())+1);
                            }
                            break;
                        case "Not Complete":
                            colorTask = R.color.notComplete;
                            if(count){
                                tasksCount.setNot_completed(Integer.parseInt(tasksCount.getNot_completed())+1);
                            }
                    }
                    userTasks.setColor(ContextCompat.getColor(this, colorTask));
                    userTasks.setStatus(status);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                Objects.requireNonNull(userTasks).setStatus("Completed Tasks");
                userTasks.setColor(ContextCompat.getColor(this, colorTask));
                if(count){
                    tasksCount.setCompleted(Integer.parseInt(tasksCount.getCompleted())+1);
                }
            }

        }
        return userTasks;
    }

    private void subscribeToFirebaseTopic() {
        //get firebase notifications
        String username = CommonClass.modelUserData.getFull_name().replaceAll(" ", "_");
        String department = CommonClass.modelUserData.getDepartment().replaceAll(" ", "_");
        FirebaseMessaging.getInstance().subscribeToTopic("All_Faculty"+department)
                .addOnCompleteListener(task -> {
                    String msg = "Done";
                    if (!task.isSuccessful()) {
                        msg = "Failed";
                    }
                    Log.e("FacultyActivity", msg);
                });
        FirebaseMessaging.getInstance().subscribeToTopic(username+department)
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