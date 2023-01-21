package com.example.bfgiactivitynotifier.faculty.tasks_activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.ActivityRequestBinding;
import com.example.bfgiactivitynotifier.faculty.tasks_activity.adapter.AdapterRequests;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestActivity extends AppCompatActivity {

    private ActivityRequestBinding activityRequestBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRequestBinding = DataBindingUtil.setContentView(this, R.layout.activity_request);

        getTasksData();
    }

    public static final ArrayList<UserTasks> userTasksList = new ArrayList<>();
    private void getTasksData() {
        userTasksList.clear();
        FirebaseFirestore.getInstance()
                .collection("faculty_data")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("task_requests")
                .orderBy("added_on", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    List<DocumentSnapshot> documentSnapshots = Objects.requireNonNull(value).getDocuments();
                    for (DocumentSnapshot document : documentSnapshots) {
                        getData(
                                Objects.requireNonNull(document.get("req_id")).toString(),
                                Objects.requireNonNull(document.get("req_change")).toString()
                        );
                    }

                    activityRequestBinding.progressBar.setVisibility(View.GONE);
                    if(!userTasksList.isEmpty()){
                        activityRequestBinding.noTasks.setVisibility(View.GONE);
                        activityRequestBinding.recyclerViewAllRequests.setVisibility(View.VISIBLE);

                        AdapterRequests userTasksAdapter = new AdapterRequests(userTasksList, this);
                        activityRequestBinding.recyclerViewAllRequests.setLayoutManager(new LinearLayoutManager(this));
                        activityRequestBinding.recyclerViewAllRequests.setAdapter(userTasksAdapter);
                    }else{
                        activityRequestBinding.recyclerViewAllRequests.setVisibility(View.GONE);
                        activityRequestBinding.noTasks.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void getData(String req_id, String req_change) {

        FirebaseFirestore.getInstance().collection("activities_data")
                .document(req_id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        UserTasks userTasks = snapshot.toObject(UserTasks.class);
                        if(userTasks!=null){
                            userTasks.setReq_change(req_change);
                        }
                        userTasksList.add(userTasks);
                    }
                });
    }
}