package uk.co.alumeni.prism.rest.representation.message;

import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class MessageThreadSubjectRepresentation {

    private ResourceRepresentationSimple resource;
    
    private UserRepresentationSimple user;
    
    private String subject;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public MessageThreadSubjectRepresentation withSubject(String subject) {
        this.subject = subject;
        return this;
    }
    
}
