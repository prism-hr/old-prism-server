package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.representation.ResourceRepresentation;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    public Role getById(PrismRole roleId) {
        return entityService.getByProperty(Role.class, "id", roleId);
    }

    public List<Role> getRoles() {
        return entityService.list(Role.class);
    }

    public UserRole createUserRole(Resource resource, User user, PrismRole roleId) {
        Role role = getById(roleId);
        UserRole userRole = new UserRole();
        userRole.setResource(resource);
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedTimestamp(new DateTime());
        return userRole;
    }

    public UserRole getOrCreateUserRole(Resource resource, User user, PrismRole roleToCreate) {
        UserRole transientUserRole = createUserRole(resource, user, roleToCreate);
        return entityService.getOrCreate(transientUserRole);
    }

    public void getOrCreateUserRoles(Resource resource, User user, PrismRole... rolesToCreate) {
        for (PrismRole roleToCreate : rolesToCreate) {
            getOrCreateUserRole(resource, user, roleToCreate);
        }
    }

    public void removeUserRoles(Resource resource, User user, PrismRole... rolesToRemove) {
        for (UserRole roleToRemove : roleDAO.getUserRoles(resource, user, rolesToRemove)) {
            for (UserNotification notificationToRemove : roleToRemove.getUserNotifications()) {
                entityService.delete(notificationToRemove);
            }
            entityService.delete(roleToRemove);
        }
    }

    @Deprecated
    public boolean hasRole(User user, PrismRole authority) {
        return hasRole(null, user, authority);
    }

    public boolean hasRole(Resource scope, User user, PrismRole authority) {
        return roleDAO.getUserRole(user, scope, authority) != null;
    }

    @Deprecated
    public User getUserInRole(Resource scope, PrismRole... authorities) {
        List<User> users = roleDAO.getUsersByRole(scope, authorities);
        return users.get(0);
    }

    @Deprecated
    public List<Program> getProgramsByUserAndRole(User currentUser, PrismRole administrator) {
        return null;
    }

    @Deprecated
    public List<User> getProgramAdministrators(Program program) {
        return null;
    }

    public List<Role> getActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    public List<Role> getDelegateActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    public List<User> getResourceUsers(Resource resource) {
        return roleDAO.getUsers(resource);
    }

    public List<PrismRole> getResourceUserRoles(Resource resource, User user) {
        return roleDAO.getUserRoles(resource, user);
    }

    public void updateRoles(Resource resource, User user, List<ResourceRepresentation.RoleRepresentation> roles) {
        for (ResourceRepresentation.RoleRepresentation role : roles) {
            if (role.getValue()) {
                getOrCreateUserRole(resource, user, role.getId());
            } else {
                removeUserRoles(resource, user, role.getId());
            }
        }
    }

    public List<PrismRole> getRoles(Class<? extends Resource> resourceClass, PrismRole... rolesToCreate) {
        return roleDAO.getRoles(resourceClass);
    }

    public List<Role> getActiveRoles() {
        return roleDAO.getActiveRoles();
    }

    public void deleteInactiveRoles() {
        roleDAO.deleteObseleteUserRoles(getActiveRoles());
    }

    public List<UserRole> getUpdateNotificationRoles(User user, Resource resource, NotificationTemplate template) {
        return roleDAO.getUpdateNotificationRoles(user, resource, template);
    }

    public List<Role> getAssignedRoles(StateAction stateAction) {
        List<Role> assignedRoles = roleDAO.getAssignedRoles(stateAction);
        return assignedRoles.isEmpty() ? getRoles() : assignedRoles;
    }

    public Role getCreatorRole(Resource resource) {
        return roleDAO.getCreatorRole(resource);
    }

    public void executeRoleTransitions(StateTransition stateTransition, Comment comment) throws WorkflowEngineException {
        for (PrismRoleTransitionType transitionType : PrismRoleTransitionType.values()) {
            HashMultimap<User, RoleTransition> userRoleTransitions = null;
            List<RoleTransition> roleTransitions = roleDAO.getRoleTransitions(stateTransition, transitionType);

            if (transitionType == PrismRoleTransitionType.CREATE) {
                userRoleTransitions = getRoleCreateTransitions(stateTransition, comment, roleTransitions);
            } else {
                userRoleTransitions = getRoleUpdateTransitions(stateTransition, comment, roleTransitions);
            }

            for (User user : userRoleTransitions.keySet()) {
                for (RoleTransition roleTransition : userRoleTransitions.get(user)) {
                    executeRoleTransition(comment, user, roleTransition);
                }
            }
        }
    }

    private HashMultimap<User, RoleTransition> getRoleUpdateTransitions(StateTransition stateTransition, Comment comment, List<RoleTransition> roleTransitions) {
        HashMultimap<User, RoleTransition> userRoleTransitions = HashMultimap.create();

        for (RoleTransition roleTransition : roleTransitions) {
            User restrictedToUser = roleTransition.isRestrictToActionOwner() ? comment.getUser() : null;
            List<User> users = roleDAO.getRoleTransitionUsers(comment.getResource(), roleTransition, restrictedToUser);

            for (User user : users) {
                userRoleTransitions.put(user, roleTransition);
            }
        }

        return userRoleTransitions;
    }

    private HashMultimap<User, RoleTransition> getRoleCreateTransitions(StateTransition stateTransition, Comment comment, List<RoleTransition> roleTransitions)
            throws WorkflowEngineException {
        HashMultimap<User, RoleTransition> userRoleTransitions = HashMultimap.create();

        for (RoleTransition roleTransition : roleTransitions) {
            User restrictedToUser = roleTransition.isRestrictToActionOwner() ? comment.getUser() : null;
            List<User> users = roleDAO.getRoleCreateTransitionUsers(comment, roleTransition.getRole(), restrictedToUser);

            Integer minimumPermitted = roleTransition.getMinimumPermitted();
            Integer maximumPermitted = roleTransition.getMinimumPermitted();

            if ((minimumPermitted == null || users.size() >= minimumPermitted) && (maximumPermitted == null || users.size() <= maximumPermitted)) {
                for (User user : users) {
                    userRoleTransitions.put(user, roleTransition);
                }
            } else {
                throw new WorkflowEngineException();
            }
        }

        return userRoleTransitions;
    }

    private void executeRoleTransition(Comment comment, User user, RoleTransition roleTransition) throws WorkflowEngineException {
        DateTime baseline = new DateTime();
        Resource resource = comment.getResource();
        UserRole transientRole = new UserRole().withResource(resource).withUser(user).withRole(roleTransition.getRole()).withAssignedTimestamp(baseline);
        UserRole transientTransitionRole = new UserRole().withResource(resource).withRole(roleTransition.getTransitionRole()).withAssignedTimestamp(baseline);

        switch (roleTransition.getRoleTransitionType()) {
        case BRANCH:
            executeBranchUserRole(transientRole, transientTransitionRole, comment);
            break;
        case CREATE:
            executeCreateUserRole(transientRole, comment);
            break;
        case REMOVE:
            executeRemoveUserRole(transientRole);
            break;
        case UPDATE:
            executeUpdateUserRole(transientRole, transientTransitionRole);
        }
    }

    private void executeBranchUserRole(UserRole userRole, UserRole transitionRole, Comment comment) throws WorkflowEngineException {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole == null || !isRoleAssignmentPermitted(userRole, comment)) {
            throw new WorkflowEngineException();
        }
        entityService.getOrCreate(transitionRole);
    }

    private void executeCreateUserRole(UserRole userRole, Comment comment) throws WorkflowEngineException {
        if (!isRoleAssignmentPermitted(userRole, comment)) {
            throw new WorkflowEngineException();
        }
        entityService.getOrCreate(userRole);
    }

    private void executeRemoveUserRole(UserRole userRole) {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole != null) {
            entityService.delete(persistentRole);
        }
    }

    private boolean isRoleAssignmentPermitted(UserRole userRole, Comment comment) {
        return userRole.getRole().getExcludedRoles().isEmpty()
                || (roleDAO.getExcludingRoles(userRole, comment).isEmpty() && roleDAO.getExcludingUserRoles(userRole).isEmpty());
    }

    private void executeUpdateUserRole(UserRole userRole, UserRole transientTransitionRole) throws WorkflowEngineException {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole == null) {
            throw new WorkflowEngineException();
        }
        entityService.delete(persistentRole);
        entityService.getOrCreate(transientTransitionRole);
    }

}
