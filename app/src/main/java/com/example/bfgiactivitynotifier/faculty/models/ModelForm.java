package com.example.bfgiactivitynotifier.faculty.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;

public class ModelForm extends BaseObservable {
    String start_date, end_date;

    @Bindable
    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    @Bindable
    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    //function to show date picker dialog
    //args: title of the date picker, fragment manager need to be passed in show method of date picker, modelForm instance of model class used to bind data
    public void getDateFromDialog(String title, FragmentManager fragmentManager, ModelForm modelForm) {

        //initializing date picker dialog
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        //set the title of the material date picker
        materialDateBuilder.setTitleText(title);
        //build the material date picker
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
        //show the material date picker on screen;
        materialDatePicker.show(fragmentManager, "MATERIAL_DATE_PICKER");

        //when positive button (OK button) is clicked
        //get the selected date from the date picker
        //and set the date for that instance
        //then notify the change of data so the data will be updated in the view
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    String date = materialDatePicker.getHeaderText();
                    if (title.equals("Select Start Date")) {
                        modelForm.setStart_date(date);
                    } else {
                        modelForm.setEnd_date(date);
                    }
                    notifyChange();
                }
        );
    }
}
