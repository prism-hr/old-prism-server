package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Advert;
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
    private ApplicationService applicationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    /**
     * @deprecated use {@link RoleService#getExecutorRoles(User, PrismScope, SystemAction)} instead.
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

    public ActionOutcome executeAction(PrismResource parentResource, User invoker, SystemAction action, Comment comment) {
        if (!userService.checkUserEnabled(invoker)) {
            throw new CannotExecuteActionException(parentResource);
        }

        Pattern createPattern = Pattern.compile("([A-Z]+)_CREATE_([A-Z]+)");
        Matcher createMatcher = createPattern.matcher(action.name());

        PrismResource childResource = parentResource;
        List<Role> invokerRoles = Lists.newArrayList();

        if (createMatcher.matches()) {
            try {
                Class<?> klass = Class.forName(PrismResourceType.valueOf(createMatcher.group(2)).getCanonicalName());
                if (klass.equals(Application.class)) {
                    childResource = applicationService.getOrCreate(invoker, (Advert) parentResource);
                }
            } catch (ClassNotFoundException e) {
                throw new Error("Tried to create a prism resource of invalid type", e);
            }
        } else {
            invokerRoles = getInvokerRoles(invoker, parentResource, action);
        }

        SystemAction nextAction = executeStateTransitions(parentResource, invoker, invokerRoles, action, childResource, comment);
        PrismResource nextActionResource = nextAction != null ? childResource.getEnclosingResource(PrismResourceType.valueOf(nextAction.getResourceName()))
                : null;
        Hibernate.initialize(nextActionResource);
        return new ActionOutcome(invoker, nextActionResource, nextAction);
    }

    private List<Role> getInvokerRoles(User user, PrismResource parentResource, SystemAction action) {
        List<Role> invokerRoles = roleService.getExecutorRoles(user, parentResource, action);
        if (invokerRoles.isEmpty()) {
            throw new CannotExecuteActionException(parentResource);
        }
        return invokerRoles;
    }

    private SystemAction executeStateTransitions(PrismResource parentResource, User invoker, List<Role> invokerRoles, SystemAction action,
            PrismResource childResource, Comment comment) {
        List<StateTransition> stateTransitions = stateService.getUserStateTransitions(parentResource.getState().getId(), action);

        SystemAction nextAction = null;

        for (StateTransition stateTransition : stateTransitions) {
            SystemAction transitionAction = stateTransition.getTransitionAction().getId();
            nextAction = transitionAction != null ? transitionAction : nextAction;

            State transitionState = stateTransition.getTransitionState();
            if (transitionState != null) {
                childResource.setState(stateTransition.getTransitionState());
                entityService.save(childResource);
            }

            executeRoleTransitions(invoker, childResource, invokerRoles, stateTransition);
        }

        postComment(comment, childResource, invoker, invokerRoles);
        return nextAction;
    }

    private void executeRoleTransitions(User invoker, PrismResource resource, List<Role> invokerRoles, StateTransition stateTransition) {
        List<RoleTransition> roleTransitions = roleService.getRoleTransitions(stateTransition, invokerRoles);
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

    private void postComment(Comment comment, PrismResource childResource, User user, List<Role> invokerRoles) {
        if (comment != null) {
            try {
                PropertyUtils.setProperty(comment, childResource.getClass().getSimpleName().toLowerCase(), childResource);
            } catch (Exception e) {
                throw new Error("Tried to create comment for invalid prism resource type", e);
            }
            comment.setUser(user);
            comment.setRoles(getInvokerRolesAsString(invokerRoles));
            comment.setCreatedTimestamp(new DateTime());
            commentService.save(comment);
        }
    }

    private String getInvokerRolesAsString(List<Role> invokerRoles) {
        String invokerRolesAsString = invokerRoles.get(0).getAuthority();
        for (int i = 1; i < invokerRoles.size(); i++) {
            invokerRolesAsString = invokerRolesAsString + "|" + invokerRoles.get(i).getAuthority();
        }
        return invokerRolesAsString;
    }

}
