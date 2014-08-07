package com.zuehlke.pgadmissions.rest.representation.application;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class UserAppointmentPreferencesRepresentation {

    private List<Boolean> preferences;

    private UserRepresentation user;

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

    public UserAppointmentPreferencesRepresentation withUser(UserRepresentation user) {
        this.user = user;
        return this;
    }

    public UserAppointmentPreferencesRepresentation withPreferences(List<Boolean> preferences) {
        this.preferences = preferences;
        return this;
    }

}
