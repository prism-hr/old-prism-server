package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRedactionType;
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
    private ResourceService resourceService;

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

    public void validateAction(Resource resource, Action action, User actionOwner, User delegateOwner) {
        Resource operative = resourceService.getOperativeResource(resource, action);
        if (delegateOwner == null && checkActionAvailable(operative, action, actionOwner)) {
            return;
        } else if (delegateOwner != null && checkActionAvailable(operative, action, delegateOwner)) {
            return;
        } else if (delegateOwner != null && checkDelegateActionAvailable(operative, action, delegateOwner)) {
            return;
        }
        throw new CannotExecuteActionException(operative, action);
    }

    public void validateAction(Resource resource, PrismAction actionId, User actionOwner) {
        Action action = getById(actionId);
        validateAction(resource, action, actionOwner, null);
    }

    public boolean checkActionAvailable(Resource resource, Action action, User invoker) {
        return actionDAO.getPermittedAction(resource, action, invoker) != null;
    }

    public boolean checkDelegateActionAvailable(Resource resource, Action action, User invoker) {
        Action delegateAction = actionDAO.getDelegateAction(resource, action);
        return checkActionAvailable(resource, delegateAction, invoker);
    }

    public List<PrismAction> getPermittedActions(Resource resource, User user) {
        return actionDAO.getPermittedActions(resource, user);
    }
    
    public List<PrismActionEnhancement> getPermittedActionEnhancements(Resource resource, User user) {
        Set<PrismActionEnhancement> enhancements = Sets.newHashSet();  
        enhancements.addAll(actionDAO.getGlobalActionEnhancements(resource, user));
        enhancements.addAll(actionDAO.getCustomActionEnhancements(resource, user));
        return Lists.newArrayList(enhancements);
    }
    
    public ActionOutcome executeUserAction(Resource resource, Action action, Comment comment) {
        validateAction(resource, action, comment.getUser(), comment.getDelegateUser());
        return executeSystemAction(resource, action, comment);
    }

    public ActionOutcome executeSystemAction(Resource resource, Action action, Comment comment) {
        User actionOwner = comment.getUser();

        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE && action.getId() != PrismAction.SYSTEM_STARTUP) {
            Resource duplicateResource = entityService.getDuplicateEntity(resource);
            
            if (duplicateResource != null) {
                Action redirectAction = getRedirectAction(action, actionOwner, duplicateResource);
                if (redirectAction != null) {
                    comment = new Comment().withResource(duplicateResource).withUser(actionOwner).withAction(redirectAction);
                    executeUserAction(duplicateResource, redirectAction, comment);
                }
            }
        }

        StateTransition stateTransition = stateService.executeStateTransition(resource, action, comment);
        Action transitionAction = stateTransition == null ? action : stateTransition.getTransitionAction();
        Resource transitionResource = stateTransition == null ? resource : resource.getEnclosingResource(transitionAction.getScope().getId());

        return new ActionOutcome(actionOwner, resource, transitionResource, transitionAction);
    }

    public Action getRedirectAction(Action action, User actionOwner, Resource duplicateResource) {
        if (action.getActionType() == PrismActionType.USER_INVOCATION) {
            return actionDAO.getUserRedirectAction(duplicateResource, actionOwner);
        } else {
            return actionDAO.getSystemRedirectAction(duplicateResource);
        }
    }

    public List<PrismRedactionType> getRedactions(User user, Resource resource, Action action) {
        return actionDAO.getRedactions(user, resource, action);
    }

    public List<Action> getActions() {
        return entityService.list(Action.class);
    }

}
