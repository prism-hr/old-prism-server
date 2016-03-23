package uk.co.alumeni.prism.rest.representation.message;

import java.util.List;

import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class MessageThreadRepresentation {

    private Integer id;

    private String subject;

    private ResourceRepresentationSimple resource;

    private UserRepresentationSimple user;

    private List<MessageRepresentation> messages;

    private List<MessageThreadParticipantRepresentation> participants;

    private MessageRepresentation lastViewedMessage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ResourceRepresentationSimple getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
    }

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public List<MessageRepresentation> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageRepresentation> messages) {
        this.messages = messages;
    }

    public List<MessageThreadParticipantRepresentation> getParticipants() {
        return participants;
    }

    public void setParticipants(List<MessageThreadParticipantRepresentation> participants) {
        this.participants = participants;
    }

    public MessageRepresentation getLastViewedMessage() {
        return lastViewedMessage;
    }

    public void setLastViewedMessage(MessageRepresentation lastViewedMessage) {
        this.lastViewedMessage = lastViewedMessage;
    }

    public MessageThreadRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public MessageThreadRepresentation withSubject(String subject) {
        this.subject = subject;
        return this;
    }

}
