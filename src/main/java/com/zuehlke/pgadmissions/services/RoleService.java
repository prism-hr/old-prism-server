package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.getUnverifiedRoles;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.Collection;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.exceptions.PrismForbiddenException;
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
    private ScopeService scopeService;

    @Inject
    private ApplicationContext applicationContext;

    public Role getById(PrismRole roleId) {
        return entityService.getByProperty(Role.class, "id", roleId);
    }

    public UserRole getUserRole(Resource resource, User user, Role role) {
        return roleDAO.getUserRole(resource, user, role);
    }

    public UserRole getUserRole(Resource resource, User user, PrismRole role) {
        return roleDAO.getUserRole(resource, user, role);
    }

    public List<Role> getRoles() {
        return entityService.list(Role.class);
    }

    public UserRole getOrCreateUserRole(UserRole transientUserRole) {
        return entityService.getOrCreate(transientUserRole.withAssignedTimestamp(new DateTime()));
    }

    public void updateUserRoles(User invoker, Resource resource, User user, PrismRoleTransitionType transitionType, PrismRole... roles) {
        Action action = actionService.getViewEditAction(resource);
        if (action != null) {
            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);

            Comment comment = new Comment().withAction(action).withUser(invoker)
                    .withContent(loader.loadLazy(PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE")))
                    .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());
            for (PrismRole role : roles) {
                comment.addAssignedUser(user, getById(role), transitionType);
            }

            actionService.executeUserAction(resource, action, comment);
            notificationService.sendInvitationNotifications(comment);
        }
    }

    public void verifyUserRoles(User invoker, Resource resource, User user, Boolean verify) {
        getUnverifiedRoles(resource.getResourceScope()).forEach(r -> {
            UserRole userRole = getUserRole(resource, user, getById(r));
            if (userRole != null) {
                if (isTrue(verify)) {
                    updateUserRoles(invoker, resource, user, CREATE, PrismRole.valueOf(r.name().replace("_UNVERIFIED", "")));
                } else {
                    Action action = actionService.getViewEditAction(resource);
                    if (!(action == null || !actionService.checkActionExecutable(resource, action, user, false))) {
                        entityService.delete(userRole);
                    }
                }
            }
        });
    }

    public void setResourceOwner(Resource resource, User user) {
        if (roleDAO.getRolesForResourceStrict(resource, user).isEmpty()) {
            throw new PrismForbiddenException("User has no role within given resource");
        }

        resource.setUser(user);
        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            resource.getAdvert().setUser(user);
        }

        resourceService.executeUpdate(resource, userService.getCurrentUser(),
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE"));
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

    public List<PrismRole> getRolesOverridingRedactions(Resource resource) {
        User user = userService.getCurrentUser();
        PrismScope resourceScope = resource.getResourceScope();
        List<Integer> resourceIds = Lists.newArrayList(resource.getId());
        return getRolesOverridingRedactions(user, resourceScope, resourceIds);
    }

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope resourceScope, Collection<Integer> resourceIds) {
        return roleDAO.getRolesOverridingRedactions(user, resourceScope, resourceIds, scopeService.getParentScopesDescending(resourceScope, SYSTEM));
    }

    public List<PrismRole> getRolesForResource(Resource resource, User user) {
        return roleDAO.getRolesForResource(resource, user);
    }

    public List<PrismRole> getRolesForResourceStrict(Resource resource, User user) {
        return roleDAO.getRolesForResourceStrict(resource, user);
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

    public void deleteObsoleteUserRoles() {
        roleDAO.deleteObsoleteUserRoles();
    }

    public Role getCreatorRole(Resource resource) {
        return roleDAO.getCreatorRole(resource);
    }

    public void executeRoleTransitions(Resource resource, Comment comment, StateTransition stateTransition) {
        for (PrismRoleTransitionType roleTransitionType : PrismRoleTransitionType.values()) {
            List<RoleTransition> roleTransitions = roleDAO.getRoleTransitions(stateTransition, roleTransitionType);
            executeRoleTransitions(resource, comment, roleTransitions);
        }
        entityService.flush();
    }

    public void deleteUserRoles(User invoker, Resource resource, User user) {
        List<PrismRole> roles = roleDAO.getRolesForResourceStrict(resource, user);
        updateUserRoles(invoker, resource, user, DELETE, roles.toArray(new PrismRole[roles.size()]));
    }

    public PrismScope getPermissionScope(User user) {
        PrismScope permissionScope = roleDAO.getPermissionScope(user);
        int permissionScopeOrdinal = permissionScope.ordinal();
        for (PrismScope permissionScopePartner : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
            PrismScope partnerScope = roleDAO.getPermissionScopePartner(user, permissionScopePartner);
            if (partnerScope != null && partnerScope.ordinal() < permissionScopeOrdinal) {
                return permissionScopePartner;
            }
        }
        return permissionScope;
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

    public List<UserRole> getUserRolesByRoleCategory(User user, PrismRoleCategory prismRoleCategory, PrismScope... excludedPrismScopes) {
        return roleDAO.getUserRoleByRoleCategory(user, prismRoleCategory, excludedPrismScopes);
    }

    public void setCreatorRoles() {
        List<Role> creatorRoles = roleDAO.getCreatorRoles();
        for (Role creatorRole : creatorRoles) {
            creatorRole.setScopeCreator(true);
        }
    }

    public void deleteUserRole(Resource resource, User user, Role role) {
        if (roleDAO.getRolesForResourceStrict(resource, user).size() < 2 && resource.getUser().getId().equals(user.getId())) {
            throw new PrismForbiddenException("Cannot remove the owner");
        }

        UserRole userRole = getUserRole(resource, user, role);
        entityService.delete(userRole);
        entityService.flush();
    }

    public List<PrismRole> getRolesByScope(PrismScope prismScope) {
        return roleDAO.getRolesByScope(prismScope);
    }

    public List<PrismRole> getRolesByScope(User user, PrismScope prismScope) {
        return roleDAO.getRolesByScope(user, prismScope);
    }

    public List<PrismRole> getRolesWithRedactions(PrismScope resourceScope) {
        return roleDAO.getRolesWithRedactions(resourceScope);
    }

    public void setVerifiedRoles() {
        List<PrismRole> roles = roleDAO.getVerifiedRoles();
        roleDAO.setVerifiedRoles(roles);
    }

    public List<PrismRole> getVerifiedRoles(User user, ResourceParent resource) {
        return roleDAO.getVerifiedRoles(user, resource);
    }

    private void executeRoleTransitions(Resource resource, Comment comment, List<RoleTransition> roleTransitions) {
        for (RoleTransition roleTransition : roleTransitions) {
            List<User> users = getRoleTransitionUsers(resource, comment, roleTransition);
            for (User user : users) {
                executeRoleTransition(comment, user, roleTransition);
            }
        }
    }

    private List<User> getRoleTransitionUsers(Resource resource, Comment comment, RoleTransition roleTransition) {
        User restrictedToUser = roleTransition.getRestrictToActionOwner() ? comment.getActionOwner() : null;

        List<User> users;
        if (roleTransition.getRoleTransitionType().isSpecified()) {
            users = getSpecifiedRoleTransitionUsers(comment, roleTransition, restrictedToUser);
        } else {
            users = roleDAO.getUnspecifiedRoleTransitionUsers(resource, roleTransition, restrictedToUser);
        }

        Integer minimumPermitted = roleTransition.getMinimumPermitted();
        Integer maximumPermitted = roleTransition.getMaximumPermitted();

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

    private void executeRoleTransition(Comment comment, User user, RoleTransition roleTransition) {
        DateTime baseline = new DateTime();

        Role role = roleTransition.getRole();
        Role transitionRole = roleTransition.getTransitionRole();

        Resource resource = resourceService.getOperativeResource(comment.getResource(), comment.getAction());
        Resource commentResource = comment.getResource();
        Resource transitionResource = commentResource.getEnclosingResource(transitionRole.getScope().getId());

        if (transitionResource != null) {
            UserRole userRole = new UserRole().withResource(resource).withUser(user).withRole(role).withAssignedTimestamp(baseline);
            UserRole transitionUserRole = new UserRole().withResource(transitionResource).withUser(user).withRole(transitionRole)
                    .withAssignedTimestamp(baseline);

            applicationContext.getBean(roleTransition.getRoleTransitionType().getResolver()).resolve(userRole, transitionUserRole, comment);
        }
    }

}
