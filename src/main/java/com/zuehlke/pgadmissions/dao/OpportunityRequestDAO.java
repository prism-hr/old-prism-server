package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.OpportunityRequest;

@Repository
public class OpportunityRequestDAO {

	private final SessionFactory sessionFactory;

	public OpportunityRequestDAO() {
		this(null);
	}

	@Autowired
	public OpportunityRequestDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(OpportunityRequest opportunityRequest) {
		sessionFactory.getCurrentSession().saveOrUpdate(opportunityRequest);
	}

}
