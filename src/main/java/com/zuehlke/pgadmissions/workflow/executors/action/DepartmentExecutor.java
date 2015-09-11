package com.zuehlke.pgadmissions.workflow.executors.action;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.services.*;
import org.springframework.stereotype.Component;

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

        ResourceParentDivisionDTO resourceDTO = (ResourceParentDivisionDTO) commentDTO.getResource();
        Comment comment = commentService.prepareProcessResourceComment(department, user, action, commentDTO);
        resourceService.updateResource(department, resourceDTO);

        return actionService.executeUserAction(department, action, comment);
    }

}
