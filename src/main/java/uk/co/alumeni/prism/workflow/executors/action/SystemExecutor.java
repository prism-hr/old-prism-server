package uk.co.alumeni.prism.workflow.executors.action;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.SystemService;

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
