package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.UPDATE;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup;
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

    @Inject
    private RoleDAO roleDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private CustomizationService customizationService;

    @Inject
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
        UserRole userRole = entityService.getOrCreate(new UserRole().withResource(resource).withUser(user).withRole(newRole)
                .withAssignedTimestamp(new DateTime()));
        entityService.flush();
        return userRole;
    }

    public void assignUserRoles(Resource resource, User user, PrismRoleTransitionType transitionType, PrismRole... roles) throws Exception {
        if (roles.length > 0) {
            User invoker = userService.getCurrentUser();
            Action action = actionService.getViewEditAction(resource);

            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);

            Comment comment = new Comment().withAction(action).withUser(invoker)
                    .withContent(loader.load(PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE")))
                    .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());
            for (PrismRole role : roles) {
                comment.addAssignedUser(user, getById(role), transitionType);
            }

            actionService.executeUserAction(resource, action, comment);
            notificationService.sendInvitationNotifications(comment);
        }
    }

    public boolean hasUserRole(Resource resource, User user, PrismRoleGroup prismRoles) {
        return hasUserRole(resource, user, prismRoles.getRoles());
    }

    public boolean hasUserRole(Resource resource, User user, PrismRole... prismRoles) {
        for (PrismRole prismRole : prismRoles) {
            PrismScope roleScope = prismRole.getScope();
            if (roleScope.ordinal() <= resource.getResourceScope().ordinal()) {
                if (roleDAO.getUserRole(resource.getEnclosingResource(roleScope), user, prismRole) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<PrismRole> getRoles(User user) {
        return roleDAO.getRoles(user);
    }

    public List<PrismRole> getRolesOverridingRedactions(PrismScope resourceScope, User user) {
        return roleDAO.getRolesOverridingRedactions(resourceScope, user);
    }

    public List<PrismRole> getRolesOverridingRedactions(Resource resource, User user) {
        return roleDAO.getRolesOverridingRedactions(resource, user);
    }

    public List<PrismRole> getRolesForResource(Resource resource, User user) {
        return roleDAO.getRolesForResource(resource, user);
    }

    public List<PrismRole> getRolesWithinResource(Resource resource, User user) {
        return roleDAO.getRolesWithinResource(resource, user);
    }

    public List<User> getRoleUsers(Resource resource, Role... roles) {
        return resource == null ? Lists.<User> newArrayList() : roleDAO.getRoleUsers(resource, roles);
    }

    public List<User> getRoleUsers(Resource resource, PrismRole... prismRoles) {
        return resource == null ? Lists.<User> newArrayList() : roleDAO.getRoleUsers(resource, prismRoles);
    }

    public List<User> getRoleUsers(Resource resource, PrismRoleGroup prismRoleGroup) {
        return getRoleUsers(resource, prismRoleGroup.getRoles());
    }

    public List<PrismRole> getCreatableRoles(PrismScope scopeId) {
        return roleDAO.getCreatableRoles(scopeId);
    }

    public void deleteObseleteUserRoles() {
        roleDAO.deleteObseleteUserRoles();
    }

    public Role getCreatorRole(Resource resource) {
        return roleDAO.getCreatorRole(resource);
    }

    public void executeRoleTransitions(Resource resource, Comment comment, StateTransition stateTransition) throws DeduplicationException {
        for (PrismRoleTransitionType roleTransitionType : PrismRoleTransitionType.values()) {
            List<RoleTransition> roleTransitions = roleDAO.getRoleTransitions(stateTransition, roleTransitionType);
            executeRoleTransitions(resource, comment, roleTransitions);
        }
        entityService.flush();
    }

    public void deleteUserRoles(Resource resource, User user) throws Exception {
        List<PrismRole> roles = roleDAO.getRolesForResource(resource, user);
        assignUserRoles(resource, user, DELETE, roles.toArray(new PrismRole[roles.size()]));
    }

    public Integer getPermissionPrecedence(User user) {
        return roleDAO.getPermissionOrdinal(user);
    }

    public void executeApplicationProvideReferenceRoleTransition(Resource resource, Comment comment) {
        User user = comment.getDelegateUser();
        Role referee = getById(APPLICATION_REFEREE);
        UserRole userRole = new UserRole().withResource(resource).withUser(user).withRole(referee);
        Role viewer = getById(APPLICATION_VIEWER_REFEREE);
        UserRole transitionUserRole = new UserRole().withResource(resource).withUser(user).withRole(viewer);
        applicationContext.getBean(UPDATE.getResolver()).resolve(userRole, transitionUserRole, comment);
    }

    public void updateUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) {
        UserRole persistentRole = entityService.getDuplicateEntity(userRole);
        if (persistentRole != null) {
            comment.addAssignedUser(userRole.getUser(), userRole.getRole(), DELETE);
            comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), CREATE);
            deleteUserRole(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole());
            getOrCreateUserRole(transitionUserRole);
        }
    }

    public List<UserRole> getUserRolesByRoleCategory(User user, PrismRoleCategory prismRoleCategory, PrismScope... exludedPrismScopes) {
        return roleDAO.getUserRoleByRoleCategory(user, prismRoleCategory, exludedPrismScopes);
    }

    public void setCreatorRoles() {
        List<Role> creatorRoles = roleDAO.getCreatorRoles();
        for (Role creatorRole : creatorRoles) {
            creatorRole.setScopeCreator(true);
        }
    }

    public void deleteUserRole(Resource resource, User user, Role role) {
        UserRole userRole = roleDAO.getUserRole(resource, user, role);
        validateUserRoleRemoval(resource, userRole.getRole());
        entityService.delete(userRole);
        reassignResourceOwner(resource);
    }

    public List<PrismRole> getRolesByScope(PrismScope prismScope) {
        return roleDAO.getRolesByScopes(prismScope);
    }

    private void executeRoleTransitions(Resource resource, Comment comment, List<RoleTransition> roleTransitions) {
        for (RoleTransition roleTransition : roleTransitions) {
            List<User> users = getRoleTransitionUsers(resource, comment, roleTransition);
            for (User user : users) {
                executeRoleTransition(comment, user, roleTransition);
            }
        }
    }

    private List<User> getRoleTransitionUsers(Resource resource, Comment comment, RoleTransition roleTransition) throws WorkflowEngineException {
        User restrictedToUser = roleTransition.getRestrictToActionOwner() ? comment.getActionOwner() : null;

        List<User> users;
        if (roleTransition.getRoleTransitionType().isSpecified()) {
            users = getSpecifiedRoleTransitionUsers(comment, roleTransition, restrictedToUser);
        } else {
            users = roleDAO.getUnspecifiedRoleTransitionUsers(resource, roleTransition, restrictedToUser);
        }

        Integer minimumPermitted;
        Integer maximumPermitted;

        WorkflowPropertyDefinition definition = roleTransition.getWorkflowPropertyDefinition();
        if (definition == null) {
            minimumPermitted = roleTransition.getMinimumPermitted();
            maximumPermitted = roleTransition.getMaximumPermitted();
        } else {
            WorkflowPropertyConfiguration workflowPropertyConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                    PrismConfiguration.WORKFLOW_PROPERTY, definition, resource.getWorkflowPropertyConfigurationVersion());

            minimumPermitted = workflowPropertyConfiguration.getMinimum();
            maximumPermitted = workflowPropertyConfiguration.getMaximum();
        }

        if (!(minimumPermitted == null || users.size() >= minimumPermitted) && !(maximumPermitted == null || users.size() <= maximumPermitted)) {
            throw new WorkflowEngineException("Incorrect number of role assignments");
        }

        return users;
    }

    private List<User> getSpecifiedRoleTransitionUsers(Comment comment, RoleTransition roleTransition, User restrictedToUser) {
        List<User> transitionUsers = Lists.newArrayList();

        Role transitionRole = roleTransition.getTransitionRole();
        PrismRoleTransitionType roleTransitionType = roleTransition.getRoleTransitionType();

        for (CommentAssignedUser assignee : comment.getAssignedUsers()) {
            if (assignee.getRole().equals(transitionRole) && assignee.getRoleTransitionType() == roleTransitionType) {
                User transitionUser = assignee.getUser();
                if (restrictedToUser == null || transitionUser.equals(restrictedToUser)) {
                    transitionUsers.add(transitionUser);
                } else {
                    throw new WorkflowEngineException("Invalid role assignment");
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

        UserRole userRole = new UserRole().withResource(resource).withUser(user).withRole(role).withAssignedTimestamp(baseline);
        UserRole transitionUserRole = new UserRole().withResource(transitionResource).withUser(user).withRole(transitionRole)
                .withAssignedTimestamp(baseline);

        applicationContext.getBean(roleTransition.getRoleTransitionType().getResolver()).resolve(userRole, transitionUserRole, comment);
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
