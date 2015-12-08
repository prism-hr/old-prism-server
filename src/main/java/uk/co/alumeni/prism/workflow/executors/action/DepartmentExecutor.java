package uk.co.alumeni.prism.workflow.executors.action;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.services.*;

import javax.inject.Inject;

@Component
public class DepartmentExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Override
    public ActionOutcomeDTO execute(CommentDTO commentDTO) {
        User user = userService.getById(commentDTO.getUser());
        Department department = departmentService.getById(commentDTO.getResource().getId());

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        ResourceParentDTO resourceDTO = (ResourceParentDTO) commentDTO.getResource();
        Comment comment = commentService.prepareProcessResourceComment(department, user, action, commentDTO);
//        resourceService.updateParentResource(department, resourceDTO);

        return actionService.executeUserAction(department, action, comment);
    }

}
