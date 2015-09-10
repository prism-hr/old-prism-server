package com.zuehlke.pgadmissions.workflow.executors.action;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

@Component
public class ProjectExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Override
    public ActionOutcomeDTO execute(CommentDTO commentDTO) {
        Integer resourceId = commentDTO.getResource().getId();
        User user = userService.getById(commentDTO.getUser());
        Project project = resourceService.getById(Project.class, resourceId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        ResourceOpportunityDTO projectDTO = (ResourceOpportunityDTO) commentDTO.getResource();
        Comment comment = commentService.prepareProcessResourceComment(project, user, action, commentDTO);
        resourceService.updateResource(PROJECT, resourceId, projectDTO);

        return actionService.executeUserAction(project, action, comment);
    }

}
