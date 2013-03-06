package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.QualificationType;

@Repository
public class QualificationTypeDAO {

    private final SessionFactory sessionFactory;

    public QualificationTypeDAO() {
        this(null);
    }

    @Autowired
    public QualificationTypeDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public List<QualificationType> getAllEnabledQualificationTypes() {
        return sessionFactory.getCurrentSession().createCriteria(QualificationType.class)
                .add(Restrictions.eq("enabled", true)).addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<QualificationType> getAllQualificationTypes() {
        return sessionFactory.getCurrentSession().createCriteria(QualificationType.class)
                .addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public QualificationType getQualificationTypeById(Integer id) {
        return (QualificationType) sessionFactory.getCurrentSession().get(QualificationType.class, id);
    }

	public void save(QualificationType qualification) {
		sessionFactory.getCurrentSession().saveOrUpdate(qualification);
	}
}
