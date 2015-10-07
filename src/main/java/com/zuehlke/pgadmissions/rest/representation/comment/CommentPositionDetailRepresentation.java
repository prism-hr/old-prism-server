package com.zuehlke.pgadmissions.rest.representation.comment;

public class CommentPositionDetailRepresentation {

    private String positionTitle;

    private String positionDescription;

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }
    
    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }
    
    public CommentPositionDetailRepresentation withPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
        return this;
    }
    
    public CommentPositionDetailRepresentation withPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
        return this;
    }
    
}
