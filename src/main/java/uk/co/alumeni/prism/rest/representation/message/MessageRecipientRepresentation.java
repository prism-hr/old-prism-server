package uk.co.alumeni.prism.rest.representation.message;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class MessageRecipientRepresentation {

    private UserRepresentationSimple user;

    private DateTime viewTimestamp;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public DateTime getViewTimestamp() {
        return viewTimestamp;
    }

    public void setViewTimestamp(DateTime viewTimestamp) {
        this.viewTimestamp = viewTimestamp;
    }

    public MessageRecipientRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public MessageRecipientRepresentation withViewTimestamp(DateTime viewTimestamp) {
        this.viewTimestamp = viewTimestamp;
        return this;
    }

}
