package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ProgrammeDetail;

@Repository
public class ProgrammeDetailDAO {

	private final SessionFactory sessionFactory;

	ProgrammeDetailDAO(){
		this(null);
	}
	@Autowired
	public ProgrammeDetailDAO(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}


	public ProgrammeDetail getProgrammeDetailWithId(Integer id) {
		return (ProgrammeDetail) sessionFactory.getCurrentSession().get(ProgrammeDetail.class, id);
	}

	public void save(ProgrammeDetail pd) {
		sessionFactory.getCurrentSession().saveOrUpdate(pd);
	}

}
