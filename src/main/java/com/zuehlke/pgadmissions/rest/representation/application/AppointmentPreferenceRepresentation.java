package com.zuehlke.pgadmissions.rest.representation.application;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class AppointmentPreferenceRepresentation {

    private UserRepresentation user;

    private List<Boolean> preferences;

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public List<Boolean> getPreferences() {
        return preferences;
    }

    public void setAnswers(List<Boolean> preferences) {
        this.preferences = preferences;
    }
    
    public AppointmentPreferenceRepresentation withUser(UserRepresentation user) {
        this.user = user;
        return this;
    }
    
    public AppointmentPreferenceRepresentation withPreferences(List<Boolean> preferences) {
        this.preferences = preferences;
        return this;
    }
    
}
