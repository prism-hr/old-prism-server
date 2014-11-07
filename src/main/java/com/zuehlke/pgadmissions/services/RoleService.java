package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

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

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ApplicationContext applicationContext;

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
        List<Integer> excludingUserRoles = roleDAO.getExcludingUserRoles(resource, user, newRole);
        if (excludingUserRoles.isEmpty()) {
            UserRole transientUserRole = new UserRole().withResource(resource).withUser(user).withRole(newRole)
                    .withActivated(resource.getResourceScope() == PrismScope.APPLICATION ? false : true).withAssignedTimestamp(new DateTime());
            UserRole persistentUserRole = entityService.getOrCreate(transientUserRole);
            entityService.flush();
            return persistentUserRole;
        }
        throwWorkflowPermissionException(resource, actionService.getViewEditAction(resource), user, newRole);
        return null;
    }

    public void updateUserRole(Resource resource, User user, PrismRoleTransitionType transitionType, PrismRole... roles) throws DeduplicationException {
        if (roles.length > 0) {
            User invoker = userService.getCurrentUser();
            Action action = actionService.getViewEditAction(resource);

            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource, invoker);

            Comment comment = new Comment().withAction(action).withUser(invoker)
                    .withContent(loader.load(PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE")))
                    .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());
            for (PrismRole role : roles) {
                comment.addAssignedUser(user, getById(role), transitionType);
            }

            actionService.executeUserAction(resource, action, comment);
            notificationService.sendInvitationNotifications(comment);
        }
    }

    public List<PrismRole> getActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    public boolean hasUserRole(Resource resource, User user, PrismRole roleId) {
        Role role = getById(roleId);
        return roleDAO.getUserRole(resource, user, role) != null;
    }

    public boolean hasAnyUserRole(Resource resource, User user, PrismRole... roleIds) {
        for (PrismRole roleId : roleIds) {
            if (hasUserRole(resource, user, roleId)) {
                return true;
            }
        }
        return false;
    }

    public List<PrismRole> getRoles(Resource resource, User user) {
        return roleDAO.getRoles(resource, user);
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

    public List<Role> getActiveRoles() {
        return roleDAO.getActiveRoles();
    }

    public void deleteInactiveRoles() {
        roleDAO.deleteObseleteUserRoles(getActiveRoles());
    }

    public Role getCreatorRole(Resource resource) {
        return roleDAO.getCreatorRole(resource);
    }

    public void deleteExcludedRoles() {
        for (Role role : entityService.list(Role.class)) {
            role.getExcludedRoles().clear();
        }
    }

    public void executeRoleTransitions(StateTransition stateTransition, Comment comment) throws DeduplicationException {
        for (PrismRoleTransitionType roleTransitionType : PrismRoleTransitionType.values()) {
            for (RoleTransition roleTransition : roleDAO.getRoleTransitions(stateTransition, roleTransitionType)) {
                List<User> users = getRoleTransitionUsers(comment, roleTransition);
                for (User user : users) {
                    executeRoleTransition(comment, user, roleTransition);
                }
            }
        }
    }

    public void deleteUserRoles(Resource resource, User user) throws DeduplicationException {
        List<PrismRole> roles = roleDAO.getUserRoles(resource, user);
        updateUserRole(resource, user, DELETE, roles.toArray(new PrismRole[roles.size()]));
    }

    private List<User> getRoleTransitionUsers(Comment comment, RoleTransition roleTransition) throws WorkflowEngineException {
        User actionOwner = comment.getUser();
        Resource resource = comment.getResource();
        User restrictedToUser = roleTransition.getRestrictToActionOwner() ? actionOwner : null;

        List<User> users;
        if (roleTransition.getRoleTransitionType().isSpecified()) {
            users = getSpecifiedRoleTransitionUsers(comment, roleTransition, restrictedToUser);
        } else {
            users = roleDAO.getUnspecifiedRoleTransitionUsers(resource, roleTransition, restrictedToUser);
        }

        Integer minimumPermitted;
        Integer maximumPermitted;

        WorkflowPropertyDefinition workflowPropertyDefinition = roleTransition.getWorkflowPropertyDefinition();
        if (workflowPropertyDefinition == null) {
            minimumPermitted = roleTransition.getMinimumPermitted();
            maximumPermitted = roleTransition.getMaximumPermitted();
        } else {
            WorkflowPropertyConfiguration workflowPropertyConfiguration = workflowService.getWorkflowPropertyConfiguration(resource, actionOwner,
                    workflowPropertyDefinition);
            if (workflowPropertyConfiguration.hasRangeSpecification()) {
                minimumPermitted = workflowPropertyConfiguration.getMinimum();
                maximumPermitted = workflowPropertyConfiguration.getMaximum();
            } else {
                minimumPermitted = roleTransition.getMinimumPermitted();
                maximumPermitted = roleTransition.getMaximumPermitted();
            }
        }

        if (!(minimumPermitted == null || users.size() >= minimumPermitted) && !(maximumPermitted == null || users.size() <= maximumPermitted)) {
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
