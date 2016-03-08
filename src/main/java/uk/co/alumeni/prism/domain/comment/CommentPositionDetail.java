package uk.co.alumeni.prism.domain.comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommentPositionDetail {

    @Column(name = "application_position_name")
    private String positionName;

    @Column(name = "application_position_description")
    private String positionDescription;

    public final String getPositionName() {
        return positionName;
    }

    public final void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public final String getPositionDescription() {
        return positionDescription;
    }

    public final void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public CommentPositionDetail withPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public CommentPositionDetail withPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
        return this;
    }

}
