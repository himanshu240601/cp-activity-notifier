package com.example.bfgiactivitynotifier.faculty.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.TaskBottomSheetBinding;
import com.example.bfgiactivitynotifier.databinding.TasksCardBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.faculty.add_new_post.AddNewPostActivity;
import com.example.bfgiactivitynotifier.faculty.tasks_activity.TasksActivity;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserTasksAdapter extends RecyclerView.Adapter<UserTasksAdapter.TasksViewHolder> {

    private final List<UserTasks> userTasksListAdapter;
    private final Activity context;
    private TasksActivity ctx;
    private final boolean tasksListView;

    public UserTasksAdapter(List<UserTasks> userTasksList, Activity context, Boolean tasksListView, TasksActivity ctx){
        this.userTasksListAdapter = userTasksList;
        this.context = context;
        this.tasksListView = tasksListView;
        this.ctx = ctx;
    }

    public UserTasksAdapter(List<UserTasks> userTasksList, Activity context, Boolean tasksListView){
        this.userTasksListAdapter = userTasksList;
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
        holder.tasksCardBinding.setTaskObject(userTasksListAdapter.get(position));

        //set margin of the last card
        if(userTasksListAdapter.size()!=0 && holder.getAbsoluteAdapterPosition()==0){
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            holder.tasksCardBinding.linearLayout.setLayoutParams(params);
        }

        if(userTasksListAdapter.size()!=1 && holder.getAbsoluteAdapterPosition()==userTasksListAdapter.size()-1){
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 40);
            holder.tasksCardBinding.linearLayout.setLayoutParams(params);
        }

        holder.tasksCardBinding.linearLayout.setOnClickListener(view-> openBottomSheetDialog(holder.getAbsoluteAdapterPosition()));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
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

        taskBottomSheetBinding.setTaskObject(userTasksListAdapter.get(pos));

        if(userTasksListAdapter.get(pos).getAdded_by().equals(FirebaseAuth.getInstance().getUid())){
            taskBottomSheetBinding.dividerLine.setVisibility(View.VISIBLE);
            taskBottomSheetBinding.removeTask.setVisibility(View.VISIBLE);
            taskBottomSheetBinding.setCompleted.setVisibility(View.VISIBLE);
            if(userTasksListAdapter.get(pos).isCompleted()) {
                taskBottomSheetBinding.editTask.setVisibility(View.GONE);
                taskBottomSheetBinding.setCompleted.setEnabled(false);
            }else{
                taskBottomSheetBinding.editTask.setVisibility(View.VISIBLE);
            }

        }

        if(userTasksListAdapter.get(pos).getDelay_reason()!=null && !userTasksListAdapter.get(pos).getDelay_reason().isEmpty()){
            taskBottomSheetBinding.reasonTopBorder.setVisibility(View.VISIBLE);
            taskBottomSheetBinding.resonOfDelay.setVisibility(View.VISIBLE);
            taskBottomSheetBinding.resonOfDelayText.setVisibility(View.VISIBLE);
            ArrayList<String> reasons = userTasksListAdapter.get(pos).getDelay_reason();
            StringBuilder delays = new StringBuilder();
            for (String rea:
                 reasons) {
                if(rea.equals(reasons.get(reasons.size()-1))){
                    delays.append(rea);
                }else{
                    delays.append(rea).append("\n");
                }
            }
            taskBottomSheetBinding.resonOfDelayText.setText(delays);
        }

        taskBottomSheetBinding.editTask.setOnClickListener(view-> {
            Intent intent = new Intent(context, AddNewPostActivity.class);
            intent.putExtra("position",pos);
            context.startActivity(intent);

            bottomSheetDialog.dismiss();
        });

        taskBottomSheetBinding.removeTask.setOnClickListener(view-> new AlertDialog.Builder(context)
                .setTitle("Remove Task")
                .setMessage("Are you sure?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> FirebaseFirestore.getInstance().collection("activities_data")
                        .document(userTasksListAdapter.get(pos).getDocument_id()).delete()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Task Removed!", Toast.LENGTH_SHORT).show();

                                bottomSheetDialog.dismiss();

                                //remove from the list
                                if(!TasksActivity.list.isEmpty()){
                                    for(int j=0;j<FacultyActivity.userTasksList.size();j++){
                                        if(Objects.equals(TasksActivity.list.get(pos).getDocument_id(), FacultyActivity.userTasksList.get(j).getDocument_id())){
                                            FacultyActivity.removedPosition = j;
                                            break;
                                        }
                                    }
                                    TasksActivity.list.remove(pos);
                                }else{
                                    FacultyActivity.userTasksList.remove(pos);
                                    new FacultyActivity().categoriesTasksCount();
                                }
                                notifyItemRemoved(pos);
                            }
                        }))
                .setNegativeButton(android.R.string.no, null)
                .show());

        taskBottomSheetBinding.setCompleted.setOnClickListener(view->new AlertDialog.Builder(context)
                .setTitle("Complete Task")
                .setMessage("Is this task complete?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("completed", true);
                    FirebaseFirestore.getInstance().collection("activities_data")
                            .document(userTasksListAdapter.get(pos).getDocument_id()).update(hashMap)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Task Completed!", Toast.LENGTH_SHORT).show();

                                    if (!TasksActivity.list.isEmpty()){
                                        ctx.checkFunction(userTasksListAdapter.get(pos).getDocument_id());
                                    }

                                    bottomSheetDialog.dismiss();
                                }
                            });
                })
                .setNegativeButton(android.R.string.no, null)
                .show());

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return userTasksListAdapter.size();
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder{

        TasksCardBinding tasksCardBinding;

        public TasksViewHolder(@NonNull TasksCardBinding itemView) {
            super(itemView.getRoot());

            tasksCardBinding = itemView;
        }
    }
}
