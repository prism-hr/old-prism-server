package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.PrismSystem;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityScope;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    public Role getById(Authority authority) {
        return roleDAO.getById(authority);
    }

    public UserRole createUserRole(PrismScope scope, User user, Authority authority) {
        UserRole userRole = new UserRole().withUser(user).withRole(getById(authority));
        PrismScope unproxiedScope = HibernateUtils.unproxy(scope);
        if (unproxiedScope instanceof PrismScope) {
            userRole.setSystem((PrismSystem) scope);
        } else if (unproxiedScope instanceof Institution) {
            userRole.setInstitution((Institution) scope);
        } else if (unproxiedScope instanceof Program) {
            userRole.setProgram((Program) scope);
        } else if (unproxiedScope instanceof Project) {
            userRole.setProject((Project) scope);
        } else if (unproxiedScope instanceof ApplicationForm) {
            userRole.setApplication((ApplicationForm) scope);
        }
        return roleDAO.saveUserRole(userRole);
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
        return roleDAO.hasRole(user, authority, scope);
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
        // TODO Auto-generated method stub
        return null;
    }

}
