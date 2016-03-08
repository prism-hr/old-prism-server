package uk.co.alumeni.prism.rest.representation.message;

import java.util.List;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class MessageRepresentation {

    private UserRepresentationSimple user;

    private List<MessageRecipientRepresentation> recipients;

    private String content;

    private List<DocumentRepresentation> documents;

    private DateTime createdTimestamp;

    private DateTime viewTimestamp;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public List<MessageRecipientRepresentation> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<MessageRecipientRepresentation> recipients) {
        this.recipients = recipients;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<DocumentRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentRepresentation> documents) {
        this.documents = documents;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getViewTimestamp() {
        return viewTimestamp;
    }

    public void setViewTimestamp(DateTime viewTimestamp) {
        this.viewTimestamp = viewTimestamp;
    }

    public MessageRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public MessageRepresentation withContent(String content) {
        this.content = content;
        return this;
    }

    public MessageRepresentation withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

}