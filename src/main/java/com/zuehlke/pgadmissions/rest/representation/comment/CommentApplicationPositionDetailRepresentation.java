package com.zuehlke.pgadmissions.rest.representation.comment;

public class CommentApplicationPositionDetailRepresentation {

    private String positionTitle;

    private String positionDescription;

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }
}
