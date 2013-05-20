package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class ApplicationFormLastAccessDAO {
    
    private final SessionFactory sessionFactory;
    
    public ApplicationFormLastAccessDAO() {
        this(null);
    }
    
    @Autowired
    public ApplicationFormLastAccessDAO(final SessionFactory sessionFactory) {
        this.sessionFactory=sessionFactory;
    }
    
    public void saveAccess(ApplicationFormLastAccess access) {
        sessionFactory.getCurrentSession().saveOrUpdate(access);
    }
    
    public void deleteAccess(ApplicationFormLastAccess access) {
        sessionFactory.getCurrentSession().delete(access);
    }
    
    public ApplicationFormLastAccess getLastAccess(ApplicationForm form, RegisteredUser user) {
        return (ApplicationFormLastAccess) sessionFactory.getCurrentSession().createCriteria(ApplicationFormLastAccess.class)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("applicationForm", form))
                .uniqueResult();
    }
}
