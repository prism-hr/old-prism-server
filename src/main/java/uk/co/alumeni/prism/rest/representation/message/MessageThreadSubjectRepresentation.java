package uk.co.alumeni.prism.rest.representation.message;

import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;

public class MessageThreadSubjectRepresentation {

    private ResourceRepresentationSimple resource;
    
    private String subject;

    public ResourceRepresentationSimple getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
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
