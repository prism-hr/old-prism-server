package uk.co.alumeni.prism.rest.representation.message;

import java.util.List;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class MessageRepresentation {

    private Integer id;
    
    private UserRepresentationSimple user;

    private String content;

    private List<DocumentRepresentation> documents;

    private DateTime createdTimestamp;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
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

    public MessageRepresentation withId(Integer id) {
        this.id = id;
        return this;
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
