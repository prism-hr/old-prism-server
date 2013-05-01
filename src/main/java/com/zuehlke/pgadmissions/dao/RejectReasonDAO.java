package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RejectReason;

@Repository
@SuppressWarnings("unchecked")
public class RejectReasonDAO {

	private final SessionFactory sessionFactory;

	public RejectReasonDAO() {
		this(null);
	}

	@Autowired
	public RejectReasonDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public List<RejectReason> getAllReasons() {
		return sessionFactory.getCurrentSession().createCriteria(RejectReason.class).addOrder(Order.asc("id")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public RejectReason getRejectReasonById(Integer id) {
		return (RejectReason) sessionFactory.getCurrentSession().get(RejectReason.class, id);
	}
}
