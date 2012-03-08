package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetail;

@Repository
public class PersonalDetailDAO {

	private final SessionFactory sessionFactory;

	PersonalDetailDAO(){
		this(null);
	}
	@Autowired
	public PersonalDetailDAO(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}

	@Transactional
	public PersonalDetail getPersonalDetailWithApplication(ApplicationForm form) {
		return (PersonalDetail) sessionFactory.getCurrentSession().createCriteria(PersonalDetail.class).add(Restrictions.eq("application", form));
	}

	@Transactional
	public void save(PersonalDetail ps) {
		sessionFactory.getCurrentSession().saveOrUpdate(ps);
	}

}
