package com.zuehlke.pgadmissions.services;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private NotificationService notificationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserService userService;

    public Role getById(PrismRole roleId) {
        return entityService.getByProperty(Role.class, "id", roleId);
    }

    public UserRole getUserRoleById(Integer id) {
        return entityService.getById(UserRole.class, id);
    }

    public List<Role> getRoles() {
        return entityService.list(Role.class);
    }

    public UserRole getOrCreateUserRole(Resource resource, User user, PrismRole newRoleId) throws WorkflowEngineException {
        Role newRole = getById(newRoleId);
        UserRole transientUserRole = new UserRole().withResource(resource).withUser(user).withRole(newRole).withAssignedTimestamp(new DateTime());
        if (isRoleAssignmentPermitted(transientUserRole)) {
            return entityService.getOrCreate(transientUserRole);
        }
        throw new WorkflowEngineException("Unable to assign role of type " + newRoleId.toString() + " to " + resource.getCode()
                + " due to existing permission conflicts");
    }

    public void getOrCreateUserRoles(Resource resource, User user, PrismRole... rolesToCreate) throws WorkflowEngineException {
        for (PrismRole roleToCreate : rolesToCreate) {
            getOrCreateUserRole(resource, user, roleToCreate);
        }
    }

    public void updateRoles(Resource resource, User user, List<AbstractResourceRepresentation.RoleRepresentation> roles) throws WorkflowEngineException {
        for (AbstractResourceRepresentation.RoleRepresentation role : roles) {
            if (role.getValue()) {
                getOrCreateUserRole(resource, user, role.getId());
            } else {
                deleteUserRoles(resource, user, role.getId());
            }
        }
    }

    public List<PrismRole> getActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    public List<User> getUsers(Resource resource) {
        return roleDAO.getUsers(resource);
    }

    public boolean hasUserRole(Resource resource, User user, PrismRole roleId) {
        Role role = getById(roleId);
        return roleDAO.getUserRole(resource, user, role) != null;
    }

    public List<PrismRole> getUserRoles(Resource resource, User user) {
        return roleDAO.getUserRoles(resource, user);
    }

    public List<User> getRoleUsers(Resource resource, Role role) {
        return roleDAO.getRoleUsers(resource, role);
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

    public Role getCreatorRole(Resource resource) {
        return roleDAO.getCreatorRole(resource);
    }

    public void deleteExludedRoles() {
        for (Role role : entityService.list(Role.class)) {
            role.getExcludedRoles().clear();
        }
    }

    public boolean isRoleAssignmentPermitted(UserRole userRole) {
        return roleDAO.getExcludingUserRoles(userRole).isEmpty();
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
            Integer maximumPermitted = roleTransition.getMaximumPermitted();

            if ((minimumPermitted == null || users.size() >= minimumPermitted) && (maximumPermitted == null || users.size() <= maximumPermitted)) {
                for (User user : users) {
                    userRoleTransitions.put(user, roleTransition);
                }
            } else {
                throw new WorkflowEngineException("Attempted to process " + users.size() + " users of role: " + roleTransition.getRole().getAuthority()
                        + ". Expected " + minimumPermitted + " <= n <=" + maximumPermitted);
            }
        }

        return userRoleTransitions;
    }

    private void executeRoleTransition(Comment comment, User user, RoleTransition roleTransition) throws WorkflowEngineException {
        DateTime baseline = new DateTime();
        Resource resource = comment.getResource();
        Resource operative = resourceService.getOperativeResource(resource, comment.getAction());
        UserRole transientRole = new UserRole().withResource(operative).withUser(user).withRole(roleTransition.getRole()).withAssignedTimestamp(baseline);
        UserRole transientTransitionRole = new UserRole().withResource(resource).withUser(user).withRole(roleTransition.getTransitionRole())
                .withAssignedTimestamp(baseline);

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
        if (persistentRole == null) {
            throw new WorkflowEngineException("Found no role of type " + userRole.getRole().getAuthority() + " for " + userRole.getResource().getCode()
                    + " to branch for user " + userRole.getUser().toString());
        } else if (!isRoleAssignmentPermitted(userRole, comment)) {
            throw new WorkflowEngineException("Unable to assign role of type " + transitionRole.getRole().getAuthority() + " for "
                    + transitionRole.getResource().getCode() + " due to existing permission conflicts");
        }
        entityService.getOrCreate(transitionRole);
        comment.withAssignedUser(transitionRole.getUser(), transitionRole.getRole());
    }

    private void executeCreateUserRole(UserRole userRole, Comment comment) throws WorkflowEngineException {
        if (!isRoleAssignmentPermitted(userRole, comment)) {
            throw new WorkflowEngineException("Unable to assign role of type " + userRole.getRole().getAuthority() + " for " + userRole.getResource().getCode()
                    + " due to existing permission conflicts");
        }
        entityService.getOrCreate(userRole);
    }

    private void executeRemoveUserRole(UserRole userRole) {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole != null) {
            deleteUserRoles(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole().getId());
        }
    }

    private boolean isRoleAssignmentPermitted(UserRole userRole, Comment comment) {
        return userRole.getRole().getExcludedRoles().isEmpty()
                || (roleDAO.getExcludingRoles(userRole, comment).isEmpty() && isRoleAssignmentPermitted(userRole));
    }

    private void executeUpdateUserRole(UserRole userRole, UserRole transitionRole) throws WorkflowEngineException {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole == null) {
            throw new WorkflowEngineException("Found no role of type " + userRole.getRole().getAuthority() + " for " + userRole.getResource().getCode()
                    + " to update for user " + userRole.getUser().toString());
        }
        deleteUserRoles(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole().getId());
        entityService.getOrCreate(transitionRole);
    }

    private void deleteUserRoles(Resource resource, User user, PrismRole... rolesToRemove) {
        for (UserRole roleToRemove : roleDAO.getUserRoles(resource, user, rolesToRemove)) {
            validateUserRoleRemoval(resource, roleToRemove.getRole());
            notificationService.deleteUserNotification(roleToRemove);
            entityService.delete(roleToRemove);
        }
        reassignResourceOwner(resource);
    }

    private void validateUserRoleRemoval(Resource resource, Role roleToRemove) {
        Role creatorRole = getCreatorRole(resource);
        if (creatorRole == roleToRemove) {
            List<User> creatorRoleAssignments = getRoleUsers(resource, creatorRole);
            if (creatorRoleAssignments.size() == 1) {
                throw new Error();
            }
        }
    }

    private void reassignResourceOwner(Resource resource) {
        User owner = resource.getUser();
        Role ownerRole = getCreatorRole(resource);
        if (!hasUserRole(resource, owner, ownerRole.getId())) {
            User newOwner = getRoleUsers(resource, ownerRole).get(0);
            resource.setUser(newOwner);
        }
    }

}
