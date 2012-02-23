package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

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
	
	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getReviewersForApplication(ApplicationForm application){
		List<RegisteredUser> users = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).list();
		List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();
		for (RegisteredUser user : users) {
			if (user.isInRole(Authority.REVIEWER) && !application.getReviewers().contains(user)) {
				reviewers.add(user);
			}
		}
		
		return reviewers;
	}

}
