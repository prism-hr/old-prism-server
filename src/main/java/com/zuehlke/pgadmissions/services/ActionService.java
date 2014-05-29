package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO.UserRoleTransition;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
import com.zuehlke.pgadmissions.domain.Role;
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

    public void validateAction(final Application application, final User user, final PrismAction action) {
        if (!checkActionAvailable(application, user, action)) {
            throw new CannotExecuteActionException(application);
        }
    }

    public boolean checkActionAvailable(PrismResource resource, User user, PrismAction action) {
        return actionDAO.getPermittedAction(user, resource, action) != null;
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
        if (!userService.checkUserEnabled(invoker) || !checkActionEnabled(operativeResource, action)) {
            throw new CannotExecuteActionException(operativeResource);
        }

        if (operativeResource != resource) {
            PrismResource duplicateResource = entityService.getDuplicateEntity(resource);
            if (duplicateResource != null) {
                return new ActionOutcome(invoker, resource, actionDAO.getRedirectAction(duplicateResource, action, invoker));
            }
        }

        List<Role> actionInvokerRoles = getActionInvokerRoles(invoker, operativeResource, action);
        PrismAction nextAction = executeStateTransition(operativeResource, resource, invoker, actionInvokerRoles, action, comment);
        PrismResource nextActionResource = resource.getEnclosingResource(nextAction.getResourceType());
        return new ActionOutcome(invoker, nextActionResource, nextAction);
    }

    private PrismAction executeStateTransition(PrismResource operativeResource, PrismResource resource, User invoker, List<Role> actionInvokerRoles,
            PrismAction action, Comment comment) {
        StateTransition stateTransition = stateService.getStateTransition(operativeResource, action, comment);
        resource.setState(stateTransition.getTransitionState());

        if (operativeResource != resource) {
            resource.setParentResource(operativeResource);
            entityService.save(resource);

            if (resource instanceof PrismResourceTransient) {
                PrismResourceTransient codableResource = (PrismResourceTransient) resource;
                codableResource.setCode(codableResource.generateCode());
            }
        }

//        executeRoleTransitions(stateTransition, resource, invoker, comment);

        if (stateTransition.isDoPostComment()) {
            comment.setRole(Joiner.on("|").join(actionInvokerRoles));
            comment.setCreatedTimestamp(new DateTime());
            entityService.save(comment);
        }

        return stateTransition.getTransitionAction().getId();
    }

//    private void executeRoleTransitions(StateTransition stateTransition, PrismResource resource, User invoker, Comment comment) {
//        List<UserRoleTransition> userRoleTransitions = roleService.getUserRoleTransitions(stateTransition, resource, invoker, comment);
//        for (UserRoleTransition userRoleTransition : userRoleTransitions) {
//            roleService.executeRoleTransition(resource, userRoleTransition);
//        }
//    }

    private boolean checkActionEnabled(PrismResource resource, PrismAction action) {
        return !actionDAO.getValidAction(resource, action).isEmpty();
    }

    private List<Role> getActionInvokerRoles(User user, PrismResource operativeResource, PrismAction action) {
        List<Role> actionInvokerRoles = roleService.getActionInvokerRoles(user, operativeResource, action);
        if (!roleService.getActionRoles(operativeResource, action).isEmpty() && actionInvokerRoles.isEmpty()) {
            throw new CannotExecuteActionException(operativeResource);
        }
        return actionInvokerRoles;
    }

}
