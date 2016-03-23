package uk.co.alumeni.prism.rest.representation;

import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationMessage;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationUser;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ProfileRepresentationCandidate extends ProfileRepresentationMessage {

    private UserRepresentationSimple user;

    private ProfileRepresentationUser profile;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public ProfileRepresentationUser getProfile() {
        return profile;
    }

    public void setProfile(ProfileRepresentationUser profile) {
        this.profile = profile;
    }

    public ProfileRepresentationCandidate withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ProfileRepresentationCandidate withProfile(ProfileRepresentationUser profile) {
        this.profile = profile;
        return this;
    }

    public ProfileRepresentationCandidate withReadMessageCount(Integer readMessageCount) {
        setReadMessageCount(readMessageCount);
        return this;
    }

    public ProfileRepresentationCandidate withUnreadMessageCount(Integer unreadMessageCount) {
        setUnreadMessageCount(unreadMessageCount);
        return this;
    }

}
