package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class UserDAO {

	private final SessionFactory sessionFactory;

	UserDAO() {
		this(null);
	}

	@Autowired
	public UserDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(RegisteredUser user) {
		sessionFactory.getCurrentSession().saveOrUpdate(user);
	}

	public RegisteredUser get(Integer id) {
		return (RegisteredUser) sessionFactory.getCurrentSession().get(
				RegisteredUser.class, id);
	}

	public RegisteredUser getUserByUsername(String username) {
		return (RegisteredUser) sessionFactory.getCurrentSession()
				.createCriteria(RegisteredUser.class)
				.add(Restrictions.eq("username", username)).uniqueResult();
	}

}
