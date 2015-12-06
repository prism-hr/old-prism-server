package uk.co.alumeni.prism.rest.representation.action;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.rest.representation.advert.AdvertThemeRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;

public class ActionOutcomeRepresentation {

    private ResourceRepresentationSimple resource;

    private ResourceRepresentationSimple transitionResource;

    private PrismAction transitionAction;

    private List<CommentRepresentation> replicableSequenceComments;

    private Integer replicableSequenceResourceCount;

    private List<AdvertThemeRepresentation> replicableSequenceFilterThemes;

    private List<ResourceRepresentationRelation> replicableSequenceFilterLocations;

    public ResourceRepresentationSimple getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
    }

    public ResourceRepresentationSimple getTransitionResource() {
        return transitionResource;
    }

    public void setTransitionResource(ResourceRepresentationSimple transitionResource) {
        this.transitionResource = transitionResource;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
    }

    public List<CommentRepresentation> getReplicableSequenceComments() {
        return replicableSequenceComments;
    }

    public void setReplicableSequenceComments(List<CommentRepresentation> replicableSequenceComments) {
        this.replicableSequenceComments = replicableSequenceComments;
    }

    public Integer getReplicableSequenceResourceCount() {
        return replicableSequenceResourceCount;
    }

    public void setReplicableSequenceResourceCount(Integer replicableSequenceResourceCount) {
        this.replicableSequenceResourceCount = replicableSequenceResourceCount;
    }

    public List<AdvertThemeRepresentation> getReplicableSequenceFilterThemes() {
        return replicableSequenceFilterThemes;
    }

    public void setReplicableSequenceFilterThemes(List<AdvertThemeRepresentation> replicableSequenceFilterThemes) {
        this.replicableSequenceFilterThemes = replicableSequenceFilterThemes;
    }

    public List<ResourceRepresentationRelation> getReplicableSequenceFilterLocations() {
        return replicableSequenceFilterLocations;
    }

    public void setReplicableSequenceFilterLocations(List<ResourceRepresentationRelation> replicableSequenceFilterLocations) {
        this.replicableSequenceFilterLocations = replicableSequenceFilterLocations;
    }

    public ActionOutcomeRepresentation withResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
        return this;
    }

    public ActionOutcomeRepresentation withTransitionResource(ResourceRepresentationSimple transitionResource) {
        this.transitionResource = transitionResource;
        return this;
    }

    public ActionOutcomeRepresentation withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public ActionOutcomeRepresentation withReplicableSequenceComments(List<CommentRepresentation> replicableSequenceComments) {
        this.replicableSequenceComments = replicableSequenceComments;
        return this;
    }

    public ActionOutcomeRepresentation withReplicableSequenceResourceCount(Integer replicableSequenceResourceCount) {
        this.replicableSequenceResourceCount = replicableSequenceResourceCount;
        return this;
    }

}
