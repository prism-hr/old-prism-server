package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class ApplicationFormUpdateDAO {
    
    private final SessionFactory sessionFactory;
    
    public ApplicationFormUpdateDAO() {
        this(null);
    }
    
    @Autowired
    public ApplicationFormUpdateDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public void saveUpdate(ApplicationFormUpdate update) {
        sessionFactory.getCurrentSession().save(update);
    }
    
    public void deleteUpdate(ApplicationFormUpdate update) {
        sessionFactory.getCurrentSession().delete(update);
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationFormUpdate> getUpdatesForUser(ApplicationForm form, RegisteredUser user) {
        DetachedCriteria lastAccessCriteria = DetachedCriteria.forClass(ApplicationFormLastAccess.class, "lastAccess")
                .setProjection(Projections.property("lastAccess.lastAccessTimestamp"))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("applicationForm", form));
        
        return (List<ApplicationFormUpdate>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUpdate.class)
                .add(Restrictions.eq("applicationForm", form))
                //gets all the updates if the user has never accessed the form or gets the only updates the user missed by getting his last access timestamp
                .add(Restrictions.or(Subqueries.notExists(lastAccessCriteria), Property.forName("updateTimestamp").gt(lastAccessCriteria)))
                .addOrder(Order.asc("updateTimestamp"))
                .list();
    }

}
