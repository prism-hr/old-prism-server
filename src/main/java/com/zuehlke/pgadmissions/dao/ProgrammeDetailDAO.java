package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ProgrammeDetails;

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

	public ProgrammeDetails getProgrammeDetailWithId(Integer id) {
		return (ProgrammeDetails) sessionFactory.getCurrentSession().get(ProgrammeDetails.class, id);
	}

	public void save(ProgrammeDetails pd) {
		sessionFactory.getCurrentSession().saveOrUpdate(pd);
	}
}
