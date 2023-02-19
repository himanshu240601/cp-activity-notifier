package com.example.bfgiactivitynotifier.signin.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.databinding.ActivitySignupBinding;
import com.example.bfgiactivitynotifier.faculty.FacultyActivity;
import com.example.bfgiactivitynotifier.signin.SignInActivity;
import com.example.bfgiactivitynotifier.signin.form_functions.FormFunctionality;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding activitySignupBinding;

    private final FormFunctionality formFunctionality = new FormFunctionality();

    //by default the password is hidden
    private boolean toggle_password = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        activitySignupBinding.signIn.setOnClickListener(view-> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });

        setUpAutoCompleteEditText();

        activitySignupBinding.editTextNamePrefix.setOnTouchListener((v, event) -> {
            activitySignupBinding.editTextNamePrefix.showDropDown();
            return false;
        });
        activitySignupBinding.editTextDepartment.setOnTouchListener((v, event) -> {
            activitySignupBinding.editTextDepartment.showDropDown();
            return false;
        });
        activitySignupBinding.editTextDesignation.setOnTouchListener((v, event) -> {
            activitySignupBinding.editTextDesignation.showDropDown();
            return false;
        });

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

            String mobile = activitySignupBinding.editTextMobile.getText().toString().trim();
            String name = activitySignupBinding.editTextName.getText().toString().trim();
            String name_pre = activitySignupBinding.editTextNamePrefix.getText().toString().trim();
            String department = activitySignupBinding.editTextDepartment.getText().toString().trim();
            String designation = activitySignupBinding.editTextDesignation.getText().toString().trim();
            String key = activitySignupBinding.editTextPassword.getText().toString().trim();

            if(formFunctionality.validateForm(this, mobile, name, department, designation, key)){
                activitySignupBinding.buttonSignIn.setEnabled(false);
                clearFocus();
                createUser(mobile+"@bfgi.com", name_pre+" "+name, department, designation, key);
            }
            activitySignupBinding.buttonSignIn.setEnabled(true);
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
                                                formFunctionality.addDataToSharedPres(this, name, department, designation, mobile);
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

    private final ArrayList<String> department = new ArrayList<>();
    private final ArrayList<String> designation = new ArrayList<>();
    private void setUpAutoCompleteEditText() {
        department.add("B.Tech CSE");
        department.add("B.Tech CE");
        department.add("B.Tech ME");
        department.add("B.Tech EE");
        ArrayAdapter<String> department_adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,department);
        activitySignupBinding.editTextDepartment.setAdapter(department_adapter);

        designation.add("Principal");
        designation.add("Dean");
        designation.add("HOD");
        designation.add("Faculty");
        ArrayAdapter<String> designation_adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,designation);
        activitySignupBinding.editTextDesignation.setAdapter(designation_adapter);

        ArrayList<String> name_prefix = new ArrayList<>();
        name_prefix.add("Er.");
        name_prefix.add("Dr.");
        name_prefix.add("Ms.");
        name_prefix.add("Mr.");
        ArrayAdapter<String> prefix_adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,name_prefix);
        activitySignupBinding.editTextNamePrefix.setAdapter(prefix_adapter);
    }

    //function to toggle the password visibility
    private void passwordToggle(boolean is_true, Drawable drawable, int inputType){
        toggle_password = is_true;
        activitySignupBinding.passwordToggle.setImageDrawable(drawable);
        activitySignupBinding.editTextPassword.setInputType(inputType);
    }
}