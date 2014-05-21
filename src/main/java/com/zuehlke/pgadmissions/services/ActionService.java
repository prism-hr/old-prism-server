package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismResourceType;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionType;
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
    private StateDAO stateDAO;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CommentService commentService;

    /**
     * @deprecated use {@link RoleService#canExecute(User, PrismScope, SystemAction)} instead.
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

    public ActionOutcome executeAction(Integer scopeId, User user, SystemAction action, Comment comment) throws Exception {
        PrismResource scope = entityService.getById(action.getScopeClass(), scopeId);
        return executeAction(scope, user, action, comment);
    }

    @SuppressWarnings("unchecked")
    public ActionOutcome executeAction(PrismResource resource, User user, SystemAction action, Comment comment) throws Exception {
        List<Role> invokerRoles = roleService.canExecute(user, resource, action);
        if (invokerRoles.isEmpty()) {
            throw new CannotExecuteActionException(resource);
        }

        Pattern createPattern = Pattern.compile("([A-Z]+)_CREATE_([A-Z]+)");
        Matcher createMatcher = createPattern.matcher(action.name());

        PrismResource newResource = resource;
        if (createMatcher.matches()) {
            String newResourceType = createMatcher.group(2);
            newResource = applicationService.getOrCreate(user, resource, PrismResourceType.valueOf(newResourceType));
        }

        SystemAction nextAction = executeStateTransitions(resource, user, invokerRoles, action, newResource, comment);
        entityService.save(newResource);
        PrismResource nextActionResource = nextAction != null ? newResource.getEnclosingResource(PrismResourceType.valueOf(nextAction.getScopeName())) : null;
        Hibernate.initialize(nextActionResource);
        return new ActionOutcome(user, nextActionResource, nextAction);
    }

    private SystemAction executeStateTransitions(PrismResource resource, User user, List<Role> invokerRoles, SystemAction action,
            PrismResource newResource, Comment comment) {
        
        String invokerRolesAsString = invokerRoles.get(0).getAuthority();
        for (int i = 1; i < invokerRoles.size(); i++) {
            invokerRolesAsString = invokerRolesAsString + "|" + invokerRoles.get(i).getAuthority();
        }
        
        List<StateTransition> stateTransitions = stateDAO.getStateTransitions(resource.getState().getId(), action, StateTransitionType.ONE_COMPLETED,
                StateTransitionType.ALL_COMPLETED, StateTransitionType.PROPAGATION);

        SystemAction nextAction = null;
        for (StateTransition stateTransition : stateTransitions) {
            SystemAction transitionAction = stateTransition.getTransitionAction().getId();
            nextAction = transitionAction != null ? transitionAction : nextAction;
            newResource.setState(stateTransition.getTransitionState());

            if (comment != null) {
                comment.setUser(user);
                comment.setRoles(invokerRolesAsString);
                comment.setCreatedTimestamp(new DateTime());
                commentService.save(comment);
            }

            executeRoleTransitions(invokerRoles, resource, stateTransition, newResource);
        }
        return nextAction;
    }

    private void executeRoleTransitions(List<Role> invokerRoles, PrismResource scope, StateTransition stateTransition, PrismResource newScope) {
        List<RoleTransition> roleTransitions = roleService.getRoleTransitions(stateTransition, invokerRoles);
        for (RoleTransition roleTransition : roleTransitions) {
            Role role = roleTransition.getRole();
            List<User> users = roleService.getBy(role, scope);
            for (User roleUser : users) {
                roleService.executeRoleTransition(scope, roleUser, role, roleTransition.getType(), newScope, roleTransition.getTransitionRole());
            }
        }
    }
}
