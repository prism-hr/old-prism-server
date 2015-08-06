package com.zuehlke.pgadmissions.workflow.executors.action;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

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
import com.zuehlke.pgadmissions.services.ProjectService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ProjectExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Override
    public ActionOutcomeDTO execute(Integer resourceId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Project project = projectService.getById(resourceId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        ResourceOpportunityDTO projectDTO = (ResourceOpportunityDTO) commentDTO.getResource();
        Comment comment = commentService.prepareProcessResourceComment(project, user, action, projectDTO, commentDTO);
        resourceService.updateResource(PROJECT, resourceId, projectDTO);

        return actionService.executeUserAction(project, action, comment);
    }

}
