package com.example.bfgiactivitynotifier.faculty;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.databinding.ActivityAddNewPostBinding;
import com.example.bfgiactivitynotifier.faculty.models.ModelForm;
import com.example.bfgiactivitynotifier.firebasecloudmessaging.MyFirebaseNotificationSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddNewPostActivity extends AppCompatActivity {

    ActivityAddNewPostBinding activityAddNewPostBinding;

    ModelForm modelForm = new ModelForm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNewPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_post);

        //set the status bar background color
        Utility.changeStatusBarColor(getWindow(), R.color.primary);

        //close activity on back button click
        activityAddNewPostBinding.backButton.setOnClickListener(view -> finish());

        activityAddNewPostBinding.setModelObject(modelForm);

        //post the activity/event on publish button click
        activityAddNewPostBinding.publishButton.setOnClickListener(view -> publishEvent());

        //update the start date in form
        activityAddNewPostBinding.startDate.setOnClickListener(view -> {
            modelForm.getDateFromDialog("Select Start Date", getSupportFragmentManager(), modelForm);
            //remove focus from other edittext
            removeFocus();
        });

        //update the end date in form
        activityAddNewPostBinding.endDate.setOnClickListener(view -> {
            modelForm.getDateFromDialog("Select End Date", getSupportFragmentManager(),modelForm);
            //remove focus from other edidtext
            removeFocus();
        });

        //set the dropdown menu for work type
        //initialize a array adapter with the dropdown item layout file and string array defined in string.xml
        //then set the adapter of the auto complete text view
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, getResources().getStringArray(R.array.type_of_task));
        activityAddNewPostBinding.taskTypeDropdown.setAdapter(arrayAdapter);
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
    private void publishEvent(){
        //get all the data
        String string1 = Objects.requireNonNull(activityAddNewPostBinding.taskPlanAuthority.getEditText()).getText().toString();
        String string2 = Objects.requireNonNull(activityAddNewPostBinding.nameOfTask.getEditText()).getText().toString();
        String string3 = Objects.requireNonNull(activityAddNewPostBinding.typeOfTask.getEditText()).getText().toString();
        String string4 = Objects.requireNonNull(activityAddNewPostBinding.actionTaker.getEditText()).getText().toString();
        String string5 = Objects.requireNonNull(activityAddNewPostBinding.followUpTakenBy.getEditText()).getText().toString();
        String string6 = Objects.requireNonNull(activityAddNewPostBinding.startDate.getText()).toString();
        String string7 = Objects.requireNonNull(activityAddNewPostBinding.endDate.getText()).toString();

        //check if all data is entered correctly
        if(validate(string1, string2, string3, string4, string5, string6, string7)){
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            //add data to firebase fire store in the activities_data collection
            //store the dat into a map of type <string, string>
            Map<String, String> docData = addData(string1, string2, string3, string4, string5, string6, string7);
            DocumentReference documentReference = firebaseFirestore.collection("activities_data").document();
            documentReference.set(docData).
                    addOnCompleteListener(this, task -> {
                                        if(task.isSuccessful()){
                                            Toast.makeText(this, "Activity added successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            task.addOnFailureListener(this, e -> {
                                                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    });

            //add data to firebase fire store in the activity collection in faculty data
            Map<String, Object> docDataFaculty = new HashMap<>();
            docDataFaculty.put("id", documentReference.getPath());
            docDataFaculty.put("added_on", new Timestamp(new Date()));
            docDataFaculty.put("last_updated", new Timestamp(new Date()));

            firebaseFirestore.collection("faculty_data").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .collection("activities").document(documentReference.getId())
                    .set(docDataFaculty)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //send push notification to all the user about event
                            MyFirebaseNotificationSender myFirebaseNotificationSender = new MyFirebaseNotificationSender("Activity Notifier", string2,"events", getApplicationContext());
                            myFirebaseNotificationSender.sendNotification();
                        }
                    });
        }else{
            Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
        }
    }

    private HashMap<String, String> addData(
            String string1, String string2, String string3, String string4, String string5, String string6, String string7
    ){
        HashMap<String, String> docData = new HashMap<>();

        docData.put("plan_authority", string1);
        docData.put("activity_name", string2);
        docData.put("activity_type", string3);
        docData.put("action_taker", string4);
        docData.put("follow_up_taken_by", string5);
        docData.put("start_date", string6);
        docData.put("end_date", string7);
        docData.put("added_by", FirebaseAuth.getInstance().getUid());

        return docData;
    }

    //validate the input by the user
    private boolean validate(String string1, String string2, String string3, String string4, String string5, String string6, String string7) {
        //if any of the input fields is empty then
        //false is returned otherwise true
        return !string1.isEmpty() && !string2.isEmpty() && !string3.isEmpty() && !string4.isEmpty() && !string5.isEmpty() && !string6.isEmpty() && !string7.isEmpty();
    }
}