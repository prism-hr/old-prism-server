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
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
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

    public UserRole getOrCreateUserRole(Resource resource, User user, PrismRole newRoleId) {
        Role newRole = getById(newRoleId);
        UserRole transientUserRole = new UserRole().withResource(resource).withUser(user).withRole(newRole).withAssignedTimestamp(new DateTime());
        return entityService.getOrCreate(transientUserRole);
    }

    public void getOrCreateUserRoles(Resource resource, User user, PrismRole... rolesToCreate) {
        for (PrismRole roleToCreate : rolesToCreate) {
            getOrCreateUserRole(resource, user, roleToCreate);
        }
    }

    public void removeUserRoles(Resource resource, User user, PrismRole... rolesToRemove) {
        for (UserRole roleToRemove : roleDAO.getUserRoles(resource, user, rolesToRemove)) {
            validateUserRoleRemoval(resource, roleToRemove.getRole());
            for (UserNotification notificationToRemove : roleToRemove.getUserNotifications()) {
                entityService.delete(notificationToRemove);
            }
            entityService.delete(roleToRemove);
        }
        reassignResourceOwner(resource);
    }

    private void validateUserRoleRemoval(Resource resource, Role roleToRemove) {
        Role creatorRole = getCreatorRole(resource);
        if (creatorRole == roleToRemove) {
            List<User> creatorRoleAssignments = getRoleUsers(resource, creatorRole);
            if (creatorRoleAssignments.size() == 1) {
                throw new Error("User attempted to remove the final " + roleToRemove.getAuthority() + " for "
                        + PrismScope.getResourceScope(resource.getClass()).getLowerCaseName() + " " + resource.getCode());
            }
        }
    }
    
    private void reassignResourceOwner(Resource resource) {
        User owner = resource.getUser();
        Role ownerRole = getCreatorRole(resource);
        
        UserRole ownerUserRole = getUserRole(resource, owner, ownerRole);
        if (ownerUserRole == null) {
            User newOwner = getRoleUsers(resource, ownerRole).get(0);
            resource.setUser(newOwner);
        }
    }

    public List<Role> getActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    public List<Role> getDelegateActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    public List<User> getUsers(Resource resource) {
        return roleDAO.getUsers(resource);
    }
    
    public UserRole getUserRole(Resource resource, User user, Role role) {
        return roleDAO.getUserRole(resource, user, role);
    }
    
    public boolean hasUserRole(Resource resource, User user, PrismRole roleId) {
        Role role = getById(roleId);
        return getUserRole(resource, user, role) != null;
    }

    public List<PrismRole> getUserRoles(Resource resource, User user) {
        return roleDAO.getUserRoles(resource, user);
    }

    public List<User> getRoleUsers(Resource resource, Role role) {
        return roleDAO.getRoleUsers(resource, role);
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

    public List<PrismRole> getRoles(Class<? extends Resource> resourceClass) {
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
