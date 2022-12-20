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
        upcoming = 0;
        in_progress = 0;
        completed = 0;
        not_completed = 0;
    }

    @Bindable
    public int getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(int upcoming) {
        this.upcoming = upcoming;
        notifyChange();
    }

    @Bindable
    public int getIn_progress() {
        return in_progress;
    }

    public void setIn_progress(int in_progress) {
        this.in_progress = in_progress;
    }

    @Bindable
    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    @Bindable
    public int getNot_completed() {
        return not_completed;
    }

    public void setNot_completed(int not_completed) {
        this.not_completed = not_completed;
    }
}
