package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismResourceType;
import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;
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

    public ActionOutcome executeAction(PrismResource resource, User invoker, PrismAction action, Comment comment) {
        if (!userService.checkUserEnabled(invoker) || !checkActionEnabled(resource, action)) {
            throw new CannotExecuteActionException(resource);
        }

        if (isResourceCreationAction(action)) {
            PrismResource duplicateResource = entityService.getDuplicateEntity(resource);
            if (duplicateResource != null) {
                return new ActionOutcome(invoker, resource, actionDAO.getRedirectAction(duplicateResource, action, invoker));
            }
        }

        List<Role> actionInvokerRoles = getActionInvokerRoles(invoker, resource, action);
        PrismAction nextAction = executeStateTransition(resource, invoker, actionInvokerRoles, action, comment);
        PrismResource nextActionResource = resource.getEnclosingResource(PrismResourceType.valueOf(nextAction.getResourceName()));
        return new ActionOutcome(invoker, nextActionResource, nextAction);
    }

    private PrismAction executeStateTransition(PrismResource resource, User invoker, List<Role> actionInvokerRoles, PrismAction action, Comment comment) {                
        StateTransition transition = stateService.getStateTransition(resource, action, comment);
        resource.setState(transition.getTransitionState());
        
        if (isResourceCreationAction(action)) {
            entityService.save(resource);
            
            if (resource instanceof PrismResourceTransient) {
                PrismResourceTransient codableResource = (PrismResourceTransient) resource;
                codableResource.setCode(codableResource.generateCode());
            }
        }

        executeRoleTransitions(invoker, resource, actionInvokerRoles, transition);
        
        if (transition.isDoPostComment()) {
            comment.setRoles(getActionInvokerRolesAsString(actionInvokerRoles));
            comment.setCreatedTimestamp(new DateTime());
            entityService.save(comment);
        }
        
        return transition.getTransitionAction().getId();
    }

    private void executeRoleTransitions(User invoker, PrismResource resource, List<Role> actionInvokerRoles, StateTransition stateTransition) {
        List<RoleTransition> roleTransitions = roleService.getRoleTransitions(stateTransition, actionInvokerRoles);
        for (RoleTransition roleTransition : roleTransitions) {
            if (roleTransition.getRoleTransitionType() == RoleTransitionType.CREATE) {
                roleService.executeRoleTransition(resource, invoker, roleTransition);
            }
            List<User> otherUsers = roleService.getByRoleTransitionAndResource(roleTransition.getRole(), resource);
            for (User otherUser : otherUsers) {
                roleService.executeRoleTransition(resource, otherUser, roleTransition);
            }
        }
    }
    
    private boolean isResourceCreationAction(PrismAction action) {
        Pattern createPattern = Pattern.compile("([A-Z]+)_CREATE_([A-Z]+)");
        Matcher createMatcher = createPattern.matcher(action.name());
        return createMatcher.matches();
    }

    private boolean checkActionEnabled(PrismResource resource, PrismAction action) {
        return !actionDAO.getValidAction(resource, action).isEmpty();
    }

    private List<Role> getActionInvokerRoles(User user, PrismResource resource, PrismAction action) {
        List<Role> actionInvokerRoles = roleService.getActionInvokerRoles(user, resource, action);
        if (!roleService.getActionRoles(resource, action).isEmpty() && actionInvokerRoles.isEmpty()) {
            throw new CannotExecuteActionException(resource);
        }
        return actionInvokerRoles;
    }

    private String getActionInvokerRolesAsString(List<Role> actionInvokerRoles) {
        String actionInvokerRolesAsString = actionInvokerRoles.get(0).getAuthority();
        for (int i = 1; i < actionInvokerRoles.size(); i++) {
            actionInvokerRolesAsString = actionInvokerRolesAsString + "|" + actionInvokerRoles.get(i).getAuthority();
        }
        return actionInvokerRolesAsString;
    }

}
