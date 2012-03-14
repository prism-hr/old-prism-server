package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;

@Repository
public class ApplicantRecordDAO {

	private final SessionFactory sessionFactory;
	
	ApplicantRecordDAO() {
		this(null);
		
	}
	@Autowired
	public ApplicantRecordDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		
	}

	public void save(ApplicantRecord record) {
		System.out.println("sessionFactory" + sessionFactory);
		sessionFactory.getCurrentSession().saveOrUpdate(record);
	}

}
