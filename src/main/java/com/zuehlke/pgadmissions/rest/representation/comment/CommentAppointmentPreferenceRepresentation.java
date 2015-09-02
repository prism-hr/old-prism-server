package com.zuehlke.pgadmissions.rest.representation.comment;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class CommentAppointmentPreferenceRepresentation {

    private UserRepresentationSimple user;

    private List<Integer> preferences;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }
    
    public List<Integer> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Integer> preferences) {
        this.preferences = preferences;
    }

    public CommentAppointmentPreferenceRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

}
