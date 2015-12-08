package uk.co.alumeni.prism.workflow.executors.action;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.services.*;

import javax.inject.Inject;

@Component
public class ProgramExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private ProgramService programService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Override
    public ActionOutcomeDTO execute(CommentDTO commentDTO) {
        Integer resourceId = commentDTO.getResource().getId();
        User user = userService.getById(commentDTO.getUser());
        Program program = programService.getById(resourceId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        ResourceOpportunityDTO programDTO = (ResourceOpportunityDTO) commentDTO.getResource();
        Comment comment = commentService.prepareProcessResourceComment(program, user, action, commentDTO);
//        resourceService.updateOpportunity(PROGRAM, resourceId, programDTO);

        return actionService.executeUserAction(program, action, comment);
    }

}
