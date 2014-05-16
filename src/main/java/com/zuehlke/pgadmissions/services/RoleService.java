package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.PrismSystem;
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

    public UserRole getOrCreateUserRole(PrismScope scope, User user, Authority authority) {
        Role role = roleDAO.getById(authority);
        UserRole userRole = roleDAO.get(user, scope, authority);
        if (userRole == null) {
            userRole = new UserRole().withUser(user).withRole(role);
            try {
                PropertyUtils.setSimpleProperty(userRole, scope.getScopeName(), scope);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            userRole.setAssignedTimestamp(new DateTime());
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

    public boolean hasRole(User user, Authority authority, PrismScope scope) {
        return roleDAO.get(user, scope, authority) != null;
    }

    public List<User> getUsersInRole(PrismScope scope, Authority... authorities) {
        return roleDAO.getUsersInRole(scope, authorities);
    }

    public User getUserInRole(PrismScope scope, Authority... authorities) {
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
    public void removeRoles(User user, PrismScope scope, Authority... authorities) {
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

    public PrismSystem getPrismSystem() {
        return roleDAO.getPrismSystem();
    }

    public User getInvitingAdmin(User user) {
        // TODO implement
        return null;
    }

    public UserRole getUserRole(User user, Authority authority) {
        return roleDAO.getUserRole(user, authority);
    }

    public PrismScope getEnclosingScope(Authority authority, PrismScope currentScope) {
        String roleName = authority.name();
        String scopeName = roleName.substring(0, roleName.indexOf('_')).toLowerCase();

        try {
            return (PrismScope) PropertyUtils.getSimpleProperty(currentScope, scopeName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, Role invokingRole) {
        return roleDAO.getRoleTransitions(stateTransition, invokingRole);
    }

    public Role canExecute(User user, PrismScope scope, ApplicationFormAction action) {
        return roleDAO.canExecute(user, scope, action);
    }

    public List<User> getBy(Role role, PrismScope scope) {
        return roleDAO.getBy(role, scope);
    }

    public void executeRoleTransition(PrismScope scope, User user, Role role, RoleTransitionType type, PrismScope newScope, Role newRole) {
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

}
