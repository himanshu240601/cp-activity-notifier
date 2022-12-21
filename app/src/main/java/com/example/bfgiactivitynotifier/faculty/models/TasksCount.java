package com.example.bfgiactivitynotifier.faculty.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class TasksCount extends BaseObservable {
    private int upcoming;
    private int in_progress;
    private int completed;
    private int not_completed;

    public TasksCount() {
        this.upcoming = 0;
        this.in_progress = 0;
        this.completed = 0;
        this.not_completed = 0;
    }

    @Bindable
    public String getUpcoming() {
        return String.valueOf(upcoming);
    }

    public void setUpcoming(int upcoming) {
        this.upcoming = upcoming;
        notifyPropertyChanged(BR.upcoming);
    }

    @Bindable
    public String getIn_progress() {
        return String.valueOf(in_progress);
    }

    public void setIn_progress(int in_progress) {
        this.in_progress = in_progress;
        notifyPropertyChanged(BR.in_progress);
    }

    @Bindable
    public String getCompleted() {
        return String.valueOf(completed);
    }

    public void setCompleted(int completed) {
        this.completed = completed;
        notifyPropertyChanged(BR.completed);
    }

    @Bindable
    public String  getNot_completed() {
        return String.valueOf(not_completed);
    }

    public void setNot_completed(int not_completed) {
        this.not_completed = not_completed;
        notifyPropertyChanged(BR.not_completed);
    }
}
