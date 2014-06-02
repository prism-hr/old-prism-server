package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
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

    public Action getById(PrismAction id) {
        return entityService.getByProperty(Action.class, "id", id);
    }

    public void validateGetAction(PrismResource resource, PrismAction action, User invoker) {
        if (!checkActionAvailable(resource, action, invoker)) {
            throw new CannotExecuteActionException(resource, action);
        }
    }

    public void validatePostAction(PrismResource resource, PrismAction action, Comment comment) {
        User invoker = comment.getUser();
        User delegateInvoker = comment.getDelegateUser();
        if (!checkActionAvailable(resource, action, invoker)) {
            if (!checkActionAvailable(resource, action, delegateInvoker)) {
                if (!checkDelegateActionAvailable(resource, action, delegateInvoker)) {
                    throw new CannotExecuteActionException(resource, action);
                }
            }
        }
    }

    public boolean checkActionAvailable(PrismResource resource, PrismAction action, User invoker) {
        return roleService.getActionRoles(resource, action).size() == 0 || actionDAO.getPermittedAction(resource, action, invoker) != null;
    }
    
    public boolean checkDelegateActionAvailable(PrismResource resource, PrismAction action, User invoker) {
        PrismAction delegateAction = actionDAO.getDelegateAction(resource, action);
        return checkActionAvailable(resource, delegateAction, invoker);
    }

    public List<StateAction> getPermittedActions(User user, PrismResource resource) {
        return actionDAO.getPermittedActions(resource, user);
    }

    public ActionOutcome executeAction(Integer resourceId, User user, PrismAction action, Comment comment) {
        PrismResourceDynamic resource = (PrismResourceDynamic) entityService.getById(action.getResourceClass(), resourceId);
        return executeAction(resource, user, action, comment);
    }

    public ActionOutcome executeAction(PrismResourceDynamic resource, User user, PrismAction action, Comment comment) {
        PrismResource operativeResource = resource;
        if (!resource.getClass().equals(action.getResourceClass())) {
            operativeResource = resource.getParentResource(action.getResourceType());
        }
        return executeAction(operativeResource, resource, action, comment);
    }

    public ActionOutcome executeAction(PrismResource operativeResource, PrismResourceDynamic resource, PrismAction action, Comment comment) {
        validatePostAction(resource, action, comment);

        User actionOwner = comment.getUser();
        
        if (operativeResource != resource) {
            PrismResource duplicateResource = entityService.getDuplicateEntity(resource);
            if (duplicateResource != null) {
                return new ActionOutcome(actionOwner, resource, actionDAO.getRedirectAction(duplicateResource, action, actionOwner));
            }
        }

        StateTransition stateTransition = stateService.executeStateTransition(operativeResource, resource, getById(action), comment);
        PrismAction transitionAction = stateTransition.getTransitionAction().getId();
        PrismResource nextActionResource = resource.getEnclosingResource(transitionAction.getResourceType());
        return new ActionOutcome(actionOwner, nextActionResource, transitionAction);
    }

}
