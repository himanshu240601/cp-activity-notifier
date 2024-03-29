package com.example.bfgiactivitynotifier.faculty;

import android.annotation.SuppressLint;
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
import com.example.bfgiactivitynotifier.faculty.tasks_activity.RequestActivity;
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

    public static int removedPosition = -1;
    @SuppressLint("StaticFieldLeak")
    private static ActivityFacultyBinding activityFacultyBinding;

    private final CommonClass commonClass = new CommonClass();

    public static final List<UserTasks> userTasksList = new ArrayList<>();

    private static boolean loadDataFirstTime = true;

    @Override
    protected void onRestart() {
        super.onRestart();
        if(removedPosition!=-1){
            userTasksList.remove(removedPosition);
            userTasksAdapter.notifyItemRemoved(removedPosition);
            removedPosition = -1;
            categoriesTasksCount();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFacultyBinding = DataBindingUtil.setContentView(this, R.layout.activity_faculty);

        loadDataFirstTime = true;

        subscribeToFirebaseTopic();

        activityFacultyBinding.setUserObject(CommonClass.modelUserData);

        if(CommonClass.modelUserData.getDesignation().equals("HOD")){
            activityFacultyBinding.taskRequests.setVisibility(View.VISIBLE);
        }

        activityFacultyBinding.taskRequests.setOnClickListener(v -> startActivity(new Intent(FacultyActivity.this, RequestActivity.class)));

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
        activityFacultyBinding.taskRecyclerView.setHasFixedSize(true);

        getTasksData();
    }

    private UserTasksAdapter userTasksAdapter;

    @SuppressLint("NotifyDataSetChanged")
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
                            UserTasks userTasks = getData(document);
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

                            userTasksList.sort((o1, o2) -> o2.getAdded_on().compareTo(o1.getAdded_on()));

                            userTasksAdapter = new UserTasksAdapter(userTasksList, this, false);

                            //set the adapter for the recycler view
                            activityFacultyBinding.taskRecyclerView.setAdapter(userTasksAdapter);
                            loadDataFirstTime = false;
                        }
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
                            UserTasks userTasks = getData(doc_snapshot);
                            if(userTasks!=null){
                                if(add){
                                    //nothing to change
                                    //add function will only be using
                                    //this activity only
                                    //the tasks activity is updated
                                    //when opened everytime
                                    userTasksList.add(0, userTasks);
                                    userTasksAdapter.notifyItemInserted(0);
                                    activityFacultyBinding.taskRecyclerView.scrollToPosition(0);
                                }else{
                                    if (!TasksActivity.list.isEmpty()){
                                        TasksActivity.changedPosition = i;
                                    }
                                    userTasksList.set(i, userTasks);
                                    userTasksAdapter.notifyItemChanged(i);
                                }
                            }
                        }
                    }
                    categoriesTasksCount();
                });
    }

    public void categoriesTasksCount(){

        TasksCount tasksCount = new TasksCount();
        for(UserTasks userTasks : userTasksList){
            switch (userTasks.getStatus()){
                case "Upcoming Tasks":
                    tasksCount.setUpcoming(Integer.parseInt(tasksCount.getUpcoming())+1);
                    break;
                case "In Progress":
                    tasksCount.setIn_progress(Integer.parseInt(tasksCount.getIn_progress())+1);
                    break;
                case "Not Complete":
                    tasksCount.setNot_completed(Integer.parseInt(tasksCount.getNot_completed())+1);
                    break;
                default:
                    tasksCount.setCompleted(Integer.parseInt(tasksCount.getCompleted())+1);

            }
        }
        activityFacultyBinding.setTaskCount(tasksCount);
    }

    private UserTasks getData(DocumentSnapshot document) {
        UserTasks userTasks = null;
        String auth = CommonClass.modelUserData.getDesignation();
        if( checkUserCriteria((String) document.get("task_type"))
                || (
                CommonClass.modelUserData.getDepartment().equals(document.get("department"))
            && CommonClass.checkDateRange(
                    Objects.requireNonNull(document.get("start_date")).toString(),
                    Objects.requireNonNull(document.get("end_date")).toString())
            &&
            (Objects.equals(document.get("task_plan_authority"), CommonClass.modelUserData.getFull_name())
                    || Objects.equals(document.get("action_taker"), CommonClass.modelUserData.getFull_name())
                    || Objects.equals(document.get("action_taker"), "All Faculty")
             || Objects.equals(document.get("added_by"), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    || auth.equals("Principal")
                    || (auth.equals("Faculty") && Objects.equals(document.get("added_by_designation"), auth))
                    || (auth.equals("HOD") && (Objects.equals(document.get("added_by_designation"), auth) || Objects.equals(document.get("added_by_designation"), "Faculty")))
                    || (auth.equals("Dean") && (!Objects.equals(document.get("added_by_designation"), "Principal")))
            )
        )){
            userTasks = document.toObject(UserTasks.class);
            Objects.requireNonNull(userTasks).setDocument_id(document.getId());
            int colorTask = R.color.completed;
            if(!userTasks.isCompleted()){
                try {
                    String start = userTasks.getStart_date();
                    String end = userTasks.getEnd_date();
                    String status = commonClass.getTasksStatus(start, end);
                    switch (status){
                        case "Upcoming Tasks":
                            colorTask = R.color.upcoming;
                            break;
                        case "In Progress":
                            colorTask = R.color.inProgress;
                            break;
                        case "Not Complete":
                            colorTask = R.color.notComplete;
                    }
                    userTasks.setColor(ContextCompat.getColor(this, colorTask));
                    userTasks.setStatus(status);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                Objects.requireNonNull(userTasks).setStatus("Completed Tasks");
                userTasks.setColor(ContextCompat.getColor(this, colorTask));
            }

        }
        return userTasks;
    }

    private boolean checkUserCriteria(String taskType) {

        Boolean[] returnBool = new Boolean[]{false};

        FirebaseFirestore.getInstance().collection("faculty_data")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get().addOnCompleteListener(task -> {
                    String criteria = (String) task.getResult().get("criteria");
                    if(criteria != null && Objects.requireNonNull(criteria).equals(taskType)){
                        returnBool[0] = true;
                    }
                });

        return returnBool[0];
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