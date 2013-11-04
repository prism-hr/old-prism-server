package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRoleId;
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

    public void save(ApplicationFormUserRole applicationFormUserRole) {
        sessionFactory.getCurrentSession().merge(applicationFormUserRole);
    }

    public List<ApplicationFormUserRole> findByApplicationFormAndAuthorities(ApplicationForm applicationForm, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("id.applicationForm", applicationForm))
                .add(Restrictions.in("id.role.id", authorities)).list();
    }

    public List<ApplicationFormUserRole> findByApplicationForm(ApplicationForm applicationForm) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("id.applicationForm", applicationForm))
                .list();
    }

    public ApplicationFormUserRole findById(ApplicationFormUserRoleId id) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class, id);
    }

}
