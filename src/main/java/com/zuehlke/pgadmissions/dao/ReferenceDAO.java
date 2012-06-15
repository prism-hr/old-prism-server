package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Reference;

@Repository
public class ReferenceDAO {

	private final SessionFactory sessionFactory;

	ReferenceDAO(){
		this(null);
	}
	
	@Autowired
	public ReferenceDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;		
	}

	public Reference getReferenceById(Integer id) {
		return (Reference) sessionFactory.getCurrentSession().get(Reference.class, id);
	}

}
