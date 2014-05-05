package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

@Repository
@SuppressWarnings("unchecked")
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

    public Role saveRole(final Role role) {
        return (Role) sessionFactory.getCurrentSession().merge(role);
    }

    public UserRole saveUserRole(UserRole userRole) {
        return (UserRole) sessionFactory.getCurrentSession().merge(userRole);
    }

    public List<User> getUsersInRole(PrismScope scope, Authority[] authorities) {
        // TODO Auto-generated method stub, sort by first and last names
        return null;
    }

    public User getUserInRole(PrismScope scope, Authority[] authorities) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasRole(User user, Authority authority, PrismScope scope) {
        // TODO Auto-generated method stub
        return false;
    }

}
