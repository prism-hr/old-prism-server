package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.RoleDAO;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.RoleTransition;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.dto.ResourceRoleDTO;
import uk.co.alumeni.prism.exceptions.PrismForbiddenException;
import uk.co.alumeni.prism.exceptions.WorkflowEngineException;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Service
@Transactional
public class RoleService {

    @Inject
    private RoleDAO roleDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private ActivityService activityService;

    @Inject
    private AdvertService advertService;

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
    private InvitationService invitationService;

    @Inject
    private ApplicationContext applicationContext;

    public Role getById(PrismRole roleId) {
        return entityService.getById(Role.class, roleId);
    }

    public UserRole getUserRoleById(Integer userRoleId) {
        return entityService.getById(UserRole.class, userRoleId);
    }

    public List<Role> getRoles() {
        return entityService.getAll(Role.class);
    }

    public UserRole getUserRole(Resource resource, User user, Role role) {
        return roleDAO.getUserRole(resource, user, role);
    }

    public List<ResourceRoleDTO> getUserRoles(User user) {
        List<ResourceRoleDTO> userRoles = Lists.newArrayList();
        for (PrismScope resourceScope : PrismScope.values()) {
            userRoles.addAll(roleDAO.getUserRoles(user, resourceScope));
        }
        return userRoles;
    }

    public HashMultimap<User, PrismRole> getUserRoles(Resource resource) {
        HashMultimap<User, PrismRole> userRoles = HashMultimap.create();
        roleDAO.getUserRoles(resource).forEach(userRole -> {
            userRoles.put(userRole.getUser(), userRole.getRole());
        });
        return userRoles;
    }

    public UserRole getOrCreateUserRole(UserRole transientUserRole) {
        return entityService.getOrCreate(transientUserRole.withAssignedTimestamp(new DateTime()));
    }

    public void acceptUnnacceptedUserRoles(User user, DateTime baseline) {
        roleDAO.getUnnacceptedRolesForUser(user).forEach(userRole -> {
            userRole.setAcceptedTimestamp(baseline);
            activityService.setSequenceIdentifier(userRole, baseline);
        });
    }

    public void deleteUserRole(Resource resource, User user, Role role) {
        if (roleDAO.getRolesForResourceStrict(resource, user).size() < 2 && resource.getUser().getId().equals(user.getId())) {
            throw new PrismForbiddenException("Cannot remove the owner");
        }

        UserRole userRole = getUserRole(resource, user, role);
        entityService.delete(userRole);
        entityService.flush();
    }

    public void createUserRoles(User invoker, Resource resource, User user, String message, PrismRole... roles) {
        updateUserRoles(invoker, resource, user, CREATE, message, true, roles);
    }

    public void createUserRoles(User invoker, Resource resource, Set<User> users, String message, PrismRole... roles) {
        updateUserRoles(invoker, resource, users, CREATE, message, true, roles);
    }

    public void createUserRoles(User invoker, Resource resource, User user, PrismRole... roles) {
        updateUserRoles(invoker, resource, user, CREATE, null, false, roles);
    }

    public void deleteUserRoles(User invoker, Resource resource, User user, PrismRole... roles) {
        updateUserRoles(invoker, resource, user, DELETE, null, false, roles);
    }

    public void deleteUserRoles(User invoker, Resource resource, User user) {
        List<PrismRole> roles = roleDAO.getRolesForResourceStrict(resource, user);
        deleteUserRoles(invoker, resource, user, roles.toArray(new PrismRole[roles.size()]));
    }

    public void verifyUserRoles(User invoker, ResourceParent resource, User user, Boolean verify) {
        Action action = actionService.getViewEditAction(resource);
        if (!(action == null || !actionService.checkActionExecutable(resource, action, invoker))) {
            boolean isVerify = isTrue(verify);
            for (UserRole userRole : roleDAO.getUnverifiedRoles(resource, user)) {
                if (isVerify) {
                    createUserRoles(invoker, resource, user, PrismRole.valueOf(userRole.getRole().getId().name().replace("_UNVERIFIED", "")));
                    if (isTrue(userRole.getRequested())) {
                        notificationService.sendJoinNotification(invoker, user, resource);
                    } else {
                        userRole.setInvitation(invitationService.createInvitation(user));
                    }
                } else {
                    getOrCreateUserRole(new UserRole().withResource(userRole.getResource()).withUser(userRole.getUser())
                            .withRole(getById(PrismRole.valueOf(userRole.getRole().getId().name().replace("_UNVERIFIED", "_REJECTED")))).withAssignedTimestamp(now()));
                }
                entityService.delete(userRole);
            }
        }
    }

    public Map<PrismScope, PrismRoleCategory> getDefaultRoleCategories(User user) {
        Map<PrismScope, PrismRoleCategory> defaults = Maps.newTreeMap();
        for (PrismScope scope : PrismScope.values()) {
            PrismRole defaultRole = roleDAO.getDefaultRoleCategories(scope, user);
            if (defaultRole != null) {
                defaults.put(scope, defaultRole.getRoleCategory());
            }
        }
        return defaults;
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
        List<Integer> resourceIds = newArrayList(resource.getId());
        return getRolesOverridingRedactions(user, resourceScope, resourceIds);
    }

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, Collection<Integer> resourceIds) {
        Set<PrismRole> roles = Sets.newHashSet();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        roles.addAll(roleDAO.getRolesOverridingRedactions(user, scope, resourceIds));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                roles.addAll(roleDAO.getRolesOverridingRedactions(user, scope, parentScope, resourceIds));
            }

            List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(scope);
            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : organizationScopes) {
                    for (PrismScope targetScope : organizationScopes) {
                        roles.addAll(roleDAO.getRolesOverridingRedactions(user, scope, targeterScope, targetScope, targeterEntities, resourceIds));
                    }
                }
            }
        }

        return newArrayList(roles);
    }

    public List<PrismRole> getRolesForResource(Resource resource, User user) {
        return roleDAO.getRolesForResource(resource, user);
    }

    public List<PrismRole> getRolesForResourceStrict(Resource resource, User user) {
        return roleDAO.getRolesForResourceStrict(resource, user);
    }

    public List<User> getRoleUsers(Resource resource, PrismRole... prismRoles) {
        return resource == null ? Lists.<User> newArrayList() : roleDAO.getRoleUsers(resource, prismRoles);
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

    public void updateUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) {
        UserRole persistentUserRole = entityService.getDuplicateEntity(userRole);
        if (persistentUserRole != null) {
            notificationService.resetUserNotifications(persistentUserRole);
            comment.addAssignedUser(persistentUserRole.getUser(), persistentUserRole.getRole(), DELETE);
            comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), CREATE);
            deleteUserRole(persistentUserRole.getResource(), persistentUserRole.getUser(), persistentUserRole.getRole());
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

    private void updateUserRoles(User invoker, Resource resource, User user, PrismRoleTransitionType transitionType, String message, boolean notify, PrismRole... roles) {
        updateUserRoles(invoker, resource, newHashSet(user), transitionType, message, notify, roles);
    }

    private void updateUserRoles(User invoker, Resource resource, Set<User> users, PrismRoleTransitionType transitionType, String message, boolean notify, PrismRole... roles) {
        Action action = actionService.getViewEditAction(resource);
        if (action != null) {
            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);

            Comment comment = new Comment().withAction(action).withUser(invoker)
                    .withContent(loader.loadLazy(PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE")))
                    .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());

            User owner = resource.getUser();
            HashMultimap<User, PrismRole> existingUserRoles = getUserRoles(resource);
            users.forEach(user -> {
                boolean delete = transitionType.equals(DELETE);
                boolean deleteOwnerRole = delete && user.equals(owner);
                stream(roles).forEach(role -> {
                    if (!(deleteOwnerRole && role.getRoleCategory().equals(ADMINISTRATOR))) {
                        Set<PrismRole> existingRoles = existingUserRoles.get(user);
                        if (existingRoles == null || delete || !existingRoles.contains(role)) {
                            comment.addAssignedUser(user, getById(role), transitionType);
                        }
                    }
                });
            });

            actionService.executeUserAction(resource, action, comment);

            if (notify && transitionType.equals(CREATE)) {
                Invitation invitation = invitationService.createInvitation(invoker, message);
                comment.getAssignedUsers().forEach(assignee -> {
                    UserRole userRole = getUserRole(resource, assignee.getUser(), assignee.getRole());
                    userRole.setInvitation(invitation);
                });
            }
        }
    }

}
