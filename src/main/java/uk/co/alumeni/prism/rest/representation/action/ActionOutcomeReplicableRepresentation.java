package uk.co.alumeni.prism.rest.representation.action;

import java.util.List;

import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;

public class ActionOutcomeReplicableRepresentation {

    private ResourceRepresentationSimple parentResource;

    private List<CommentRepresentation> sequenceComments;

    public ResourceRepresentationSimple getParentResource() {
        return parentResource;
    }

    public void setParentResource(ResourceRepresentationSimple parentResource) {
        this.parentResource = parentResource;
    }

    public List<CommentRepresentation> getSequenceComments() {
        return sequenceComments;
    }

    public void setSequenceComments(List<CommentRepresentation> sequenceComments) {
        this.sequenceComments = sequenceComments;
    }

    public ActionOutcomeReplicableRepresentation withParentResource(ResourceRepresentationSimple parentResource) {
        this.parentResource = parentResource;
        return this;
    }

    public ActionOutcomeReplicableRepresentation withSequenceComments(List<CommentRepresentation> sequenceComments) {
        this.sequenceComments = sequenceComments;
        return this;
    }

}
