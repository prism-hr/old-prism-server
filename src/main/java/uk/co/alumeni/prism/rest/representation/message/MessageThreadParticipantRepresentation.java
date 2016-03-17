package uk.co.alumeni.prism.rest.representation.message;

import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

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

}
