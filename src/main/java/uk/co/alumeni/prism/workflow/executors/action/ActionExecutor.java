package uk.co.alumeni.prism.workflow.executors.action;

import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;

public interface ActionExecutor {

    ActionOutcomeDTO execute(CommentDTO commentDTO);

}
