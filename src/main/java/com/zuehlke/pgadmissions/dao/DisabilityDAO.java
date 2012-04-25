package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Disability;

@Repository
public class DisabilityDAO {

	private final SessionFactory sessionFactory;

	DisabilityDAO() {
		this(null);
	}

	@Autowired
	public DisabilityDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<Disability> getAllDisabilities() {
		return sessionFactory.getCurrentSession().createCriteria(Disability.class).addOrder(Order.asc("name")).list();
	}

	public Disability getDisabilityById(Integer id) {
		return (Disability) sessionFactory.getCurrentSession().get(Disability.class, id);
	}

}
