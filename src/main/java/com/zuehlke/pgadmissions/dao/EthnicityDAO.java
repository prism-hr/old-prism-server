package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
		return sessionFactory.getCurrentSession().createCriteria(Ethnicity.class).addOrder(Order.asc("id")).list();
	}

	public Ethnicity getEthnicityById(Integer id) {
		return (Ethnicity) sessionFactory.getCurrentSession().get(Ethnicity.class, id);
	}
}