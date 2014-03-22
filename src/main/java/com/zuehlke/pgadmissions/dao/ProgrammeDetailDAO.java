package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ProgramDetails;

@Repository
public class ProgrammeDetailDAO {

	private final SessionFactory sessionFactory;

	public ProgrammeDetailDAO(){
		this(null);
	}
	
	@Autowired
	public ProgrammeDetailDAO(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}

	public ProgramDetails getProgrammeDetailWithId(Integer id) {
		return (ProgramDetails) sessionFactory.getCurrentSession().get(ProgramDetails.class, id);
	}

	public void save(ProgramDetails pd) {
		sessionFactory.getCurrentSession().saveOrUpdate(pd);
	}
}
