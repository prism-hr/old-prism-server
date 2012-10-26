package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Ethnicity;

@Repository
public class EthnicityDAO {

	private final SessionFactory sessionFactory;

	EthnicityDAO() {
		this(null);
	}

	@Autowired
	public EthnicityDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<Ethnicity> getAllEthnicities() {
        return sessionFactory.getCurrentSession().createCriteria(Ethnicity.class).addOrder(Order.asc("id"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
    public List<Ethnicity> getAllEnabledEthnicities() {
        return sessionFactory.getCurrentSession().createCriteria(Ethnicity.class).add(Restrictions.eq("enabled", true))
                .addOrder(Order.asc("id")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

	public Ethnicity getEthnicityById(Integer id) {
		return (Ethnicity) sessionFactory.getCurrentSession().get(Ethnicity.class, id);
	}

	public void save(Ethnicity ethnicity) {
		sessionFactory.getCurrentSession().saveOrUpdate(ethnicity);
	}
}