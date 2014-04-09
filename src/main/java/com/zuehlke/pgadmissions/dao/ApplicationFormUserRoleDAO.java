package com.zuehlke.pgadmissions.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationFormUserRoleDAO {

    private final SessionFactory sessionFactory;

    public ApplicationFormUserRoleDAO() {
        this(null);
    }

    @Autowired
    public ApplicationFormUserRoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public ApplicationFormUserRole get(Integer id) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class, id);
    }

    public Date getUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm, ApplicationUpdateScope updateVisibility) {
        return (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .createAlias("role", "applicationRole")
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.ge("applicationRole.updateVisibility", updateVisibility))
                .setProjection(Projections.projectionList()
                        .add(Projections.max("updateTimestamp"))).uniqueResult();
    }
    
    public List<ApplicationFormUserRole> getByApplicationFormAndAuthorities(ApplicationForm applicationForm, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.in("role.id", authorities)).list();
    }

    public List<ApplicationFormUserRole> getByApplicationFormAndUserAndAuthorities(ApplicationForm applicationForm, User user, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.in("role.id", authorities)).list();
    }
    
    public List<ApplicationFormUserRole> getByApplicationFormAndUserAndAuthoritiesWithActions(ApplicationForm applicationForm, User user, List<Authority> authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.in("role.id", authorities)).list();
    }

}
