package com.example.bfgiactivitynotifier.faculty.add_new_post;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.ActivityAddNewPostBinding;
import com.example.bfgiactivitynotifier.faculty.add_new_post.models.ModelForm;
import com.example.bfgiactivitynotifier.firebasecloudmessaging.MyFirebaseNotificationSender;
import com.example.bfgiactivitynotifier.models.UserModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddNewPostActivity extends AppCompatActivity {

    ActivityAddNewPostBinding activityAddNewPostBinding;

    private final ModelForm modelForm = new ModelForm();

    private final ArrayList<String> faculty = new ArrayList<>();
    private final ArrayList<String> taskType = new ArrayList<>();
    private final ArrayList<String> followUpTakenBy = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNewPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_post);

        //close activity on back button click
        activityAddNewPostBinding.backButton.setOnClickListener(view -> finish());

        activityAddNewPostBinding.setModelObject(modelForm);

        fetchFacultyDataFromDB();

        setAutoCompleteForTaskType();
        setAutoCompleteForFollowUp();

        //update the start date in form
        activityAddNewPostBinding.startDate.setOnClickListener(view -> {
            modelForm.getDateFromDialog("Select Start Date", getSupportFragmentManager(), modelForm);
            //remove focus from other edittext
            removeFocus();
        });

        //update the end date in form
        activityAddNewPostBinding.endDate.setOnClickListener(view -> {
            modelForm.getDateFromDialog("Select End Date", getSupportFragmentManager(),modelForm);
            //remove focus from other edittext
            removeFocus();
        });

        activityAddNewPostBinding.publishButton.setOnClickListener(view -> publishEvent());
    }

    private void setAutoCompleteForFollowUp() {
        followUpTakenBy.add("HOD CSE");
        ArrayAdapter<String> adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,followUpTakenBy);
        activityAddNewPostBinding.followUpTakenBy.setAdapter(adapter1);
    }

    private void setAutoCompleteForTaskType() {
        taskType.add("Academics");
        taskType.add("Ranking");
        ArrayAdapter<String> adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,taskType);
        activityAddNewPostBinding.typeOfTask.setAdapter(adapter1);
    }

    @SuppressLint("SetTextI18n")
    private void fetchFacultyDataFromDB() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = firebaseFirestore.collection("faculty_data");

        List<UserModel> userModels = new ArrayList<>();

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    userModels.add(
                            new UserModel(
                                    document.getString("name"),
                                    document.getString("designation"),
                                    document.getString("department")
                            )
                    );
                }
                for(int i=0; i<userModels.size(); i++){
                    faculty.add(userModels.get(i).getFull_name());
                }

                //setting autocomplete textview for task planning authority
                AutoCompleteTextView autoCompleteTextView = activityAddNewPostBinding.taskPlanAuthority;
                ArrayAdapter<String> adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,faculty);
                autoCompleteTextView.setAdapter(adapter1);

                autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                    int pos = faculty.indexOf(autoCompleteTextView.getText().toString());

                    activityAddNewPostBinding.taskPlanAuthorityDesignation.setVisibility(View.VISIBLE);
                    activityAddNewPostBinding.taskPlanAuthorityDesignation.setText(
                            userModels.get(pos).getDesignation() + " - " + userModels.get(pos).getDepartment()
                    );
                });

                //setting autocomplete textview for responsible persons
                ArrayList<String> responsible_person = new ArrayList<>(faculty);
                responsible_person.add(0, "All Faculty");
                ArrayAdapter<String> adapter2=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,responsible_person);
                activityAddNewPostBinding.actionTaker.setAdapter(adapter2);
            }
        });
    }

    //function to remove focus from other input fields
    //when the start date and end date is selected
    private void removeFocus() {
        activityAddNewPostBinding.taskPlanAuthority.clearFocus();
        activityAddNewPostBinding.nameOfTask.clearFocus();
        activityAddNewPostBinding.typeOfTask.clearFocus();
        activityAddNewPostBinding.actionTaker.clearFocus();
        activityAddNewPostBinding.followUpTakenBy.clearFocus();
    }

    //on clicking the publish button
    //the task/activity/work is published in the app
    //add the task to database and send as a push
    //notification of the new task/activity/work
    @SuppressLint("NotifyDataSetChanged")
    private void publishEvent(){
        activityAddNewPostBinding.publishButton.setEnabled(false);
        //get all the data
        String auth = activityAddNewPostBinding.taskPlanAuthority.getText().toString();
        String task = Objects.requireNonNull(activityAddNewPostBinding.nameOfTask.getText()).toString();
        String type = activityAddNewPostBinding.typeOfTask.getText().toString();
        String resp = activityAddNewPostBinding.actionTaker.getText().toString();
        String follow = activityAddNewPostBinding.followUpTakenBy.getText().toString();
        String start = Objects.requireNonNull(activityAddNewPostBinding.startDate.getText()).toString();
        String end = Objects.requireNonNull(activityAddNewPostBinding.endDate.getText()).toString();

        //check if all data is entered correctly
        if(validate(auth, task, type, resp, follow, start, end)){
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            //add data to firebase fire store in the activities_data collection
            //store the dat into a map of type <string, string>
            DocumentReference documentReference = firebaseFirestore.collection("activities_data").document();
            Map<String, Object> docData = addData(auth, task, type, resp, follow, start, end);
            documentReference.set(docData).
                    addOnCompleteListener(this, task1 -> {
                        if(task1.isSuccessful()){
                            Toast.makeText(this, "Activity added successfully!", Toast.LENGTH_SHORT).show();
                            //send push notification to all the user about event
                            MyFirebaseNotificationSender myFirebaseNotificationSender = new MyFirebaseNotificationSender("Activity Notifier", task,"tasks", getApplicationContext());
                            myFirebaseNotificationSender.sendNotification("NEW");
                            finish();
                        }else{
                            task1.addOnFailureListener(this, e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                        }
                        activityAddNewPostBinding.publishButton.setEnabled(true);
                    });
        }else{
            Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
            activityAddNewPostBinding.publishButton.setEnabled(true);
        }
    }

    private HashMap<String, Object> addData(
            String s1, String s2, String s3, String s4, String s5, String s6, String s7
    ){
        HashMap<String, Object> docData = new HashMap<>();

        //basic information
        docData.put("task_plan_authority", s1);
        docData.put("task_name", s2);
        docData.put("task_type", s3);
        docData.put("action_taker", s4);
        docData.put("follow_up_taken_by", s5);
        docData.put("start_date", s6);
        docData.put("end_date", s7);

        //additional information
        docData.put("added_on", new Timestamp(new Date()));
        docData.put("added_by", FirebaseAuth.getInstance().getUid());
        docData.put("last_updated", new Timestamp(new Date()));
        docData.put("completed", false);

        return docData;
    }

    //validate the input by the user
    private boolean validate(
            String string1, String string2, String string3, String string4,
            String string5, String string6, String string7) {
        //if any of the input fields is empty then
        //false is returned otherwise true
        return !string1.isEmpty() && !string2.isEmpty() && !string3.isEmpty() && !string4.isEmpty() && !string5.isEmpty() && !string6.isEmpty() && !string7.isEmpty();
    }
}