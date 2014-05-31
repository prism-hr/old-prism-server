package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
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
        PrismResourceTransient resource = (PrismResourceTransient) entityService.getById(action.getResourceClass(), resourceId);
        return executeAction(resource, user, action, comment);
    }
    
    public ActionOutcome executeAction(PrismResourceTransient resource, User user, PrismAction action, Comment comment) {
        PrismResource operativeResource = resource;
        if (!resource.getClass().equals(action.getResourceClass())) {
            operativeResource = resource.getParentResource(action.getResourceType());
        }
        return executeAction(operativeResource, resource, user, action, comment);
    }

    public ActionOutcome executeAction(PrismResource operativeResource, PrismResourceTransient resource, User invoker, PrismAction action, Comment comment) {
        validateAction(resource, invoker, action);

        if (operativeResource != resource) {
            PrismResource duplicateResource = entityService.getDuplicateEntity(resource);
            if (duplicateResource != null) {
                return new ActionOutcome(invoker, resource, actionDAO.getRedirectAction(duplicateResource, action, invoker));
            }
        }

        StateTransition stateTransition = stateService.executeStateTransition(operativeResource, resource, invoker, action, comment);

        StateTransition delegateStateTransition = stateTransition.getDelegateStateTransition();
        if (delegateStateTransition != null) {
            stateService.executeDelegateStateTransition(resource, delegateStateTransition, comment.getUser());
        }
        
        if (!stateTransition.getPropagatedStateTransitions().isEmpty()) {
            stateService.executePropagatedStateTransitions(resource, stateTransition);
        }
        
        // TODO: dispatch update emails
        // TODO: dispatch request emails
        
        PrismAction transitionAction = stateTransition.getTransitionAction().getId();
        PrismResource nextActionResource = resource.getEnclosingResource(transitionAction.getResourceType());
        return new ActionOutcome(invoker, nextActionResource, transitionAction);
    }

}
