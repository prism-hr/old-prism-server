package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismResourceType;
import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
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

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @deprecated use {@link RoleService#getActionactionInvokerRoles(User, PrismScope, SystemAction)} instead.
     */
    @Deprecated
    public void validateAction(final Application application, final User user, final SystemAction action) {
        if (!checkActionAvailable(application, user, action)) {
            throw new CannotExecuteActionException(application);
        }
    }

    public boolean checkActionAvailable(final Application application, final User user, final SystemAction action) {
        return !actionDAO.getUserActionById(application.getId(), user.getId(), action).isEmpty();
    }

    public List<ActionDefinition> getUserActions(Integer applicationFormId, Integer userId) {
        return actionDAO.getUserActions(applicationFormId, userId);
    }

    public ActionOutcome executeAction(Integer resourceId, User user, SystemAction action, Comment comment) {
        PrismResource resource = entityService.getById(action.getResourceClass(), resourceId);
        return executeAction(resource, user, action, comment);
    }

    public ActionOutcome executeAction(PrismResource resource, User invoker, SystemAction action, Comment comment) {
        if (!userService.checkUserEnabled(invoker) || !checkActionEnabled(resource, action)) {
            throw new CannotExecuteActionException(resource);
        }

        Pattern createPattern = Pattern.compile("([A-Z]+)_CREATE_([A-Z]+)");
        Matcher createMatcher = createPattern.matcher(action.name());

        List<Role> actionInvokerRoles = Lists.newArrayList();

        if (createMatcher.matches()) {
            try {
                PrismResourceType childResourceType = PrismResourceType.valueOf(createMatcher.group(2));
                Object resourceService = applicationContext.getBean(childResourceType.toString().toLowerCase() + "Service");
                resource = (PrismResource) MethodUtils.invokeExactMethod(resourceService, "getOrCreate", new Object[] { invoker, resource });
            } catch (Exception e) {
                throw new Error("Tried to create a prism resource of invalid type", e);
            }
        }

        actionInvokerRoles = getActionInvokerRoles(invoker, resource, action);
        SystemAction nextAction = executeStateTransitions(resource, invoker, actionInvokerRoles, action, comment);
        PrismResource nextActionResource = nextAction != null ? resource.getEnclosingResource(PrismResourceType.valueOf(nextAction.getResourceName())) : null;
        Hibernate.initialize(nextActionResource);
        return new ActionOutcome(invoker, nextActionResource, nextAction);
    }

    public boolean checkActionEnabled(PrismResource resource, SystemAction action) {
        return !actionDAO.getPermittedAction(resource, action).isEmpty();
    }

    private List<Role> getActionInvokerRoles(User user, PrismResource resource, SystemAction action) {
        List<Role> actionInvokerRoles = roleService.getActionInvokerRoles(user, resource, action);
        if (!roleService.getActionRoles(resource, action).isEmpty() && actionInvokerRoles.isEmpty()) {
            throw new CannotExecuteActionException(resource);
        }
        return actionInvokerRoles;
    }

    private SystemAction executeStateTransitions(PrismResource resource, User invoker, List<Role> actionInvokerRoles, SystemAction action, Comment comment) {
        List<StateTransition> stateTransitions = stateService.getUserStateTransitions(resource.getState().getId(), action);

        SystemAction nextAction = null;

        for (StateTransition stateTransition : stateTransitions) {
            SystemAction transitionAction = stateTransition.getTransitionAction().getId();
            nextAction = transitionAction != null ? transitionAction : nextAction;

            State transitionState = stateTransition.getTransitionState();
            if (transitionState != null) {
                resource.setState(stateTransition.getTransitionState());
                entityService.save(resource);
            }

            executeRoleTransitions(invoker, resource, actionInvokerRoles, stateTransition);
        }

        postComment(comment, resource, invoker, actionInvokerRoles);
        return nextAction;
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

    private void postComment(Comment comment, PrismResource childResource, User user, List<Role> actionInvokerRoles) {
        if (comment != null) {
            try {
                PropertyUtils.setProperty(comment, childResource.getClass().getSimpleName().toLowerCase(), childResource);
            } catch (Exception e) {
                throw new Error("Tried to create comment for invalid prism resource type", e);
            }
            comment.setUser(user);
            comment.setRoles(getActionInvokerRolesAsString(actionInvokerRoles));
            comment.setCreatedTimestamp(new DateTime());
            commentService.save(comment);
        }
    }

    private String getActionInvokerRolesAsString(List<Role> actionInvokerRoles) {
        String actionInvokerRolesAsString = actionInvokerRoles.get(0).getAuthority();
        for (int i = 1; i < actionInvokerRoles.size(); i++) {
            actionInvokerRolesAsString = actionInvokerRolesAsString + "|" + actionInvokerRoles.get(i).getAuthority();
        }
        return actionInvokerRolesAsString;
    }

}
