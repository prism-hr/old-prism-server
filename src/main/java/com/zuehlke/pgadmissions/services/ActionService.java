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
    
    public void validateAction(PrismResource resource, PrismAction actionId, User actionOwner) {
        Action action = getById(actionId);
        validateAction(resource, action, actionOwner, null);
    }

    public void validateAction(PrismResource resource, Action action, User actionOwner, User delegateOwner) {
        if (delegateOwner == null && checkActionAvailable(resource, action, actionOwner)) {
            return;
        } else if (delegateOwner != null && checkActionAvailable(resource, action, delegateOwner)) {
            return;
        } else if (delegateOwner != null && checkDelegateActionAvailable(resource, action, delegateOwner)) {
            return;
        } 
        throw new CannotExecuteActionException(resource, action);
    }

    public boolean checkActionAvailable(PrismResource resource, Action action, User invoker) {
        return roleService.getActionRoles(resource, action).size() == 0 || actionDAO.getPermittedAction(resource, action, invoker) != null;
    }

    public boolean checkDelegateActionAvailable(PrismResource resource, Action action, User invoker) {
        Action delegateAction = actionDAO.getDelegateAction(resource, action);
        return checkActionAvailable(resource, delegateAction, invoker);
    }

    public List<StateAction> getPermittedActions(User user, PrismResource resource) {
        return actionDAO.getPermittedActions(resource, user);
    }

    public ActionOutcome executeAction(Integer resourceId, PrismAction actionId, Comment comment) {
        PrismResourceDynamic resource = (PrismResourceDynamic) entityService.getById(actionId.getResourceClass(), resourceId);
        Action action = getById(actionId);
        return executeAction(resource, action, comment);
    }
    
    public ActionOutcome executeAction(PrismResourceDynamic resource, PrismAction actionId, Comment comment) {
        Action action = getById(actionId);
        return executeAction(resource, action, comment);
    }

    public ActionOutcome executeAction(PrismResourceDynamic resource, Action action, Comment comment) {
        PrismResource operativeResource = resource;
        if (!resource.getClass().equals(action.getId().getResourceClass())) {
            operativeResource = resource.getParentResource(action.getId().getResourceType());
        }
        return executeAction(operativeResource, resource, action, comment);
    }

    public ActionOutcome executeAction(PrismResource operativeResource, PrismResourceDynamic resource, Action action, Comment comment) {
        validateAction(resource, action, comment.getUser(), comment.getDelegateUser());

        User actionOwner = comment.getUser();

        if (operativeResource != resource) {
            PrismResourceDynamic duplicateResource = entityService.getDuplicateEntity(resource);
            if (duplicateResource != null) {
                Action redirectAction = actionDAO.getRedirectAction(duplicateResource, actionOwner);
                comment = new Comment().withResource(duplicateResource).withUser(actionOwner).withAction(redirectAction);
                executeAction(duplicateResource, redirectAction, comment);
            }
        }

        StateTransition stateTransition = stateService.executeStateTransition(operativeResource, resource, action, comment);
        PrismAction transitionAction = stateTransition.getTransitionAction().getId();
        PrismResource nextActionResource = resource.getEnclosingResource(transitionAction.getResourceType());
        
        return new ActionOutcome(actionOwner, nextActionResource, transitionAction);
    }

}
