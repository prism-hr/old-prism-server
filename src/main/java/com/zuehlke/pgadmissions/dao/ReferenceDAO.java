package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ReferenceComment;

@Repository
public class ReferenceDAO {

	private final SessionFactory sessionFactory;

	public ReferenceDAO(){
		this(null);
	}
	
	@Autowired
	public ReferenceDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;		
	}

	public ReferenceComment getReferenceById(Integer id) {
		return (ReferenceComment) sessionFactory.getCurrentSession().get(ReferenceComment.class, id);
	}

}
