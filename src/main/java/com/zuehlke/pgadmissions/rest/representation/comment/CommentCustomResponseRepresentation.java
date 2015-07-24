package com.zuehlke.pgadmissions.rest.representation.comment;

public class CommentCustomResponseRepresentation {

    private String label;
    
    private String propertyValue;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
    
    public CommentCustomResponseRepresentation withLabel(String label) {
        this.label = label;
        return this;
    }
    
    public CommentCustomResponseRepresentation withPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }
    
}
