package com.example.bfgiactivitynotifier.fragments.fragment_events.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class DocumentReferenceClass {
    private String id;
    private Timestamp added_on, last_updated;

    public DocumentReferenceClass() {
    }

    public DocumentReferenceClass(String id, Timestamp added_on, Timestamp last_updated) {
        this.id = id;
        this.added_on = added_on;
        this.last_updated = last_updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getAdded_on() {
        return added_on;
    }

    public void setAdded_on(Timestamp added_on) {
        this.added_on = added_on;
    }

    public Timestamp getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Timestamp last_updated) {
        this.last_updated = last_updated;
    }
}
