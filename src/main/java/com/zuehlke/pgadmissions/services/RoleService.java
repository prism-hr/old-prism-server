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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityService entityService;

    public Role getById(Authority authority) {
        return roleDAO.getById(authority);
    }

    public UserRole createUserRole(PrismResource resource, User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setResource(resource);
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedTimestamp(new DateTime());
        return userRole;
    }

    public UserRole getOrCreateUserRole(PrismResource resource, User user, Role role) {
        UserRole transientUserRole = createUserRole(resource, user, role);
        return entityService.getOrCreate(transientUserRole);
    }

    public void removeUserRoles(User user, PrismResource resource, Authority... authorities) {
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
    
    public void executeUserRoleTransitions(StateTransition stateTransition, PrismResource resource, User invoker, Comment comment) {
        HashMap<User, RoleTransition> userRoleTransitions = Maps.newHashMap();
        userRoleTransitions.putAll(getUserRoleUpdateTransitions(stateTransition, resource, invoker));
        
        if (comment != null) {
            userRoleTransitions.putAll(getUserCreationRoleTransitions(stateTransition, resource, invoker, comment));
        }
        
        for (User user : userRoleTransitions.keySet()) {
            executeRoleTransition(resource, user, userRoleTransitions.get(user));
        }
    }
    
    public void executeDelegateUserRoleTransitions(PrismResource resource, StateTransition delegateStateTransition, User invoker) {
        executeUserRoleTransitions(delegateStateTransition, resource, invoker, null);
    }

    public void executeRoleTransition(PrismResource resource, User user, RoleTransition roleTransition) {
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

    public boolean hasAnyRole(User user, Authority... authorities) {
        for (Authority authority : authorities) {
            if (hasRole(user, authority, null)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRole(User user, Authority authority) {
        return hasRole(user, authority, null);
    }

    public boolean hasRole(User user, Authority authority, PrismResource scope) {
        return roleDAO.getUserRole(user, scope, authority) != null;
    }

    public List<User> getUsersInRole(PrismResource scope, Authority... authorities) {
        return roleDAO.getUsersByRole(scope, authorities);
    }

    public User getUserInRole(PrismResource scope, Authority... authorities) {
        List<User> users = roleDAO.getUsersByRole(scope, authorities);
        return users.get(0);
    }

    public List<Program> getProgramsByUserAndRole(User currentUser, Authority administrator) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<User> getProgramAdministrators(Program program) {
        // TODO Auto-generated method stub
        return null;
    }

    public UserRole getUserRole(User user, Authority authority) {
        return roleDAO.getUserRole(user, authority);
    }

    public List<Role> getActionRoles(PrismResource resource, PrismAction action) {
        return roleDAO.getActionRoles(resource, action);
    }

    public List<Role> getActionInvokerRoles(User user, PrismResource resource, PrismAction action) {
        return roleDAO.getActionInvokerRoles(user, resource, action);
    }

    private HashMap<User, RoleTransition> getUserRoleUpdateTransitions(StateTransition stateTransition, PrismResource resource, User invoker) {
        HashMap<User, RoleTransition> userRoleTransitions = Maps.newHashMap();
        
        HashMultimap<RoleTransition, User> roleTransitionUsers = roleDAO.getRoleTransitionUsers(stateTransition, resource, invoker);
        for (RoleTransition roleTransition : roleTransitionUsers.keySet()) {
            for (User user : roleTransitionUsers.get(roleTransition)) {
                validateUserRoleTransition(resource, userRoleTransitions, roleTransition, user);
            }
        }
        
        return userRoleTransitions;

    }

    private HashMap<User, RoleTransition> getUserCreationRoleTransitions(StateTransition stateTransition, PrismResource resource, User invoker, Comment comment) {
        HashMap<User, RoleTransition> userRoleTransitions = Maps.newHashMap();

        HashMultimap<Role, RoleTransition> roleCreationTransitions = roleDAO.getRoleCreationTransitions(stateTransition);
        for (Role role : roleCreationTransitions.keySet()) {
            for (RoleTransition roleTransition : roleCreationTransitions.get(role)) {
                User restrictedToUser = null;
                if (roleTransition.isRestrictToInvoker()) {
                    restrictedToUser = invoker;
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

    private void validateUserRoleTransition(PrismResource resource, HashMap<User, RoleTransition> userRoleTransitions, RoleTransition roleTransition, User user) {
        if (roleDAO.getExcludingUserRole(user, resource, roleTransition.getExcludedRoles()).isEmpty()) {
            userRoleTransitions.put(user, roleTransition);
        } else {
            throw new Error("Attempted to process a conflicted role creation transition");
        }
    }

}
