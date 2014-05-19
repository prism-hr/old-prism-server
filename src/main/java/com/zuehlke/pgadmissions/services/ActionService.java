package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
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
    private ApplicationFormService applicationService;

    @Autowired
    private EntityCreationService entityCreationService;

    @Autowired
    private CommentService commentService;

    @Deprecated
    public void validateAction(final ApplicationForm application, final User user, final ApplicationFormAction action) {
        if (!checkActionAvailable(application, user, action)) {
            throw new CannotExecuteActionException(application);
        }
    }

    public boolean checkActionAvailable(final ApplicationForm application, final User user, final ApplicationFormAction action) {
        return !actionDAO.getUserActionById(application.getId(), user.getId(), action).isEmpty();
    }

    public List<ActionDefinition> getUserActions(Integer applicationFormId, Integer userId) {
        return actionDAO.getUserActions(applicationFormId, userId);
    }

    public ActionOutcome executeAction(Integer scopeId, User user, ApplicationFormAction action, Comment comment) {
        PrismScope scope = entityService.getById(action.getScopeClass(), scopeId);
        return executeAction(scope, user, action, comment);
    }

    public ActionOutcome executeAction(PrismScope scope, User user, ApplicationFormAction action, Comment comment) {
        Role invokingRole = roleService.canExecute(user, scope, action);
        if (invokingRole == null) {
            throw new CannotExecuteActionException(scope);
        }

        Pattern createPattern = Pattern.compile("([A-Z]+)_CREATE_([A-Z]+)");
        Matcher createMatcher = createPattern.matcher(action.name());

        PrismScope newScope = scope;
        if (createMatcher.matches()) {
            String newScopeName = createMatcher.group(2).toLowerCase();
            newScope = entityCreationService.create(user, scope, newScopeName);
        }

        ApplicationFormAction nextAction = executeStateTransition(scope, user, invokingRole, action, newScope, comment);
        entityService.save(newScope);
        PrismScope nextActionScope = nextAction != null ? newScope.getEnclosingScope(nextAction.getScopeName()) : null;
        return new ActionOutcome(user, nextActionScope, nextAction);
    }

    private ApplicationFormAction executeStateTransition(PrismScope scope, User user, Role invokingRole, ApplicationFormAction action, PrismScope newScope,
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
                comment.setCreatedTimestamp(new Date());
                commentService.save(comment);
            }

            executeRoleTransitions(invokingRole, scope, stateTransition, newScope);
        }
        return nextAction;
    }

    private void executeRoleTransitions(Role invokingRole, PrismScope scope, StateTransition stateTransition, PrismScope newScope) {
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
