package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO.RoleTransitionInstruction;
import com.zuehlke.pgadmissions.dao.RoleDAO.UserRoleTransition;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityScope;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;

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

    public void saveUserRole(UserRole userRole) {
        entityService.save(userRole);
    }

    public void deleteUserRole(UserRole userRole) {
        entityService.delete(userRole);
    }

    public void executeRoleTransition(PrismResource resource, UserRoleTransition userRoleTransition) {
        switch (userRoleTransition.getRoleTransitionType()) {
        case BRANCH:
        case CREATE:
            saveUserRole(getOrCreateUserRole(resource, userRoleTransition.getUser(), userRoleTransition.getTransitionRole()));
            break;
        case REJOIN:
            saveUserRole(getOrCreateUserRole(resource, userRoleTransition.getUser(), userRoleTransition.getTransitionRole()));
            deleteUserRole(getOrCreateUserRole(resource, userRoleTransition.getUser(), userRoleTransition.getRole()));
            break;
        case UPDATE:
            UserRole userRole = getOrCreateUserRole(resource, userRoleTransition.getUser(), userRoleTransition.getRole());
            userRole.setRole(userRoleTransition.getTransitionRole());
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

    // TODO: exclusions
    public List<UserRoleTransition> getUserRoleTransitions(StateTransition stateTransition, PrismResource resource, User invoker, Comment comment) {
        List<UserRoleTransition> transitions = Lists.newArrayList();
        transitions.addAll(roleDAO.getRoleTransitions(stateTransition, resource, invoker));
        transitions.addAll(getUserCreationRoleTransitions(stateTransition, invoker, comment));
        return transitions;
    }

    private List<UserRoleTransition> getUserCreationRoleTransitions(StateTransition stateTransition, User invoker, Comment comment) {
        List<UserRoleTransition> transitions = Lists.newArrayList();
        
        HashMultimap<Role, RoleTransitionInstruction> instructions = roleDAO.getUserRoleCreationInsructions(stateTransition);
        for (Role role : instructions.keySet()) {
            for (RoleTransitionInstruction instruction : instructions.get(role)) {
                User restrictedToUser = null;
                if (instruction.isRestrictToInvoker()) {
                    restrictedToUser = invoker;
                }
                
                List<User> users = commentService.getAssignedUsersByRole(comment, role, restrictedToUser);
                
                Integer minimumPermitted = instruction.getMinimumPermitted();
                Integer maximumPermitted = instruction.getMinimumPermitted();
                
                if (minimumPermitted == null || users.size() >= minimumPermitted && (maximumPermitted == null || users.size() <= maximumPermitted)) {
                    for (User user : users) {
                        transitions.add(roleDAO.new UserRoleTransition(user, role, RoleTransitionType.CREATE, role));
                    }
                }
                
                throw new Error("Attempted to process an invalid role creation transition");
            }
        }
        
        return transitions;
    }

}
