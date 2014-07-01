package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.ResourceDynamic;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
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

    public void validateAction(Resource resource, PrismAction actionId, User actionOwner) {
        Action action = getById(actionId);
        validateAction(resource, action, actionOwner, null);
    }

    public void validateAction(Resource resource, Action action, User actionOwner, User delegateOwner) {
        if (delegateOwner == null && checkActionAvailable(resource, action, actionOwner)) {
            return;
        } else if (delegateOwner != null && checkActionAvailable(resource, action, delegateOwner)) {
            return;
        } else if (delegateOwner != null && checkDelegateActionAvailable(resource, action, delegateOwner)) {
            return;
        }
        throw new CannotExecuteActionException(resource, action);
    }

    public boolean checkActionAvailable(Resource resource, Action action, User invoker) {
        return roleService.getActionRoles(resource, action).size() == 0 || actionDAO.getPermittedAction(resource, action, invoker) != null;
    }

    public boolean checkDelegateActionAvailable(Resource resource, Action action, User invoker) {
        Action delegateAction = actionDAO.getDelegateAction(resource, action);
        return checkActionAvailable(resource, delegateAction, invoker);
    }

    public List<PrismAction> getPermittedActions(Resource resource, User user) {
        return actionDAO.getPermittedActions(resource, user);
    }

    public ActionOutcome executeAction(Integer resourceId, PrismAction actionId, Comment comment) {
        ResourceDynamic resource = (ResourceDynamic) entityService.getById(actionId.getScope().getResourceClass(), resourceId);
        Action action = getById(actionId);
        return executeAction(resource, action, comment);
    }

    public ActionOutcome executeAction(ResourceDynamic resource, PrismAction actionId, Comment comment) {
        Action action = getById(actionId);
        return executeAction(resource, action, comment);
    }

    public ActionOutcome executeAction(ResourceDynamic resource, Action action, Comment comment) {
        Resource operativeResource = action.isCreationAction() ? resource.getParentResource(resource.getResourceScope()) : resource;
        validateAction(operativeResource, action, comment.getUser(), comment.getDelegateUser());

        User actionOwner = comment.getUser();

        if (action.getCreationScope() != null) {
            ResourceDynamic duplicateResource = entityService.getDuplicateEntity(resource);
            if (duplicateResource != null) {
                Action redirectAction = actionDAO.getRedirectAction(duplicateResource, actionOwner);
                comment = new Comment().withResource(duplicateResource).withUser(actionOwner).withAction(redirectAction);
                executeAction(duplicateResource, redirectAction, comment);
            }
        }

        StateTransition stateTransition = stateService.executeStateTransition(operativeResource, resource, action, comment);
        PrismAction transitionAction = stateTransition.getTransitionAction().getId();
        Resource nextActionResource = resource.getEnclosingResource(transitionAction.getScope());

        return new ActionOutcome(actionOwner, nextActionResource, transitionAction);
    }

    public List<PrismRedactionType> getRedactions(User user, ResourceDynamic resource, Action action) {
        return actionDAO.getRedactions(user, resource, action);
    }

    public List<Action> getActions() {
        return entityService.getAll(Action.class);
    }

    public boolean isCreationAction(PrismState stateId, PrismState transitionStateId, PrismAction actionId) {
        return stateId.getScope().getPrecedence() > transitionStateId.getScope().getPrecedence() && !actionId.name().contains("_CREATE_");
    }

}
