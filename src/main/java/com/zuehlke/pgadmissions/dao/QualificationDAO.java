package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Qualification;

@Repository
public class QualificationDAO {

	private final SessionFactory sessionFactory;
	QualificationDAO(){
		this(null);
	}
	@Autowired
	public QualificationDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}
	public void delete(Qualification qualification) {
		sessionFactory.getCurrentSession().delete(qualification);
		
	}

}
