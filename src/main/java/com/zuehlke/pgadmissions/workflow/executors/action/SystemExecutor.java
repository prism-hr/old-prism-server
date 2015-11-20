package com.zuehlke.pgadmissions.workflow.executors.action;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class SystemExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private SystemService systemService;

    @Override
    public ActionOutcomeDTO execute(CommentDTO commentDTO) {
        return new ActionOutcomeDTO().withTransitionResource(systemService.getSystem()).withTransitionAction(actionService.getById(commentDTO.getAction()));
    }

}
