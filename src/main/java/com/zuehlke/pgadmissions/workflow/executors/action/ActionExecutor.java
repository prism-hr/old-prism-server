package com.zuehlke.pgadmissions.workflow.executors.action;

import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;

public interface ActionExecutor {

    ActionOutcomeDTO execute(CommentDTO commentDTO);

}
