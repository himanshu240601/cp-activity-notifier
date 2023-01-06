package com.example.bfgiactivitynotifier.signin.register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.ActivitySignupBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.models.UserModel;
import com.example.bfgiactivitynotifier.signin.SignInActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding activitySignupBinding;

    //by default the password is hidden
    private boolean toggle_password = false;

    private final CommonClass commonClass = new CommonClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        activitySignupBinding.signIn.setOnClickListener(view-> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });

        setUpAutoCompleteEditText();

        //drawables for the password toggle component
        //eye - when password is not visible, show it on click
        //eye_off - when password is visible, hide it on click
        Drawable eye = ContextCompat.getDrawable(this, R.drawable.sign_in_eye);
        Drawable eye_off = ContextCompat.getDrawable(this, R.drawable.sign_in_eye_off);

        //on clicking the password toggle button
        //check if the password is hidden or visible
        activitySignupBinding.passwordToggle.setOnClickListener(view -> {
            if(!toggle_password){
                passwordToggle(true, eye_off, InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else{
                passwordToggle(false, eye, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            //set the cursor to the last position
            activitySignupBinding.editTextPassword.setSelection(activitySignupBinding.editTextPassword.getText().length());
        });

        activitySignupBinding.buttonSignIn.setOnClickListener(v -> {
            String mobile = activitySignupBinding.editTextMobile.getText().toString();
            String name = activitySignupBinding.editTextName.getText().toString();
            String department = activitySignupBinding.editTextDepartment.getText().toString();
            String designation = activitySignupBinding.editTextDesignation.getText().toString();
            String key = activitySignupBinding.editTextPassword.getText().toString();
            if(validateForm(mobile, name, department, designation, key)){
                activitySignupBinding.buttonSignIn.setEnabled(false);
                clearFocus();
                createUser(mobile+"@bfgi.com", "Er. "+name, department, designation, key);
            }
        });
    }

    private void clearFocus() {
        activitySignupBinding.editTextPassword.clearFocus();
        activitySignupBinding.editTextName.clearFocus();
        activitySignupBinding.editTextMobile.clearFocus();
        activitySignupBinding.editTextDesignation.clearFocus();
        activitySignupBinding.editTextDepartment.clearFocus();
    }

    private void createUser(String mobile, String name, String department, String designation, String key) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mobile, key)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("name", name);
                        docData.put("department", department);
                        docData.put("designation", designation);
                        DocumentReference documentReference = firebaseFirestore.collection("faculty_data").document(
                                Objects.requireNonNull(task.getResult().getUser()).getUid()
                        );
                        documentReference.set(docData)
                                        .addOnCompleteListener(SignupActivity.this, task1 -> {
                                            if(task1.isSuccessful()){
                                                //save data in shared preferences
                                                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                                sp.edit().putBoolean("logged", true).apply();

                                                String[] name_str = name.split(" ");

                                                sp.edit().putString("user_id", mobile).apply();
                                                sp.edit().putString("full_name", name).apply();
                                                sp.edit().putString("first_name", name_str[1]).apply();
                                                sp.edit().putString("designation", designation).apply();
                                                sp.edit().putString("department", department).apply();

                                                UserModel userModel = new UserModel(
                                                        mobile,
                                                        name,
                                                        name_str[1],
                                                        designation,
                                                        department
                                                );
                                                commonClass.setModelUserData(userModel);
                                                //according to the type of user
                                                //go to the next screen
                                                activitySignupBinding.buttonSignIn.setEnabled(true);
                                                startActivity(new Intent(SignupActivity.this, FacultyActivity.class));
                                                finishAffinity();
                                            }else{
                                                Toast.makeText(this, "Try again later!", Toast.LENGTH_SHORT).show();
                                            }
                                            activitySignupBinding.buttonSignIn.setEnabled(true);
                                        }).addOnFailureListener(e -> Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

                    }else{
                        activitySignupBinding.buttonSignIn.setEnabled(true);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    activitySignupBinding.buttonSignIn.setEnabled(true);
                });
    }

    private boolean validateForm(String mobile, String name, String department, String designation, String key) {
        if(mobile.length() < 10){
            Toast.makeText(this, "Please enter valid mobile no.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name.isEmpty()){
            Toast.makeText(this, "Please enter a name!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(department.isEmpty()){
            Toast.makeText(this, "Please select your department!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(designation.isEmpty()){
            Toast.makeText(this, "Please select your designation!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(key.isEmpty()){
            Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private final ArrayList<String> department = new ArrayList<>();
    private final ArrayList<String> designation = new ArrayList<>();
    private void setUpAutoCompleteEditText() {
        department.add("B.Tech CSE");
        department.add("B.Tech CE");
        department.add("B.Tech CIVIL");
        department.add("B.Tech EE");
        ArrayAdapter<String> department_adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,department);
        activitySignupBinding.editTextDepartment.setAdapter(department_adapter);

        designation.add("Principal");
        designation.add("HOD");
        designation.add("Professor");
        designation.add("Assistant Professor");
        ArrayAdapter<String> designation_adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,designation);
        activitySignupBinding.editTextDesignation.setAdapter(designation_adapter);
    }

    //function to toggle the password visibility
    private void passwordToggle(boolean is_true, Drawable drawable, int inputType){
        toggle_password = is_true;
        activitySignupBinding.passwordToggle.setImageDrawable(drawable);
        activitySignupBinding.editTextPassword.setInputType(inputType);
    }
}