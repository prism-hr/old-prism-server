package com.zuehlke.pgadmissions.rest.representation.comment;

public class CommentCustomResponseRepresentation {

    private String propertyValue;

    private String label;

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public CommentCustomResponseRepresentation withLabel(String label) {
        this.label = label;
        return this;
    }
    
}
