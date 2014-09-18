package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation.RoleRepresentation;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private ActionService actionService;

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

    public UserRole getUserRole(Resource resource, User user, Role role) {
        return roleDAO.getUserRole(resource, user, role);
    }

    public List<Role> getRoles() {
        return entityService.list(Role.class);
    }

    public UserRole getOrCreateUserRole(UserRole userRole) throws DeduplicationException {
        return getOrCreateUserRole(userRole.getResource(), userRole.getUser(), userRole.getRole().getId());
    }

    public UserRole getOrCreateUserRole(Resource resource, User user, PrismRole newRoleId) throws DeduplicationException {
        Role newRole = getById(newRoleId);
        UserRole transientUserRole = new UserRole().withResource(resource).withUser(user).withRole(newRole).withAssignedTimestamp(new DateTime());
        if (newRole.getExcludedRoles().isEmpty() || roleDAO.getExcludingUserRoles(transientUserRole).isEmpty()) {
            UserRole persistentUserRole = entityService.getOrCreate(transientUserRole);
            entityService.flush();
            return persistentUserRole;
        }
        Action action = actionService.getViewEditAction(resource);
        throwWorkflowPermissionException(resource, action, user, newRole);
        return null;
    }

    public void updateUserRoles(Resource resource, User user, List<RoleRepresentation> roleRepresentations) throws DeduplicationException {
        User invoker = userService.getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        
        Comment comment = new Comment().withUser(invoker).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false);
        
        for (RoleRepresentation roleRepresentation : roleRepresentations) {
            Role role = getById(roleRepresentation.getId());
            comment.addAssignedUser(user, role, roleRepresentation.getValue() ? PrismRoleTransitionType.CREATE : PrismRoleTransitionType.DELETE);
        }
        
        actionService.executeUserAction(resource, action, comment);
    }

    public List<PrismRole> getActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
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

    public List<User> getRoleUsers(Resource resource, PrismRole roleId) {
        Role role = getById(roleId);
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

    public void executeRoleTransitions(StateTransition stateTransition, Comment comment) throws DeduplicationException {
        for (PrismRoleTransitionType roleTransitionType : PrismRoleTransitionType.values()) {
            for (RoleTransition roleTransition : roleDAO.getRoleTransitions(stateTransition, roleTransitionType)) {
                List<User> users = getRoleTransitionUsers(stateTransition, comment, roleTransition);
                for (User user : users) {
                    executeRoleTransition(comment, user, roleTransition);
                }
            }
        }
    }

    private List<User> getRoleTransitionUsers(StateTransition stateTransition, Comment comment, RoleTransition roleTransition) throws WorkflowEngineException {
        User actionOwner = comment.getUser();
        Resource resource = comment.getResource();
        User restrictedToUser = roleTransition.isRestrictToActionOwner() ? actionOwner : null;

        List<User> users = Lists.newArrayList();
        if (roleTransition.getRoleTransitionType().isSpecified()) {
            users = getSpecifiedRoleTransitionUsers(comment, roleTransition, restrictedToUser);
        } else {
            users = roleDAO.getUnspecifiedRoleTransitionUsers(resource, roleTransition, restrictedToUser);
        }

        Integer minimumPermitted = roleTransition.getMinimumPermitted();
        Integer maximumPermitted = roleTransition.getMaximumPermitted();

        if (!(minimumPermitted == null || users.size() >= minimumPermitted) && (maximumPermitted == null || users.size() <= maximumPermitted)) {
            actionService.throwWorkflowEngineException(comment.getResource(), comment.getAction(), "Attempted to "
                    + roleTransition.getRoleTransitionType().name() + " " + users.size() + " users of role: " + roleTransition.getRole().getAuthority()
                    + ". Expected " + minimumPermitted + " <= n <=" + maximumPermitted);
        }

        return users;
    }

    private List<User> getSpecifiedRoleTransitionUsers(Comment comment, RoleTransition roleTransition, User restrictedToUser) {
        List<User> transitionUsers = Lists.newArrayList();

        Role transitionRole = roleTransition.getTransitionRole();
        PrismRoleTransitionType roleTransitionType = roleTransition.getRoleTransitionType();

        for (CommentAssignedUser assignee : comment.getAssignedUsers()) {
            if (assignee.getRole() == transitionRole && assignee.getRoleTransitionType() == roleTransitionType) {
                User transitionUser = assignee.getUser();
                if (restrictedToUser == null || transitionUser.equals(restrictedToUser)) {
                    transitionUsers.add(transitionUser);
                } else {
                    actionService.throwWorkflowEngineException(comment.getResource(), comment.getAction(), "Attempted to "
                            + roleTransitionType.name().toLowerCase() + " user role: " + comment.getResource().getCode() + " " + transitionUser.toString()
                            + " in role " + transitionRole.getAuthority() + ". Transition is restricted to invoker: " + restrictedToUser.toString());
                }
            }
        }

        return transitionUsers;
    }

    private void executeRoleTransition(Comment comment, User user, RoleTransition roleTransition) throws DeduplicationException {
        DateTime baseline = new DateTime();

        Role role = roleTransition.getRole();
        Role transitionRole = roleTransition.getTransitionRole();

        Resource resource = resourceService.getOperativeResource(comment.getResource(), comment.getAction());
        Resource transitionResource = comment.getResource();

        UserRole transientRole = new UserRole().withResource(resource).withUser(user).withRole(role).withAssignedTimestamp(baseline);
        UserRole transientTransitionRole = new UserRole().withResource(transitionResource).withUser(user).withRole(transitionRole)
                .withAssignedTimestamp(baseline);

        PrismRoleTransitionType roleTransitionType = roleTransition.getRoleTransitionType();

        switch (roleTransitionType) {
        case BRANCH:
            executeBranchUserRole(transientRole, transientTransitionRole, comment);
            break;
        case CREATE:
            executeCreateUserRole(transientTransitionRole);
            break;
        case DELETE:
            executeRemoveUserRole(transientTransitionRole, comment);
            break;
        case RETIRE:
            executeRemoveUserRole(transientTransitionRole, comment);
            break;
        case UPDATE:
            executeUpdateUserRole(transientRole, transientTransitionRole, comment);
            break;
        }

    }

    private void executeBranchUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole == null) {
            actionService.throwWorkflowEngineException(comment.getResource(), comment.getAction(), "Found no role of type " + userRole.getRole().getAuthority()
                    + " for " + userRole.getResource().getCode() + " to branch for user " + userRole.getUser().toString());
        }
        comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), PrismRoleTransitionType.CREATE);
        getOrCreateUserRole(transitionUserRole);
    }

    private void executeCreateUserRole(UserRole userRole) throws DeduplicationException {
        getOrCreateUserRole(userRole);
    }

    private void executeRemoveUserRole(UserRole userRole, Comment comment) throws DeduplicationException {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole != null) {
            deleteUserRole(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole());
            comment.addAssignedUser(userRole.getUser(), userRole.getRole(), PrismRoleTransitionType.DELETE);
        }
    }

    private void executeUpdateUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole == null) {
            actionService.throwWorkflowEngineException(comment.getResource(), comment.getAction(), "Found no role of type " + userRole.getRole().getAuthority()
                    + " for " + userRole.getResource().getCode() + " to update for user " + userRole.getUser().toString());
        }
        comment.addAssignedUser(userRole.getUser(), userRole.getRole(), PrismRoleTransitionType.DELETE);
        comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), PrismRoleTransitionType.CREATE);
        deleteUserRole(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole());
        getOrCreateUserRole(transitionUserRole);
    }

    private void deleteUserRole(Resource resource, User user, Role role) {
        UserRole userRole = roleDAO.getUserRole(resource, user, role);
        validateUserRoleRemoval(resource, userRole.getRole());
        entityService.delete(userRole);
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

    private void throwWorkflowPermissionException(Resource resource, Action action, User user, Role role) {
        actionService.throwWorkflowPermissionException(resource, action, "Unable to assign role of type " + role.getAuthority() + " for " + resource.getCode()
                + " to " + user.toString() + " due to existing permission conflicts");
    }

}
