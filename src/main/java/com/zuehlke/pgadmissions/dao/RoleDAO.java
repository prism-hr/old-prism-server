package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.PrismSystem;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Repository
public class RoleDAO {

    private SessionFactory sessionFactory;

    public RoleDAO() {
    }

    @Autowired
    public RoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public PrismSystem getPrismSystem() {
        return (PrismSystem) sessionFactory.getCurrentSession().createCriteria(PrismSystem.class).uniqueResult();
    }

    public Role getById(final Authority id) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public int save(UserRole userRole) {
        return (Integer) sessionFactory.getCurrentSession().save(userRole);
    }

    public List<User> getUsersInRole(PrismScope scope, Authority[] authorities) {
        // TODO Auto-generated method stub, sort by first and last names
        return null;
    }

    public User getUserInRole(PrismScope scope, Authority[] authorities) {
        // TODO Auto-generated method stub
        return null;
    }

    public UserRole get(User user, PrismScope scope, Authority authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq(scope.getScopeName(), scope)) //
                .uniqueResult();
    }

}
