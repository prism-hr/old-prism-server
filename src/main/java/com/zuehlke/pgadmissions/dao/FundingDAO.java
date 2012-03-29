package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Funding;

@Repository
public class FundingDAO {

	private final SessionFactory sessionFactory;
	FundingDAO(){
		this(null);
	}
	@Autowired
	public FundingDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}
	public void delete(Funding funding) {
		sessionFactory.getCurrentSession().delete(funding);
		
	}

}
