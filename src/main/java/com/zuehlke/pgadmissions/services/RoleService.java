package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO.UserRoleTransition;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityScope;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

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

    public void saveUserRole(UserRole userRole) {
        entityService.save(userRole);
    }   
    
    public void deleteUserRole(UserRole userRole) {
        entityService.delete(userRole);
    }

    public void executeRoleTransition(PrismResource resource, UserRoleTransition userRoleTransition) {
        User user = userRoleTransition.getUser();
        RoleTransition roleTransition = userRoleTransition.getRoleTransition();
        
        switch (roleTransition.getRoleTransitionType()) {
        case BRANCH:
            saveUserRole(getOrCreateUserRole(resource, user, roleTransition.getTransitionRole()));
            break;
        case CREATE:
            saveUserRole(getOrCreateUserRole(resource, user, roleTransition.getRole()));
            break;
        case REJOIN:
            saveUserRole(getOrCreateUserRole(resource, user, roleTransition.getTransitionRole()));
            deleteUserRole(getOrCreateUserRole(resource, user, roleTransition.getRole()));
            break;
        case REMOVE:
            deleteUserRole(getOrCreateUserRole(resource, user, roleTransition.getRole()));
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
        return roleDAO.get(user, scope, authority) != null;
    }

    public List<User> getUsersInRole(PrismResource scope, Authority... authorities) {
        return roleDAO.getUsersInRole(scope, authorities);
    }

    public User getUserInRole(PrismResource scope, Authority... authorities) {
        List<User> users = roleDAO.getUsersInRole(scope, authorities);
        return users.get(0);
    }

    public List<Program> getProgramsByUserAndRole(User currentUser, Authority administrator) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Removes given roles from the user.
     * 
     * @param user
     *            user to remove roles from
     * @param scope
     *            specifies roles' scope, system scope when <code>null</code>
     * @param authorities
     *            role to remove, when <code>null</code> removes all the roles in given scope
     */
    public void removeRoles(User user, PrismResource scope, Authority... authorities) {
        // TODO Auto-generated method stub
    }

    public List<User> getProgramAdministrators(Program program) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Authority> getAuthorities(AuthorityScope program) {
        // TODO Auto-generated method stub
        return null;
    }

    public User getInvitingAdmin(User user) {
        // TODO implement
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

//    public List<UserRoleTransition> getUserRoleTransitions(StateTransition stateTransition, PrismResource resource, User invoker, Comment comment) {
//        // Handle the creator roles. They are not stored yet so we need a different strategy
//        List<UserRoleTransition> userRoleTransitions = Lists.newArrayList();
//        
//        List<RoleTransition> creatorRoleTransitions = roleDAO.getCreatorRoleTransitions(stateTransition);
//        for (CommentAssignedUser assignedUser : comment.getCommentAssignedUsers()) {
//            if (assignedUser.getRole() == )
//        }
//        
//        // Then handle the other 
//        
//        return roleDAO.getRoleTransitions(stateTransition, resource, invoker);
//    }

}
