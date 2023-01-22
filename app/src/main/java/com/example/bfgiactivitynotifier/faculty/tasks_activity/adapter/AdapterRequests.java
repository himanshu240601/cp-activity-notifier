package com.example.bfgiactivitynotifier.faculty.tasks_activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.RequestCardBinding;
import com.example.bfgiactivitynotifier.faculty.tasks_activity.RequestActivity;
import com.example.bfgiactivitynotifier.firebasecloudmessaging.MyFirebaseNotificationSender;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.TasksViewHolder> {

    private final List<UserTasks> userTasksListAdapter;
    private final Context ctx;

    public AdapterRequests(List<UserTasks> userTasksListAdapter, Context ctx) {
        this.userTasksListAdapter = userTasksListAdapter;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public AdapterRequests.TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RequestCardBinding tasksCardBinding = DataBindingUtil.inflate(layoutInflater, R.layout.request_card, parent, false);
        return new AdapterRequests.TasksViewHolder(tasksCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRequests.TasksViewHolder holder, int position) {
        holder.tasksCardBinding.setReqObject(userTasksListAdapter.get(position));

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

        holder.tasksCardBinding.approved.setOnClickListener(v -> new AlertDialog.Builder(ctx)
                .setTitle("Approve Request")
                .setMessage("Are you sure?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    final ArrayList<String> delayReason = new ArrayList<>();
                    if(userTasksListAdapter.get(position).getDelay_reason()!=null && userTasksListAdapter.get(position).getDelay_reason().isEmpty()){
                        delayReason.addAll(userTasksListAdapter.get(position).getDelay_reason());
                    }
                    HashMap<String, Object> docData = new HashMap<>();
                    docData.put("task_plan_authority", userTasksListAdapter.get(position).getTask_plan_authority());
                    docData.put("task_name", userTasksListAdapter.get(position).getTask_name());
                    docData.put("task_type", userTasksListAdapter.get(position).getTask_type());
                    docData.put("action_taker", userTasksListAdapter.get(position).getAction_taker());
                    docData.put("follow_up_taken_by", userTasksListAdapter.get(position).getFollow_up_taken_by());
                    docData.put("start_date", userTasksListAdapter.get(position).getStart_date());
                    docData.put("end_date", userTasksListAdapter.get(position).getEnd_date());
                    docData.put("last_updated", new Timestamp(new Date()));

                    delayReason.add(userTasksListAdapter.get(position).getReq_change());
                    docData.put("delay_reason", FieldValue.arrayUnion(delayReason.get(delayReason.size()-1)));

                    FirebaseFirestore.getInstance().collection("activities_data")
                            .document(userTasksListAdapter.get(position).getDocument_id()).update(docData)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(ctx, "Approved!", Toast.LENGTH_SHORT).show();
                                }
                            });

                    //send push notification to all the user about event
                    sendNotification(holder.getAbsoluteAdapterPosition(), true, "Changes Approved - ");

                })
                .setNegativeButton(android.R.string.no, null)
                .show());

        holder.tasksCardBinding.declined.setOnClickListener(v -> new AlertDialog.Builder(ctx)
                    .setTitle("Decline Request")
                    .setMessage("Are you sure?")
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {

                        Toast.makeText(ctx, "Declined!", Toast.LENGTH_SHORT).show();

                        //send push notification to all the user about event
                        sendNotification(holder.getAbsoluteAdapterPosition(), false, "Changes Declined - ");
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show());
    }

    private void deleteTaskFromDbRequest(int pos) {
        Query documentReference = FirebaseFirestore.getInstance().collection("faculty_data")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("task_requests").whereEqualTo("req_id", userTasksListAdapter.get(pos).getDocument_id());

        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                List<DocumentSnapshot> list = value.getDocuments();
                //delete from requests
                list.get(list.size()-1).getReference().delete();
                RequestActivity.userTasksList.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    private void sendNotification(int pos, boolean b, String op){
        //send first notification to the person
        //by whom the activity change is requested
        String topic = userTasksListAdapter.get(pos).getAdded_by_name() + CommonClass.modelUserData.getDepartment();
        MyFirebaseNotificationSender myFirebaseNotificationSender =
                new MyFirebaseNotificationSender("Task Notifier", op+userTasksListAdapter.get(pos).getTask_name(), topic, ctx);
        myFirebaseNotificationSender.sendNotification("UPDATE");

        //if changes are approved
        //send changes to others users associated
        //with the activity
        if(b){
            topic = userTasksListAdapter.get(pos).getAction_taker() + CommonClass.modelUserData.getDepartment();
            myFirebaseNotificationSender =
                    new MyFirebaseNotificationSender("Task Notifier", userTasksListAdapter.get(pos).getTask_name(), topic, ctx);
            myFirebaseNotificationSender.sendNotification("UPDATE");
        }

        deleteTaskFromDbRequest(pos);
    }

    @Override
    public int getItemCount() {
        return userTasksListAdapter.size();
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder{

        RequestCardBinding tasksCardBinding;

        public TasksViewHolder(@NonNull RequestCardBinding itemView) {
            super(itemView.getRoot());

            tasksCardBinding = itemView;
        }
    }
}

