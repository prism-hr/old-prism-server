package uk.co.alumeni.prism.rest.representation.comment;

public class CommentPositionDetailRepresentation {

    private String positionName;

    private String positionDescription;

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public CommentPositionDetailRepresentation withPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public CommentPositionDetailRepresentation withPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
        return this;
    }

}
