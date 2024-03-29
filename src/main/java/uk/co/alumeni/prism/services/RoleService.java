package uk.co.alumeni.prism.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import uk.co.alumeni.prism.dto.UserRoleDTO;
import uk.co.alumeni.prism.exceptions.PrismForbiddenException;
import uk.co.alumeni.prism.exceptions.WorkflowEngineException;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

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
    private EntityService entityService;

    @Inject
    private InvitationService invitationService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private UserActivityCacheService userActivityCacheService;

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

    public UserRole getUserRoleStrict(Resource resource, User user, Role role) {
        return roleDAO.getUserRoleStrict(resource, user, role);
    }

    public HashMultimap<UserRoleDTO, PrismRole> getUserRolesStrict(Resource resource, PrismRole searchRole, String searchTerm, boolean directlyAssignableOnly) {
        HashMultimap<UserRoleDTO, PrismRole> userRoles = HashMultimap.create();
        roleDAO.getUserRolesStrict(resource, searchRole, searchTerm, directlyAssignableOnly).stream().forEach(userRole -> {
            userRoles.put(userRole, userRole.getRole());
        });
        return userRoles;
    }

    public UserRole getOrCreateUserRole(UserRole userRole) {
        Resource resource = userRole.getResource();
        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            userRole.setAdvert(resource.getAdvert());
        }

        userRole.setAssignedTimestamp(now());
        return entityService.getOrCreate(userRole);
    }

    public void acceptUnnacceptedUserRoles(User user, DateTime baseline) {
        roleDAO.getUnacceptedRolesForUser(user).forEach(userRole -> {
            userRole.setAcceptedTimestamp(baseline);
            activityService.setSequenceIdentifier(userRole, baseline);
        });
    }

    public void deleteUserRole(Resource resource, User user, Role role) {
        if (getRolesForResource(resource, user).size() < 2 && resource.getUser().equals(user)) {
            throw new PrismForbiddenException("Cannot remove the owner");
        }

        UserRole userRole = getUserRoleStrict(resource, user, role);
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
        List<PrismRole> roles = getRolesForResource(resource, user);
        deleteUserRoles(invoker, resource, user, roles.toArray(new PrismRole[roles.size()]));
    }

    public void verifyUserRoles(User currentUser, ResourceParent resource, User user, Boolean verify) {
        DateTime baseline = now();
        Action action = actionService.getViewEditAction(resource);

        if (!(action == null || !actionService.checkActionExecutable(resource, action, currentUser))) {
            boolean isVerify = isTrue(verify);
            for (UserRole userRole : roleDAO.getUnverifiedRoles(resource, user)) {
                if (isVerify) {
                    createUserRoles(currentUser, resource, user, PrismRole.valueOf(userRole.getRole().getId().name().replace("_UNVERIFIED", "")));
                    if (isTrue(userRole.getRequested())) {
                        notificationService.sendJoinNotification(currentUser, user, resource);
                    } else {
                        userRole.setInvitation(invitationService.createInvitation(user));
                    }
                } else {
                    getOrCreateUserRole(new UserRole().withResource(userRole.getResource()).withUser(userRole.getUser())
                            .withRole(getById(PrismRole.valueOf(userRole.getRole().getId().name().replace("_UNVERIFIED", "_REJECTED"))))
                            .withAssignedTimestamp(baseline));
                }
                entityService.delete(userRole);
            }
        }

        userActivityCacheService.updateUserActivityCaches(resource, currentUser, baseline);
    }

    public Map<PrismScope, PrismRoleCategory> getDefaultRoleCategories(User user) {
        Map<PrismScope, PrismRoleCategory> defaults = newTreeMap();
        for (PrismScope scope : PrismScope.values()) {
            PrismRole defaultRole = roleDAO.getDefaultRoleCategories(scope, user);
            if (defaultRole != null) {
                defaults.put(scope, defaultRole.getRoleCategory());
            }
        }
        return defaults;
    }

    public boolean hasUserRole(Resource resource, User user, PrismRoleGroup prismRoles) {
        return hasUserRole(resource, user, prismRoles.getRoles());
    }

    public boolean hasUserRole(Resource resource, User user, PrismRole... prismRoles) {
        return isNotEmpty(prismRoles) ? isNotEmpty(roleDAO.getUserRoles(resource, user, asList(prismRoles))) : false;
    }

    public boolean createsUserRole(Comment comment, User user, PrismRole... prismRoles) {
        for (CommentAssignedUser commentAssignedUser : comment.getAssignedUsers()) {
            if (commentAssignedUser.getRoleTransitionType().equals(CREATE) && user.equals(commentAssignedUser.getUser())
                    && contains(prismRoles, commentAssignedUser.getRole().getId())) {
                return true;
            }
        }
        return false;
    }

    public List<PrismRole> getRolesOverridingRedactions(Resource resource, User currentUser) {
        PrismScope resourceScope = resource.getResourceScope();
        List<Integer> resourceIds = newArrayList(resource.getId());
        return getRolesOverridingRedactions(currentUser, resourceScope, resourceIds);
    }

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, Collection<Integer> resourceIds) {
        Set<PrismRole> roles = Sets.newHashSet();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        roles.addAll(roleDAO.getRolesOverridingRedactions(user, scope, resourceIds));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                roles.addAll(roleDAO.getRolesOverridingRedactions(user, scope, parentScope, resourceIds));
            }

            for (PrismScope targeterScope : organizationScopes) {
                for (PrismScope targetScope : organizationScopes) {
                    roles.addAll(roleDAO.getRolesOverridingRedactions(user, scope, targeterScope, targetScope, resourceIds));
                }
            }
        }

        return newArrayList(roles);
    }

    public List<PrismRole> getRolesForResource(Resource resource, User user) {
        return roleDAO.getRolesForResource(resource, user);
    }

    public List<PrismRole> getUserRoles(Resource resource, User user) {
        return roleDAO.getUserRoles(resource, user).stream().map(userRole -> userRole.getRole()).collect(toList());
    }

    public List<UserRoleDTO> getUserRoles(Resource resource, List<PrismRole> roles) {
        return roleDAO.getUserRoles(resource, roles);
    }

    public List<UserRoleDTO> getUserRoles(Collection<Resource> resources, List<PrismRole> roles) {
        return roleDAO.getUserRoles(resources, roles);
    }

    public List<PrismRole> getCreatableRoles(PrismScope prismScope) {
        return roleDAO.getCreatableRoles(prismScope);
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

    public List<UserRole> getUserRolesForWhichUserIsCandidate(User user) {
        return roleDAO.getUserRolesForWhichUserIsCandidate(user);
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

    private void updateUserRoles(User invoker, Resource resource, User user, PrismRoleTransitionType transitionType, String message, boolean notify,
            PrismRole... roles) {
        updateUserRoles(invoker, resource, newHashSet(user), transitionType, message, notify, roles);
    }

    private void updateUserRoles(User invoker, Resource resource, Set<User> users, PrismRoleTransitionType transitionType, String message, boolean notify,
            PrismRole... roles) {
        Action action = actionService.getViewEditAction(resource);
        if (action != null) {
            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);

            Comment comment = new Comment().withAction(action).withUser(invoker)
                    .withContent(loader.loadLazy(PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE")))
                    .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());

            User owner = resource.getUser();
            HashMultimap<UserRoleDTO, PrismRole> existingUserRoles = getUserRolesStrict(resource);
            users.forEach(user -> {
                boolean delete = transitionType.equals(DELETE);
                boolean deleteOwnerRole = delete && user.equals(owner);
                stream(roles).forEach(role -> {
                    if (!(deleteOwnerRole && role.getRoleCategory().equals(ADMINISTRATOR))) {
                        Set<PrismRole> existingRoles = existingUserRoles.get(new UserRoleDTO().withId(user.getId()).withEmail(user.getEmail()));
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
                    UserRole userRole = getUserRoleStrict(resource, assignee.getUser(), assignee.getRole());
                    userRole.setInvitation(invitation);
                });
            }
        }
    }

    private HashMultimap<UserRoleDTO, PrismRole> getUserRolesStrict(Resource resource) {
        return getUserRolesStrict(resource, null, null, false);
    }

}
