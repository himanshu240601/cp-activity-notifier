package com.example.bfgiactivitynotifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.common.Utility;
import com.example.bfgiactivitynotifier.databinding.ActivitySignInBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.models.ModelUserData;
import com.example.bfgiactivitynotifier.students.StudentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    //declare binding class object
    ActivitySignInBinding activitySignInBinding;

    //by default prompt for student login
    private boolean is_student = true;

    //by default the password is hidden
    private boolean toggle_password = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //assign the binding class object
        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        //change status bar color
        Utility.changeStatusBarColor(getWindow(), R.color.primary);

        //close application if backButton is pressed,
        //remove all previous activities from stack using finishAffinity()
        activitySignInBinding.backButton.setOnClickListener(view -> finishAffinity());

        //change the states of button ('Student' and 'Faculty') when clicked on them
        //change the background defined in the drawable files
        activitySignInBinding.btnStudent.setOnClickListener(view ->{
            //if not isStudent, change student button background
            //to active and faculty to normal
            if(!is_student){
                //                          btnStudent                       btnFaculty
                setButtonStates(true, R.drawable.button_border_active, R.drawable.button_border_unactive, R.string.enter_your_uid);
                //change the text color
                activitySignInBinding.btnStudent.setTextColor(ContextCompat.getColor(this, R.color.white));
                activitySignInBinding.btnFaculty.setTextColor(ContextCompat.getColor(this, R.color.color_normal));
            }
        });
        activitySignInBinding.btnFaculty.setOnClickListener(view ->{
            //if isStudent is true, change to faculty background
            //to active and student to normal
            if(is_student){
                //                           btnStudent                       btnFaculty
                setButtonStates(false, R.drawable.button_border_unactive, R.drawable.button_border_active, R.string.enter_your_mobile);
                //change the text color
                activitySignInBinding.btnFaculty.setTextColor(ContextCompat.getColor(this, R.color.white));
                activitySignInBinding.btnStudent.setTextColor(ContextCompat.getColor(this, R.color.color_normal));
            }
        });

        //drawables for the password toggle component
        //eye - when password is not visible, show it on click
        //eye_off - when password is visible, hide it on click
        Drawable eye = ContextCompat.getDrawable(this, R.drawable.sign_in_eye);
        Drawable eye_off = ContextCompat.getDrawable(this, R.drawable.sign_in_eye_off);

        //on clicking the password toggle button
        //check if the password is hidden or visible
        activitySignInBinding.passwordToggle.setOnClickListener(view -> {
            if(!toggle_password){
                passwordToggle(true, eye_off, InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else{
                passwordToggle(false, eye, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            //set the cursor to the last position
            activitySignInBinding.password.setSelection(activitySignInBinding.password.getText().length());
        });

        //on clicking the sign in button
        activitySignInBinding.btnSignin.setOnClickListener(view -> {
            //get the values of text fields
            String id = activitySignInBinding.uid.getText().toString();
            String key = activitySignInBinding.password.getText().toString();

            //check if the values entered are correct
            //and not empty or contains some unwanted keywords
            //store the boolean value returned by the validate()
            //function into isValid variable
            boolean isValid = validate(id, key);

            //if isValid == true
            //check if user exist in the database
            if(isValid){
                //add suffix to the id
                //it's necessary for firebase email login
                id+="@bfgi.com";
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String userId = id;
                firebaseAuth.signInWithEmailAndPassword(id, key)
                        .addOnCompleteListener(this, task -> {
                            if(task.isSuccessful()){
                                //save data in shared preferences
                                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                sp.edit().putBoolean("logged", true).apply();
                                sp.edit().putBoolean("student", is_student).apply();

                                //set the user data model
                                //this will be used at various activities
                                //or fragments in the application
                                //the data will be fetched from the db server
                                //here the dummy data is sent
                                //after connecting to the database the data will be handled
                                //in the setModelUserData itself
                                //then we'll just need to call this function without any arguments

                                String path = is_student ? "student_data" : "faculty_data";

                                FirebaseFirestore.getInstance().collection(path).document(
                                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                                ).addSnapshotListener((value, error) -> {
                                    if(value!=null){
                                        String name = value.getString("name");
                                        String[] name_str = Objects.requireNonNull(name).split(" ");
                                        Utility.setModelUserData(new ModelUserData(name_str[1], name, userId, Utility.getCurrentDate()));
                                    }
                                });

                                //according to the type of user
                                //go to the next screen
                                if(is_student){
                                    startActivity(new Intent(SignInActivity.this, StudentActivity.class));
                                }else{
                                    startActivity(new Intent(SignInActivity.this, FacultyActivity.class));
                                }

                                finish();

                            }else{
                                task.addOnFailureListener(this, e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                            }
                        });
            }
        });
    }

    //function to toggle the password visibility
    private void passwordToggle(boolean is_true, Drawable drawable, int inputType){
        toggle_password = is_true;
        activitySignInBinding.passwordToggle.setImageDrawable(drawable);
        activitySignInBinding.password.setInputType(inputType);
    }

    //function to change the states (background to 'active' or 'normal') of signin choice buttons
    //according to whether the user is student or faculty
    private void setButtonStates(boolean is_true, int drawableBtn1, int drawableBtn2, int hint){
        is_student = is_true;
        activitySignInBinding.btnStudent.setBackground(ContextCompat.getDrawable(this, drawableBtn1));
        activitySignInBinding.btnFaculty.setBackground(ContextCompat.getDrawable(this, drawableBtn2));

        //change the hint text
        activitySignInBinding.uid.setHint(hint);

        //clear the edittext fields
        activitySignInBinding.uid.setText(null);
        activitySignInBinding.password.setText(null);
        //and set focus to the first edittext field
        activitySignInBinding.uid.setFocusable(true);
    }

    //function to validate user input
    private boolean validate(String id, String key){
        //check if any input by user is empty
        String type = is_student?"uid":"mobile number";
        if(id.isEmpty()){
            Toast.makeText(this, "Please enter a valid "+type+"!", Toast.LENGTH_SHORT).show();
            return false;
        }
        //check for the length of uid or phone number
        if(is_student){
            if(id.length()!=7){
                Toast.makeText(this, "Please enter a valid "+type+"!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            if(id.length()!=10){
                Toast.makeText(this, "Please enter a valid "+type+"!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(key.isEmpty()){
            Toast.makeText(this, "Please enter a valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}