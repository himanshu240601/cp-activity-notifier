package com.example.bfgiactivitynotifier.faculty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.TasksCardBinding;
import com.example.bfgiactivitynotifier.models.UserTasks;

import java.util.List;

public class UserTasksAdapter extends RecyclerView.Adapter<UserTasksAdapter.TasksViewHolder> {

    private final List<UserTasks> userTasksList;
    private final Context context;

    public UserTasksAdapter(List<UserTasks> userTasksList, Context context){
        this.userTasksList = userTasksList;
        this.context = context;
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
