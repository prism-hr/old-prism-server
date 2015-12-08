package uk.co.alumeni.prism.workflow.executors.action;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;

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
//        resourceService.updateOpportunity(PROJECT, resourceId, projectDTO);

        return actionService.executeUserAction(project, action, comment);
    }

}
