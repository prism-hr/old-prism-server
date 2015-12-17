package uk.co.alumeni.prism.rest.representation.action;

import java.util.List;

import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;

public class ActionOutcomeReplicableRepresentation {

    private ResourceListFilterDTO filter;

    private List<CommentRepresentation> sequenceComments;
    
    public ResourceListFilterDTO getFilter() {
        return filter;
    }

    public void setFilter(ResourceListFilterDTO filter) {
        this.filter = filter;
    }

    public List<CommentRepresentation> getSequenceComments() {
        return sequenceComments;
    }

    public void setSequenceComments(List<CommentRepresentation> sequenceComments) {
        this.sequenceComments = sequenceComments;
    }

    public ActionOutcomeReplicableRepresentation withFilter(ResourceListFilterDTO filter) {
        this.filter = filter;
        return this;
    }

    public ActionOutcomeReplicableRepresentation withSequenceComments(List<CommentRepresentation> sequenceComments) {
        this.sequenceComments = sequenceComments;
        return this;
    }

}
