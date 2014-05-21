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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.PrismResourceType;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionType;
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
     * @deprecated use {@link RoleService#canExecute(User, PrismScope, ApplicationFormAction)} instead.
     */
    @Deprecated
    public void validateAction(final Application application, final User user, final ApplicationFormAction action) {
        if (!checkActionAvailable(application, user, action)) {
            throw new CannotExecuteActionException(application);
        }
    }

    public boolean checkActionAvailable(final Application application, final User user, final ApplicationFormAction action) {
        return !actionDAO.getUserActionById(application.getId(), user.getId(), action).isEmpty();
    }

    public List<ActionDefinition> getUserActions(Integer applicationFormId, Integer userId) {
        return actionDAO.getUserActions(applicationFormId, userId);
    }

    public ActionOutcome executeAction(Integer scopeId, User user, ApplicationFormAction action, Comment comment) throws Exception {
        PrismResource scope = entityService.getById(action.getScopeClass(), scopeId);
        return executeAction(scope, user, action, comment);
    }

    @SuppressWarnings("unchecked")
    public ActionOutcome executeAction(PrismResource resource, User user, ApplicationFormAction action, Comment comment) throws Exception {
        Role invokingRole = roleService.canExecute(user, resource, action);
        if (invokingRole == null) {
            throw new CannotExecuteActionException(resource);
        }

        Pattern createPattern = Pattern.compile("([A-Z]+)_CREATE_([A-Z]+)");
        Matcher createMatcher = createPattern.matcher(action.name());

        PrismResource newResource = resource;
        if (createMatcher.matches()) {
            String newResourceType = createMatcher.group(2);
            newResource = applicationService.getOrCreate(user, resource, PrismResourceType.valueOf(newResourceType));
        }

        ApplicationFormAction nextAction = executeStateTransition(resource, user, invokingRole, action, newResource, comment);
        entityService.save(newResource);
        PrismResource nextActionResource = nextAction != null ? newResource.getEnclosingResource(PrismResourceType.valueOf(nextAction.getScopeName())) : null;
        Hibernate.initialize(nextActionResource);
        return new ActionOutcome(user, nextActionResource, nextAction);
    }

    private ApplicationFormAction executeStateTransition(PrismResource scope, User user, Role invokingRole, ApplicationFormAction action, PrismResource newScope,
            Comment comment) {
        List<StateTransition> stateTransitions = stateDAO.getStateTransitions(scope.getState().getId(), action, StateTransitionType.ONE_COMPLETED,
                StateTransitionType.ALL_COMPLETED);

        ApplicationFormAction nextAction = null;
        for (StateTransition stateTransition : stateTransitions) {
            ApplicationFormAction transitionAction = stateTransition.getTransitionAction().getId();
            nextAction = transitionAction != null ? transitionAction : nextAction;
            newScope.setState(stateTransition.getTransitionState());

            if (comment != null) {
                comment.setUser(user);
                comment.setCreatedTimestamp(new DateTime());
                commentService.save(comment);
            }

            executeRoleTransitions(invokingRole, scope, stateTransition, newScope);
        }
        return nextAction;
    }

    private void executeRoleTransitions(Role invokingRole, PrismResource scope, StateTransition stateTransition, PrismResource newScope) {
        List<RoleTransition> roleTransitions = roleService.getRoleTransitions(stateTransition, invokingRole);
        for (RoleTransition roleTransition : roleTransitions) {
            Role role = roleTransition.getRole();
            List<User> users = roleService.getBy(role, scope);
            for (User roleUser : users) {
                roleService.executeRoleTransition(scope, roleUser, role, roleTransition.getType(), newScope, roleTransition.getTransitionRole());
            }
        }
    }
}
