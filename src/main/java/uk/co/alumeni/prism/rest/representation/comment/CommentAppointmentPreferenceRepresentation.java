package uk.co.alumeni.prism.rest.representation.comment;

import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import java.util.List;

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
