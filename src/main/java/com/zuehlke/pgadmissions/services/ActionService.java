package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.exceptions.CannotExecuteActionException;

@Service
@Transactional
public class ActionService {

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private StateService stateService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    public void validateAction(PrismResource resource, User invoker, PrismAction action) {
        if (!checkActionAvailable(resource, invoker, action)) {
            throw new CannotExecuteActionException(resource);
        }
    }

    public boolean checkActionAvailable(PrismResource resource, User invoker, PrismAction action) {
        return roleService.getActionRoles(resource, action).size() == 0 || actionDAO.getPermittedAction(invoker, resource, action) != null;
    }

    public List<StateAction> getPermittedActions(User user, PrismResource resource) {
        return actionDAO.getPermittedActions(user, resource);
    }

    public ActionOutcome executeAction(Integer resourceId, User user, PrismAction action, Comment comment) {
        PrismResource resource = entityService.getById(action.getResourceClass(), resourceId);
        return executeAction(resource, user, action, comment);
    }

    public ActionOutcome executeAction(PrismResource resource, User user, PrismAction action, Comment comment) {
        PrismResource operativeResource = resource;
        if (!resource.getClass().equals(action.getResourceClass())) {
            operativeResource = resource.getParentResource(action.getResourceType());
        }
        return executeAction(operativeResource, resource, user, action, comment);
    }

    public ActionOutcome executeAction(PrismResource operativeResource, PrismResource resource, User invoker, PrismAction action, Comment comment) {
        validateAction(resource, invoker, action);

        if (operativeResource != resource) {
            PrismResource duplicateResource = entityService.getDuplicateEntity(resource);
            if (duplicateResource != null) {
                return new ActionOutcome(invoker, resource, actionDAO.getRedirectAction(duplicateResource, action, invoker));
            }
        }
        
        PrismAction nextAction = executeStateTransition(operativeResource, resource, invoker, action, comment);
        PrismResource nextActionResource = resource.getEnclosingResource(nextAction.getResourceType());
        
        return new ActionOutcome(invoker, nextActionResource, nextAction);
    }

    private PrismAction executeStateTransition(PrismResource operativeResource, PrismResource resource, User invoker, PrismAction action, Comment comment) {
        StateTransition stateTransition = stateService.getStateTransition(operativeResource, action, comment);
        resource.setState(stateTransition.getTransitionState());

        if (operativeResource != resource) {
            resource.setParentResource(operativeResource);
            entityService.save(resource);
            PrismResourceTransient codableResource = (PrismResourceTransient) resource;
            codableResource.setCode(codableResource.generateCode());
        }

        if (stateTransition.isDoPostComment()) {
            comment.setCreatedTimestamp(new DateTime());
            entityService.save(comment);
        }

        executeRoleTransitions(stateTransition, resource, invoker, comment);
        comment.setRole(Joiner.on("|").join(roleService.getActionInvokerRoles(invoker, resource, action)));
        return stateTransition.getTransitionAction().getId();
    }

    private void executeRoleTransitions(StateTransition stateTransition, PrismResource resource, User invoker, Comment comment) {
        HashMap<User, RoleTransition> userRoleTransitions = roleService.getUserRoleTransitions(stateTransition, resource, invoker, comment);
        for (User user : userRoleTransitions.keySet()) {
            roleService.executeRoleTransition(resource, user, userRoleTransitions.get(user));
        }
    }

}
