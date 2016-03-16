package uk.co.alumeni.prism.rest.representation;

import uk.co.alumeni.prism.rest.representation.user.UserProfileRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ProfileRepresentationCandidate {

    private UserRepresentationSimple user;

    private UserProfileRepresentation profile;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public UserProfileRepresentation getProfile() {
        return profile;
    }

    public void setProfile(UserProfileRepresentation profile) {
        this.profile = profile;
    }

    public ProfileRepresentationCandidate withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ProfileRepresentationCandidate withProfile(UserProfileRepresentation profile) {
        this.profile = profile;
        return this;
    }
}

