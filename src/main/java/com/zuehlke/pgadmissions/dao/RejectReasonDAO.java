package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RejectReason;

@Repository
public class RejectReasonDAO {

	private final SessionFactory sessionFactory;

	public RejectReasonDAO() {
		this(null);
	}

	public RejectReasonDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<RejectReason> getAllReasons() {
		return sessionFactory.getCurrentSession().createCriteria(RejectReason.class).addOrder(Order.asc("id")).list();
	}
}
