package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.InterviewTimeslot;

@Repository
public class InterviewTimeslotDAO {

	private final SessionFactory sessionFactory;

	public InterviewTimeslotDAO() {
		this(null);
	}

	@Autowired
	public InterviewTimeslotDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	public InterviewTimeslot getTimeslotById(Integer id) {
		return (InterviewTimeslot) sessionFactory.getCurrentSession().get(InterviewTimeslot.class, id);
	}

}