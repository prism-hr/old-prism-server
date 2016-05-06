package uk.co.alumeni.prism.rest.representation.message;

import static com.google.common.base.Objects.equal;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import com.google.common.base.Objects;

public class MessageThreadParticipantRepresentation {

    private UserRepresentationSimple user;

    private MessageRepresentation lastViewedMessage;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public MessageRepresentation getLastViewedMessage() {
        return lastViewedMessage;
    }

    public void setLastViewedMessage(MessageRepresentation lastViewedMessage) {
        this.lastViewedMessage = lastViewedMessage;
    }

    public MessageThreadParticipantRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public MessageThreadParticipantRepresentation withLastViewedMessage(MessageRepresentation lastViewedMessage) {
        this.lastViewedMessage = lastViewedMessage;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        MessageThreadParticipantRepresentation other = (MessageThreadParticipantRepresentation) object;
        return equal(user, other.getUser());
    }

}
