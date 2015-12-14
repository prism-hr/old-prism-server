package uk.co.alumeni.prism.rest.representation.action;

import java.util.List;

import uk.co.alumeni.prism.rest.representation.advert.AdvertThemeRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceLocationRepresentationRelation;

public class ActionOutcomeReplicableRepresentation {

    private List<CommentRepresentation> sequenceComments;

    private Integer sequenceResourceCount;

    private List<AdvertThemeRepresentation> sequenceFilterThemes;

    private List<ResourceLocationRepresentationRelation> sequenceFilterLocations;

    public List<CommentRepresentation> getSequenceComments() {
        return sequenceComments;
    }

    public void setSequenceComments(List<CommentRepresentation> sequenceComments) {
        this.sequenceComments = sequenceComments;
    }

    public Integer getSequenceResourceCount() {
        return sequenceResourceCount;
    }

    public void setSequenceResourceCount(Integer sequenceResourceCount) {
        this.sequenceResourceCount = sequenceResourceCount;
    }

    public List<AdvertThemeRepresentation> getSequenceFilterThemes() {
        return sequenceFilterThemes;
    }

    public void setSequenceFilterThemes(List<AdvertThemeRepresentation> sequenceFilterThemes) {
        this.sequenceFilterThemes = sequenceFilterThemes;
    }

    public List<ResourceLocationRepresentationRelation> getSequenceFilterLocations() {
        return sequenceFilterLocations;
    }

    public void setSequenceFilterLocations(List<ResourceLocationRepresentationRelation> sequenceFilterLocations) {
        this.sequenceFilterLocations = sequenceFilterLocations;
    }

    public ActionOutcomeReplicableRepresentation withSequenceComments(List<CommentRepresentation> sequenceComments) {
        this.sequenceComments = sequenceComments;
        return this;
    }

    public ActionOutcomeReplicableRepresentation withSequenceResourceCount(Integer sequenceResourceCount) {
        this.sequenceResourceCount = sequenceResourceCount;
        return this;
    }

    public ActionOutcomeReplicableRepresentation withSequenceFilterThemes(List<AdvertThemeRepresentation> sequenceFilterThemes) {
        this.sequenceFilterThemes = sequenceFilterThemes;
        return this;
    }

    public ActionOutcomeReplicableRepresentation withSequenceFilterLocations(List<ResourceLocationRepresentationRelation> sequenceFilterLocations) {
        this.sequenceFilterLocations = sequenceFilterLocations;
        return this;
    }
}
