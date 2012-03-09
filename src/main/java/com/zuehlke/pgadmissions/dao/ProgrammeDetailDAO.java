package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
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

	@Transactional
	public ProgrammeDetail getProgrammeDetailWithApplication(ApplicationForm form) {
		return (ProgrammeDetail) sessionFactory.getCurrentSession().createCriteria(ProgrammeDetail.class).add(Restrictions.eq("application", form)).uniqueResult();
	}

	@Transactional
	public void save(ProgrammeDetail pd) {
		sessionFactory.getCurrentSession().saveOrUpdate(pd);
	}

}
