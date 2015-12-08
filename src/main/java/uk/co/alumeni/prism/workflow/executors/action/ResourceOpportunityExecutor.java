package uk.co.alumeni.prism.workflow.executors.action;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.UserService;

@Component
public class ResourceOpportunityExecutor implements ActionExecutor {

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
        PrismAction actionId = commentDTO.getAction();
        PrismScope resourceScope = actionId.getScope();
        Integer resourceId = commentDTO.getResource().getId();

        User user = userService.getById(commentDTO.getUser());
        ResourceOpportunity opportunity = (ResourceOpportunity) resourceService.getById(resourceScope, resourceId);

        ResourceCreationDTO projectDTO = commentDTO.getResource();
        if (projectDTO.getClass().equals(ResourceOpportunityDTO.class)) {
            resourceService.updateOpportunity(resourceScope, resourceId, (ResourceOpportunityDTO) projectDTO);
        }

        Action action = actionService.getById(actionId);
        Comment comment = commentService.prepareProcessResourceComment(opportunity, user, action, commentDTO);
        return actionService.executeUserAction(opportunity, action, comment);
    }

}
