package com.zuehlke.pgadmissions.workflow.executors.action;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.services.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;

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
        resourceService.updateResource(PROGRAM, resourceId, programDTO);

        return actionService.executeUserAction(program, action, comment);
    }

}
