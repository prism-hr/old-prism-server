package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.ResourceDynamic;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityService entityService;

    public Role getById(PrismRole authority) {
        return roleDAO.getById(authority);
    }
    
    public List<Role> getRoles() {
        return entityService.getAll(Role.class);
    }

    public UserRole createUserRole(Resource resource, User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setResource(resource);
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedTimestamp(new DateTime());
        return userRole;
    }

    public UserRole getOrCreateUserRole(Resource resource, User user, Role role) {
        UserRole transientUserRole = createUserRole(resource, user, role);
        return entityService.getOrCreate(transientUserRole);
    }

    public void removeUserRoles(User user, Resource resource, PrismRole... authorities) {
        for (UserRole roleToRemove : roleDAO.getUserRoles(resource, user, authorities)) {
            entityService.delete(roleToRemove);
        }
    }

    public void saveUserRole(UserRole userRole) {
        entityService.save(userRole);
    }

    public void deleteUserRole(UserRole userRole) {
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
            saveUserRole(getOrCreateUserRole(resource, user, roleTransition.getTransitionRole()));
            break;
        case REJOIN:
            saveUserRole(getOrCreateUserRole(resource, user, roleTransition.getTransitionRole()));
            deleteUserRole(getOrCreateUserRole(resource, user, roleTransition.getRole()));
            break;
        case UPDATE:
            UserRole userRole = getOrCreateUserRole(resource, user, roleTransition.getRole());
            userRole.setRole(roleTransition.getTransitionRole());
            saveUserRole(userRole);
            break;
        }
    }

    public boolean hasAnyRole(User user, PrismRole... authorities) {
        for (PrismRole authority : authorities) {
            if (hasRole(user, authority, null)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRole(User user, PrismRole authority) {
        return hasRole(user, authority, null);
    }

    public boolean hasRole(User user, PrismRole authority, Resource scope) {
        return roleDAO.getUserRole(user, scope, authority) != null;
    }

    public List<User> getUsersInRole(Resource scope, PrismRole... authorities) {
        return roleDAO.getUsersByRole(scope, authorities);
    }

    public User getUserInRole(Resource scope, PrismRole... authorities) {
        List<User> users = roleDAO.getUsersByRole(scope, authorities);
        return users.get(0);
    }

    public List<Program> getProgramsByUserAndRole(User currentUser, PrismRole administrator) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<User> getProgramAdministrators(Program program) {
        // TODO Auto-generated method stub
        return null;
    }

    public UserRole getUserRole(User user, PrismRole authority) {
        return roleDAO.getUserRole(user, authority);
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

                throw new Error("Attempted to process an invalid role creation transition");
            }
        }

        return userRoleTransitions;
    }

    private void validateUserRoleTransition(Resource resource, HashMap<User, RoleTransition> userRoleTransitions, RoleTransition roleTransition, User user) {
        if (roleDAO.getExcludingUserRole(user, resource, roleTransition.getRole().getExcludedRoles()).isEmpty()) {
            userRoleTransitions.put(user, roleTransition);
        } else {
            throw new Error("Attempted to process a conflicted role creation transition");
        }
    }

    public List<User> getUsers(Resource resource) {
        return roleDAO.getUsers(resource);
    }

    public List<PrismRole> getRoles(Resource resource, User user) {
        return roleDAO.getRoles(resource, user);
    }
}
