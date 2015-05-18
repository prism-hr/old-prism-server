package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommentApplicationPositionDetail {

    @Column(name = "application_position_title")
    private String positionTitle;

    @Column(name = "application_position_description")
    private String positionDescription;

    public final String getPositionTitle() {
        return positionTitle;
    }

    public final void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public final String getPositionDescription() {
        return positionDescription;
    }

    public final void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public CommentApplicationPositionDetail withPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
        return this;
    }

    public CommentApplicationPositionDetail withPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
        return this;
    }

}
