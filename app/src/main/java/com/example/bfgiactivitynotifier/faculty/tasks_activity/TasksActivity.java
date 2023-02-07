package com.example.bfgiactivitynotifier.faculty.tasks_activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.ActivityTasksBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.faculty.adapters.UserTasksAdapter;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class TasksActivity extends AppCompatActivity {

    public static int changedPosition = -1;
    private UserTasksAdapter userTasksAdapter;

    public static final List<UserTasks> list = new ArrayList<>();

    @Override
    protected void onRestart() {
        super.onRestart();
        if(changedPosition!=-1){
            checkFunction(FacultyActivity.userTasksList.get(changedPosition).getDocument_id());
        }
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
    private String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTasksBinding = DataBindingUtil.setContentView(this, R.layout.activity_tasks);

        list.clear();

        activityTasksBinding.sortButton.setVisibility(View.GONE);
        activityTasksBinding.filterButton.setVisibility(View.GONE);

        //set text for top bar
        Intent intent = getIntent();
        text = intent.getStringExtra("textOnAction");
        activityTasksBinding.actionBarText.setText(text);

        //finish activity on back pressed
        activityTasksBinding.backButton.setOnClickListener(view-> onBackPressed());

        //open popup dialog to select sorting method for the list
        activityTasksBinding.sortButton.setOnClickListener(view-> showSortByPopUp());
        activityTasksBinding.filterButton.setOnClickListener(view-> showFilterPopUp());

        activityTasksBinding.progressBar.setVisibility(View.VISIBLE);
        if(text.equals("My Tasks")){
            list.addAll(FacultyActivity.userTasksList);
            setRecyclerView();
        }else if (text.equals("Tasks Added By Me")){
            getMyTasksFromFirebase();
        }
        else{
            for(UserTasks userTasks : FacultyActivity.userTasksList){
                if(userTasks.getStatus().equals(text)){
                    list.add(userTasks);
                }
            }
            setRecyclerView();
        }

    }

    private void setRecyclerView(){
        activityTasksBinding.progressBar.setVisibility(View.GONE);
        if(!list.isEmpty()){
            activityTasksBinding.noTasks.setVisibility(View.GONE);
            activityTasksBinding.recyclerViewAllTasks.setVisibility(View.VISIBLE);

            userTasksAdapter = new UserTasksAdapter(list, this, true, this);
            activityTasksBinding.recyclerViewAllTasks.setLayoutManager(new LinearLayoutManager(this));
            activityTasksBinding.recyclerViewAllTasks.setAdapter(userTasksAdapter);
        }else{
//            activityTasksBinding.sortButton.setVisibility(View.GONE);
//            activityTasksBinding.filterButton.setVisibility(View.GONE);
            activityTasksBinding.recyclerViewAllTasks.setVisibility(View.GONE);
            activityTasksBinding.noTasks.setVisibility(View.VISIBLE);
        }
    }

    private void getMyTasksFromFirebase() {
        FirebaseFirestore.getInstance().collection("activities_data").orderBy("added_on", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                        for (DocumentSnapshot documentSnapshot: documentSnapshotList){
                            UserTasks userTasks = documentSnapshot.toObject(UserTasks.class);
                            if(userTasks != null && Objects.equals(userTasks.getAdded_by(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){

                                Objects.requireNonNull(userTasks).setDocument_id(documentSnapshot.getId());
                                int colorTask = R.color.completed;
                                if(!userTasks.isCompleted()){
                                    try {
                                        String start = userTasks.getStart_date();
                                        String end = userTasks.getEnd_date();
                                        String status = new CommonClass().getTasksStatus(start, end);
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

                                list.add(userTasks);
                            }
                        }
                        setRecyclerView();
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    private void showFilterPopUp() {
        PopupMenu popupMenu = new PopupMenu(this, activityTasksBinding.filterButton);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.showAllTasks:
                    filterData("all");
                    Toast.makeText(this, "All Tasks", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.addedByMe:
                    filterData("user");
                    Toast.makeText(this, "Added by You", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
        popupMenu.inflate(R.menu.filter);
        popupMenu.setGravity(Gravity.END);
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterData(String all) {
        list.clear();
        if(all.equals("user")){
            for(UserTasks userTasks : FacultyActivity.userTasksList){
                if(userTasks.getStatus().equals(text) && userTasks.getAdded_by_name().equals(CommonClass.modelUserData.getFull_name())){
                    list.add(userTasks);
                }
            }
        }else{
            if(text.equals("My Tasks")){
                list.addAll(FacultyActivity.userTasksList);
            }else{
                for(UserTasks userTasks : FacultyActivity.userTasksList){
                    if(userTasks.getStatus().equals(text)){
                        list.add(userTasks);
                    }
                }
            }
        }
        userTasksAdapter.notifyDataSetChanged();
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    private void showSortByPopUp() {
        PopupMenu popupMenu = new PopupMenu(this, activityTasksBinding.sortButton);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.sortByLatest:
                    list.sort((o1, o2) -> o2.getAdded_on().compareTo(o1.getAdded_on()));
                    userTasksAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "sort by latest", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.sortByOldest:
                    list.sort(Comparator.comparing(UserTasks::getAdded_on));
                    userTasksAdapter.notifyDataSetChanged();
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