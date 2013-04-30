package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.SourcesOfInterest;

@Repository
@SuppressWarnings("unchecked")
public class SourcesOfInterestDAO {

    private final SessionFactory sessionFactory;

    public SourcesOfInterestDAO() {
        this(null);
    }

    @Autowired
    public SourcesOfInterestDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<SourcesOfInterest> getAllEnabledSourcesOfInterest() {
        return sessionFactory.getCurrentSession().createCriteria(SourcesOfInterest.class)
                .add(Restrictions.eq("enabled", true)).addOrder(Order.asc("name")).list();
    }
    
    public List<SourcesOfInterest> getAllSourcesOfInterest() {
        return sessionFactory.getCurrentSession().createCriteria(SourcesOfInterest.class)
                .addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public SourcesOfInterest getSourcesOfInterestById(Integer id) {
        return (SourcesOfInterest) sessionFactory.getCurrentSession().get(SourcesOfInterest.class, id);
    }

	public void save(SourcesOfInterest sourceOfInterest) {
		sessionFactory.getCurrentSession().saveOrUpdate(sourceOfInterest);
	}
}
