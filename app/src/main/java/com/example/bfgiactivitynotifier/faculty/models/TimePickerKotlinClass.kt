package com.example.bfgiactivitynotifier.faculty.models

import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker

class TimePickerKotlinClass {

    fun getTimeFromDialog(title: String, fragmentManager: FragmentManager?, modelForm: ModelForm) {

        //initializing time picker dialog
        val materialTimeBuilder = MaterialTimePicker.Builder()
        //set the title of the material time picker
        materialTimeBuilder.setTitleText(title)
        //build the material time picker
        val materialTimePicker = materialTimeBuilder.build()
        //show the material time picker on screen;
        materialTimePicker.show(fragmentManager!!, "MATERIAL_DATE_PICKER")

        //when positive button (OK button) is clicked
        //get the selected time from the time picker
        //and set the time for that instance
        //then notify the change of data so the data will be updated in the view
        materialTimePicker.addOnPositiveButtonClickListener {
            val hour = materialTimePicker.hour
            val min = materialTimePicker.minute
            val time: String = when {
                hour>12->{
                    if(min<10){
                        "${materialTimePicker.hour-12}:0${materialTimePicker.minute} pm"
                    }else{
                        "${materialTimePicker.hour-12}:${materialTimePicker.minute} pm"
                    }
                }
                hour==12->{
                    if(min<10){
                        "${materialTimePicker.hour}:0${materialTimePicker.minute} pm"
                    }else{
                        "${materialTimePicker.hour}:${materialTimePicker.minute} pm"
                    }
                }
                hour==0->{
                    if(min<10){
                        "${materialTimePicker.hour+12}:0${materialTimePicker.minute} am"
                    }else{
                        "${materialTimePicker.hour+12}:${materialTimePicker.minute} am"
                    }
                }else->{
                    if(min<10){
                        "${materialTimePicker.hour}:0${materialTimePicker.minute} am"
                    }else{
                        "${materialTimePicker.hour}:${materialTimePicker.minute} am"
                    }
                }
            }

            modelForm.setEvent_time(time)
            modelForm.notifyChange()
        }
    }
}