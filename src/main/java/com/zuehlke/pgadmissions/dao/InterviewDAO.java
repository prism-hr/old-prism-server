package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Interview;

@Repository
public class InterviewDAO {

	private final SessionFactory sessionFactory;
	
	InterviewDAO() {
		this(null);
	}

	@Autowired
	public InterviewDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	public void save(Interview interview) {
		sessionFactory.getCurrentSession().saveOrUpdate(interview);
		
	}
	
	public Interview getInterviewById(Integer id) {
		return (Interview) sessionFactory.getCurrentSession().get(Interview.class, id);
	}


}
