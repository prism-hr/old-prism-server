package uk.co.alumeni.prism.workflow.selectors.user;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.rest.dto.StateActionPendingDTO;

public interface PrismReplicableActionUserAssignmentSelector {

    public void setUserAssignments(Comment comment, StateActionPendingDTO stateActionPendingDTO);

}
