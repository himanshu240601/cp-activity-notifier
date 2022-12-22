package com.example.bfgiactivitynotifier.faculty.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.TaskBottomSheetBinding;
import com.example.bfgiactivitynotifier.databinding.TasksCardBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.faculty.models.TasksCount;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class UserTasksAdapter extends RecyclerView.Adapter<UserTasksAdapter.TasksViewHolder> {

    private final List<UserTasks> userTasksList;
    private final Activity context;
    private final boolean tasksListView;

    public UserTasksAdapter(List<UserTasks> userTasksList, Activity context, Boolean tasksListView){
        this.userTasksList = userTasksList;
        this.context = context;
        this.tasksListView = tasksListView;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TasksCardBinding tasksCardBinding = DataBindingUtil.inflate(layoutInflater, R.layout.tasks_card, parent, false);
        return new UserTasksAdapter.TasksViewHolder(tasksCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {
        holder.tasksCardBinding.setTaskObject(userTasksList.get(position));

        //set margin of the last card and first card
        if(tasksListView && position==0){
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 40, 8, 8);
            holder.tasksCardBinding.linearLayout.setLayoutParams(params);
        }
        if(userTasksList.size()!=1 && position==userTasksList.size()-1){
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 40);
            holder.tasksCardBinding.linearLayout.setLayoutParams(params);
        }

        holder.tasksCardBinding.linearLayout.setOnClickListener(view-> openBottomSheetDialog(position));
    }

    private void openBottomSheetDialog(int pos) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        TaskBottomSheetBinding taskBottomSheetBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.task_bottom_sheet,
                null,
                false
        );
        bottomSheetDialog.setContentView(taskBottomSheetBinding.getRoot());

        taskBottomSheetBinding.setTaskObject(userTasksList.get(pos));

        if(userTasksList.get(pos).getAdded_by().equals(FirebaseAuth.getInstance().getUid())){
            taskBottomSheetBinding.actionButtons.setVisibility(View.VISIBLE);
            taskBottomSheetBinding.setCompleted.setVisibility(View.VISIBLE);
            if(!userTasksList.get(pos).isCompleted()){
                taskBottomSheetBinding.modifyTask.setVisibility(View.VISIBLE);
            }else{
                taskBottomSheetBinding.setCompleted.setEnabled(false);
            }

        }

        taskBottomSheetBinding.setCompleted.setOnClickListener(view->{
            taskBottomSheetBinding.setCompleted.setEnabled(false);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("completed", true);
            String status = userTasksList.get(pos).getStatus();
            FirebaseFirestore.getInstance().collection("activities_data")
                    .document(userTasksList.get(pos).getDocument_id()).update(hashMap)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Task Completed!", Toast.LENGTH_SHORT).show();
                            TasksCount tasksCount = FacultyActivity.tasksCount;
                            switch (status){
                                case "Upcoming Tasks":
                                    tasksCount.setUpcoming(Integer.parseInt(tasksCount.getUpcoming())-1);
                                    break;
                                case "In Progress":
                                    tasksCount.setIn_progress(Integer.parseInt(tasksCount.getUpcoming())-1);
                                    break;
                                case "Not Complete":
                                    tasksCount.setNot_completed(Integer.parseInt(tasksCount.getUpcoming())-1);
                            }

                            tasksCount.setCompleted(Integer.parseInt(tasksCount.getUpcoming())+1);

                            bottomSheetDialog.dismiss();
                        }
                    });
            taskBottomSheetBinding.setCompleted.setEnabled(true);
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return userTasksList.size();
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder{

        TasksCardBinding tasksCardBinding;

        public TasksViewHolder(@NonNull TasksCardBinding itemView) {
            super(itemView.getRoot());

            tasksCardBinding = itemView;
        }
    }
}
