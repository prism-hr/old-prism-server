package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityScope;
import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    public Role getById(Authority authority) {
        return roleDAO.getById(authority);
    }

    public UserRole getOrCreateUserRole(PrismResource scope, User user, Authority authority) {
        Role role = roleDAO.getById(authority);
        UserRole userRole = roleDAO.get(user, scope, authority);
        if (userRole == null) {
            userRole = new UserRole().withUser(user).withRole(role).withScope(scope).withAssignedTimestamp(new DateTime());
            roleDAO.save(userRole);
        }
        return userRole;
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
        return roleDAO.getUserInRole(scope, authorities);
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

    public System getPrismSystem() {
        return roleDAO.getPrismSystem();
    }

    public User getInvitingAdmin(User user) {
        // TODO implement
        return null;
    }

    public UserRole getUserRole(User user, Authority authority) {
        return roleDAO.getUserRole(user, authority);
    }

    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, Role invokingRole) {
        return roleDAO.getRoleTransitions(stateTransition, invokingRole);
    }

    public Role canExecute(User user, PrismResource scope, ApplicationFormAction action) {
        return roleDAO.getExecutorRole(user, scope, action);
    }

    public List<User> getBy(Role role, PrismResource scope) {
        return roleDAO.getBy(role, scope);
    }

    public void executeRoleTransition(PrismResource scope, User user, Role role, RoleTransitionType type, PrismResource newScope, Role newRole) {
        UserRole userRole = getUserRole(user, role.getId());
        switch (type) {
        case UPDATE:
            userRole.setRole(newRole);
            userRole.setScope(newScope);
            break;
        default:
            break;
        }
    }

    public Role getCreatorRole(ApplicationFormAction action, PrismResource scope) {
        return roleDAO.getCreatorRole(action, scope);
    }

}
