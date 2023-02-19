package com.example.bfgiactivitynotifier.signin.form_functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.models.UserModel;

import org.checkerframework.checker.units.qual.C;

public class FormFunctionality {
    public boolean validateForm(Context ctx, String mobile, String name, String department, String designation, String key) {
        if(mobile.length() < 10){
            return showToastMessage("Please enter valid mobile no.", ctx);
        }
        if(name.isEmpty()){
            return showToastMessage("Please enter a name!", ctx);
        }
        if(department.isEmpty()){
            return showToastMessage("Please select your department!", ctx);
        }
        if(designation.isEmpty()){
            return showToastMessage("Please select your designation!", ctx);
        }
        if(key.isEmpty()){
            return showToastMessage("Please enter a password!", ctx);
        }
        return true;
    }

    //function to validate user input
    public boolean validateForm(Context ctx, String id, String key){
        if(id.isEmpty()){
            return showToastMessage("Please enter a valid mobile number!", ctx);
        }
        if(id.length()!=10){
            return showToastMessage("Please enter a valid mobile number!", ctx);
        }
        if(key.isEmpty()){
            return showToastMessage("Please enter a valid password!", ctx);
        }
        return true;
    }

    private boolean showToastMessage(String message, Context ctx){
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void addDataToSharedPres(Context ctx, String name, String designation, String department, String mobile){
        SharedPreferences sp = ctx.getSharedPreferences("login", Context.MODE_PRIVATE);
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

        new CommonClass().setModelUserData(userModel);
    }
}
