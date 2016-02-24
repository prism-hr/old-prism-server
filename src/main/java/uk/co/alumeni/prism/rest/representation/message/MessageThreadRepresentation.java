package uk.co.alumeni.prism.rest.representation.message;

import java.util.List;

public class MessageThreadRepresentation {

    private Integer id;

    private String subject;

    private List<MessageRepresentation> messages;

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

    public List<MessageRepresentation> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageRepresentation> messages) {
        this.messages = messages;
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
