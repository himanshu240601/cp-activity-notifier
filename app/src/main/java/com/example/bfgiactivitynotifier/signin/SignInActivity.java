package com.example.bfgiactivitynotifier.signin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.ActivitySignInBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    //declare binding class object
    ActivitySignInBinding activitySignInBinding;

    //by default the password is hidden
    private boolean toggle_password = false;

    private final CommonClass commonClass = new CommonClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //assign the binding class object
        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

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
            activitySignInBinding.editTextPassword.setSelection(activitySignInBinding.editTextPassword.getText().length());
        });

        //on clicking the sign in button
        activitySignInBinding.buttonSignIn.setOnClickListener(view -> {
            activitySignInBinding.buttonSignIn.setEnabled(false);
            activitySignInBinding.editTextMobile.clearFocus();
            activitySignInBinding.editTextPassword.clearFocus();
            //get the values of text fields
            String id = activitySignInBinding.editTextMobile.getText().toString();
            String key = activitySignInBinding.editTextPassword.getText().toString();

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
                String user_id = id;
                firebaseAuth.signInWithEmailAndPassword(id, key)
                        .addOnCompleteListener(this, task -> {
                            if(task.isSuccessful()){
                                //save data in shared preferences
                                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                sp.edit().putBoolean("logged", true).apply();

                                //set the user data model
                                //this will be used at various activities
                                //or fragments in the application
                                //the data will be fetched from the db server
                                FirebaseFirestore.getInstance().collection("faculty_data").document(
                                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                                ).addSnapshotListener((value, error) -> {
                                    if(value!=null){
                                        String name = value.getString("name");
                                        String[] name_str = Objects.requireNonNull(name).split(" ");
                                        String designation = value.getString("designation");
                                        String department = value.getString("department");

                                        sp.edit().putString("user_id", user_id).apply();
                                        sp.edit().putString("full_name", name).apply();
                                        sp.edit().putString("first_name", name_str[1]).apply();
                                        sp.edit().putString("designation", designation).apply();
                                        sp.edit().putString("department", department).apply();

                                        UserModel userModel = new UserModel(
                                                user_id,
                                                name,
                                                name_str[1],
                                                designation,
                                                department
                                        );
                                        commonClass.setModelUserData(userModel);
                                        //according to the type of user
                                        //go to the next screen
                                        startActivity(new Intent(SignInActivity.this, FacultyActivity.class));
                                        finish();
                                    }
                                });

                            }
                            activitySignInBinding.buttonSignIn.setEnabled(true);
                        })
                        .addOnFailureListener(this, e -> {
                            activitySignInBinding.buttonSignIn.setEnabled(true);
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }else{
                activitySignInBinding.buttonSignIn.setEnabled(true);
            }
        });
    }

    //function to toggle the password visibility
    private void passwordToggle(boolean is_true, Drawable drawable, int inputType){
        toggle_password = is_true;
        activitySignInBinding.passwordToggle.setImageDrawable(drawable);
        activitySignInBinding.editTextPassword.setInputType(inputType);
    }


    //function to validate user input
    private boolean validate(String id, String key){
        if(id.isEmpty()){
            Toast.makeText(this, "Please enter a valid mobile number!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(id.length()!=10){
            Toast.makeText(this, "Please enter a valid mobile number!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(key.isEmpty()){
            Toast.makeText(this, "Please enter a valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}