package com.example.bfgiactivitynotifier.faculty.add_new_post;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.ActivityAddNewPostBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.faculty.add_new_post.models.ModelForm;
import com.example.bfgiactivitynotifier.firebasecloudmessaging.MyFirebaseNotificationSender;
import com.example.bfgiactivitynotifier.models.UserModel;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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

    private final ArrayList<String> delayReason = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNewPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_post);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);

        //close activity on back button click
        activityAddNewPostBinding.backButton.setOnClickListener(view -> finish());

        activityAddNewPostBinding.setModelObject(modelForm);

        if(position!=-1){
            activityAddNewPostBinding.changeText.setVisibility(View.VISIBLE);
            activityAddNewPostBinding.delayTextBox.setVisibility(View.VISIBLE);

            UserTasks userTasks = FacultyActivity.userTasksList.get(position);
            activityAddNewPostBinding.taskPlanAuthority.setText(userTasks.getTask_plan_authority());
            activityAddNewPostBinding.nameOfTask.setText(userTasks.getTask_name());
            activityAddNewPostBinding.typeOfTask.setText(userTasks.getTask_type());
            activityAddNewPostBinding.actionTaker.setText(userTasks.getAction_taker());
            activityAddNewPostBinding.followUpTakenBy.setText(userTasks.getFollow_up_taken_by());
            modelForm.setStart_date(userTasks.getStart_date());
            modelForm.setEnd_date(userTasks.getEnd_date());

            if(userTasks.getDelay_reason()!=null && userTasks.getDelay_reason().isEmpty()){
                delayReason.addAll(userTasks.getDelay_reason());
            }

            activityAddNewPostBinding.publishButton.setText(R.string.save_changes);
        }

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

        activityAddNewPostBinding.publishButton.setOnClickListener(view -> {
            if(position==-1){
                publishEvent("added", position, "NEW");
            }else{
                publishEvent("updated", position, "UPDATE");
            }
        });
    }

    private void setAutoCompleteForFollowUp() {
        followUpTakenBy.add("HOD");
        followUpTakenBy.add("Dean");
        followUpTakenBy.add("Principal");
        ArrayAdapter<String> adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,followUpTakenBy);
        activityAddNewPostBinding.followUpTakenBy.setAdapter(adapter1);
    }

    private void setAutoCompleteForTaskType() {
        taskType.add("Academics");
        taskType.add("Ranking");
        taskType.add("DAA");
        taskType.add("DSW");
        taskType.add("NAAC");
        taskType.add("Criteria 1");
        taskType.add("Criteria 2");
        taskType.add("Criteria 3");
        taskType.add("Criteria 4");
        taskType.add("Criteria 5");
        ArrayAdapter<String> adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,taskType);
        activityAddNewPostBinding.typeOfTask.setAdapter(adapter1);
    }

    private final List<UserModel> userModels = new ArrayList<>();
    @SuppressLint("SetTextI18n")
    private void fetchFacultyDataFromDB() {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = firebaseFirestore.collection("faculty_data");

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
    private void publishEvent(String operation, int isNew, String dataType){
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

            if(isNew==-1){
                Map<String, Object> docData = addData(auth, task, type, resp, follow, start, end, true);
                DocumentReference documentReference = firebaseFirestore.collection("activities_data").document();
                documentReference.set(docData).
                        addOnCompleteListener(this, task1 -> checkCompletion(task1, operation, resp, task, dataType))
                        .addOnFailureListener(this, e -> {
                            activityAddNewPostBinding.publishButton.setEnabled(true);
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }else{
                if(Objects.equals(CommonClass.modelUserData.getDesignation(), "Faculty")){
                    //add data in "task_requests"
                    //with id and reason for change in request
                    //on request accepted or declined the data
                    //will be deleted from the collection
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("req_id", FacultyActivity.userTasksList.get(isNew).getDocument_id());
                    docData.put("req_change", " • "+CommonClass.modelUserData.getDate()+ " : "+Objects.requireNonNull(activityAddNewPostBinding.delayReason.getText()));

                    final FirebaseFirestore firebaseFirestoreAuth = FirebaseFirestore.getInstance();
                    final CollectionReference collectionReference = firebaseFirestoreAuth.collection("faculty_data");

                    collectionReference.get().addOnCompleteListener(taskHod -> {
                        //get the hod of the department similar to the current user
                        //the current user will be faculty only
                        if (taskHod.isSuccessful()) {
                            for (DocumentSnapshot document : taskHod.getResult()) {
                                if(Objects.requireNonNull(document.get("designation")).toString().equals("HOD")
                                        && Objects.requireNonNull(document.get("department")).toString().equals(CommonClass.modelUserData.getDepartment())){
                                    //add data in the collection
                                    //when the hod and department matches
                                    collectionReference
                                            .document(document.getId())
                                            .collection("task_requests")
                                            .document()
                                            .set(docData)
                                            .addOnCompleteListener(task13 -> {
                                                if(task13.isSuccessful()){
                                                    checkCompletion(task13, "Change Request Sent", (String) document.get("name"), "Task Change Request - "+ task13, dataType);
                                                }
                                            })
                                            .addOnFailureListener(this, e -> {
                                                activityAddNewPostBinding.publishButton.setEnabled(true);
                                                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }
                        }
                    });
                }else{
                    Map<String, Object> docData = addData(auth, task, type, resp, follow, start, end, false);
                    DocumentReference documentReference = firebaseFirestore.collection("activities_data").document(
                            FacultyActivity.userTasksList.get(isNew).getDocument_id()
                    );

                    documentReference.update(docData).
                            addOnCompleteListener(this, task12 -> checkCompletion(task12, operation, resp, task, dataType))
                            .addOnFailureListener(this, e -> {
                                activityAddNewPostBinding.publishButton.setEnabled(true);
                                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        }else{
            Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
            activityAddNewPostBinding.publishButton.setEnabled(true);
        }
    }

    private void checkCompletion(Task<Void> task1, String operation, String resp, String task, String dataType) {
        if(task1.isSuccessful()){
            Toast.makeText(this, "Task "+operation+" successfully!", Toast.LENGTH_SHORT).show();
            //send push notification to all the user about event
            String topic = resp+CommonClass.modelUserData.getDepartment();
            MyFirebaseNotificationSender myFirebaseNotificationSender =
                    new MyFirebaseNotificationSender("Task Notifier", task, topic, getApplicationContext());
            myFirebaseNotificationSender.sendNotification(dataType);
            finish();
        }
        activityAddNewPostBinding.publishButton.setEnabled(true);
    }

    private HashMap<String, Object> addData(
            String s1, String s2, String s3, String s4, String s5, String s6, String s7, boolean isNew
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
        docData.put("last_updated", new Timestamp(new Date()));
        if(isNew){
            docData.put("added_on", new Timestamp(new Date()));
            docData.put("added_by", FirebaseAuth.getInstance().getUid());
            docData.put("added_by_designation", CommonClass.modelUserData.getDesignation());
            docData.put("completed", false);
            docData.put("department", CommonClass.modelUserData.getDepartment());
        }else{
            delayReason.add(" • "+CommonClass.modelUserData.getDate()+ " : "+Objects.requireNonNull(activityAddNewPostBinding.delayReason.getText()));
            docData.put("delay_reason", FieldValue.arrayUnion(delayReason.get(delayReason.size()-1)));
        }

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