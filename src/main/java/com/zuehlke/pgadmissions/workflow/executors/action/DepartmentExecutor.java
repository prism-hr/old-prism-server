package com.zuehlke.pgadmissions.workflow.executors.action;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

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
        resourceService.updateResource(department, resourceDTO);

        return actionService.executeUserAction(department, action, comment);
    }

}
