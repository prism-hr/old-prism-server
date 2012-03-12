package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	

	public void save(PersonalDetail personalDetails) {
		sessionFactory.getCurrentSession().saveOrUpdate(personalDetails);
	}
	
	public PersonalDetail getPersonalDetailsById(Integer id) {
		return (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, id);
	}

}
