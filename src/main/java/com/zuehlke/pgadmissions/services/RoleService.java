package com.zuehlke.pgadmissions.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.domain.ResourceRepresentation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
    private UserService userService;

    public Role getById(PrismRole roleId) {
        return roleDAO.getById(roleId);
    }

    public List<Role> getRoles() {
        return entityService.getAll(Role.class);
    }

    public UserRole createUserRole(Resource resource, User user, PrismRole roleToCreate) {
        Role role = getById(roleToCreate);
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
            deleteUserRole(roleToRemove);
        }
    }

    public void saveUserRole(UserRole userRole) {
        entityService.save(userRole);
    }

    public void deleteUserRole(UserRole userRole) {
        for (UserNotificationIndividual pendingNotification : userRole.getPendingNotifications()) {
            entityService.delete(pendingNotification);
        }
        entityService.delete(userRole);
    }

    public void executeUserRoleTransitions(Resource resource, StateTransition stateTransition, Comment comment) {
        HashMap<User, RoleTransition> userRoleTransitions = Maps.newHashMap();
        userRoleTransitions.putAll(getUserRoleUpdateTransitions(stateTransition, resource, comment.getUser()));
        userRoleTransitions.putAll(getUserCreationRoleTransitions(stateTransition, resource, comment.getUser(), comment));

        for (User user : userRoleTransitions.keySet()) {
            executeRoleTransition(resource, user, userRoleTransitions.get(user));
        }
    }

    public void executeRoleTransition(Resource resource, User user, RoleTransition roleTransition) {
        switch (roleTransition.getRoleTransitionType()) {
            case BRANCH:
            case CREATE:
                saveUserRole(getOrCreateUserRole(resource, user, roleTransition.getTransitionRole().getId()));
                break;
            case REJOIN:
                saveUserRole(getOrCreateUserRole(resource, user, roleTransition.getTransitionRole().getId()));
                deleteUserRole(getOrCreateUserRole(resource, user, roleTransition.getRole().getId()));
                break;
            case UPDATE:
                UserRole userRole = getOrCreateUserRole(resource, user, roleTransition.getRole().getId());
                userRole.setRole(roleTransition.getTransitionRole());
                saveUserRole(userRole);
                break;
        }
    }

    @Deprecated
    public boolean hasRole(User user, PrismRole authority) {
        return hasRole(user, authority, null);
    }

    @Deprecated
    public boolean hasRole(User user, PrismRole authority, Resource scope) {
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

    public List<Role> getActionRoles(Resource resource, Action action) {
        return roleDAO.getActionRoles(resource, action);
    }

    public List<Role> getActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    public List<Role> getDelegateActionOwnerRoles(User user, Resource resource, Action action) {
        return roleDAO.getActionOwnerRoles(user, resource, action);
    }

    private HashMap<User, RoleTransition> getUserRoleUpdateTransitions(StateTransition stateTransition, Resource resource, User actionOwner) {
        HashMap<User, RoleTransition> userRoleTransitions = Maps.newHashMap();

        HashMultimap<RoleTransition, User> roleTransitionUsers = roleDAO.getRoleTransitionUsers(stateTransition, resource, actionOwner);
        for (RoleTransition roleTransition : roleTransitionUsers.keySet()) {
            for (User user : roleTransitionUsers.get(roleTransition)) {
                validateUserRoleTransition(resource, userRoleTransitions, roleTransition, user);
            }
        }

        return userRoleTransitions;

    }

    public Role getResourceCreatorRole(ResourceDynamic resource, Action createAction) {
        return (Role) roleDAO.getResourceCreatorRole(resource, createAction);
    }

    private HashMap<User, RoleTransition> getUserCreationRoleTransitions(StateTransition stateTransition, Resource resource, User actionOwner, Comment comment) {
        HashMap<User, RoleTransition> userRoleTransitions = Maps.newHashMap();

        HashMultimap<Role, RoleTransition> roleCreationTransitions = roleDAO.getRoleCreationTransitions(stateTransition);
        for (Role role : roleCreationTransitions.keySet()) {
            for (RoleTransition roleTransition : roleCreationTransitions.get(role)) {
                User restrictedToUser = null;
                if (roleTransition.isRestrictToActionOwner()) {
                    restrictedToUser = actionOwner;
                }

                List<User> users = commentService.getAssignedUsers(comment, role, restrictedToUser);

                Integer minimumPermitted = roleTransition.getMinimumPermitted();
                Integer maximumPermitted = roleTransition.getMinimumPermitted();

                if ((minimumPermitted == null || users.size() >= minimumPermitted) && (maximumPermitted == null || users.size() <= maximumPermitted)) {
                    for (User user : users) {
                        validateUserRoleTransition(resource, userRoleTransitions, roleTransition, user);
                    }
                }
                // TODO : custom workflow error
                throw new Error("Attempted to process an invalid role creation transition");
            }
        }

        return userRoleTransitions;
    }

    private void validateUserRoleTransition(Resource resource, HashMap<User, RoleTransition> userRoleTransitions, RoleTransition roleTransition, User user) {
        if (roleDAO.getExcludingUserRole(user, resource, roleTransition.getRole().getExcludedRoles()).isEmpty()) {
            userRoleTransitions.put(user, roleTransition);
        } else {
            // TODO : custom workflow error
            throw new Error("Attempted to process a conflicted role creation transition");
        }
    }

    public List<User> getUsers(Resource resource) {
        return roleDAO.getUsers(resource);
    }

    public List<PrismRole> getRoles(Resource resource, User user) {
        return roleDAO.getRoles(resource, user);
    }

    public void updateRoles(ResourceDynamic resource, User user, List<ResourceRepresentation.RoleRepresentation> roles) {
        for (ResourceRepresentation.RoleRepresentation role : roles) {
            if (role.getValue()) {
                getOrCreateUserRole(resource, user, role.getId());
            } else {
                removeUserRoles(resource, user, role.getId());
            }
        }
    }

    public List<PrismRole> getRolesToRemove(Class<? extends Resource> resourceClass, PrismRole... rolesToCreate) {
        return roleDAO.getRolesToRemove(resourceClass);
    }
}
