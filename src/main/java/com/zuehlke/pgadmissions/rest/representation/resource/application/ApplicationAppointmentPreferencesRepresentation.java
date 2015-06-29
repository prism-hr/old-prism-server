package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ApplicationAppointmentPreferencesRepresentation {

    private List<Integer> preferences;

    private UserRepresentation user;

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public List<Integer> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Integer> preferences) {
        this.preferences = preferences;
    }

    public ApplicationAppointmentPreferencesRepresentation withUser(UserRepresentation user) {
        this.user = user;
        return this;
    }

}
