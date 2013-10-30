package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;

@Repository
public class AdditionalInfoDAO {

	private final SessionFactory sessionFactory;

	AdditionalInfoDAO() {
		this(null);
	}

	@Autowired
	public AdditionalInfoDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(AdditionalInformation info) {
		sessionFactory.getCurrentSession().saveOrUpdate(info);
	}
}