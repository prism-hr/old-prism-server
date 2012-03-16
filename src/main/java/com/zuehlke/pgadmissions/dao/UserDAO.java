package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

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
		return (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
	}

	public RegisteredUser getUserByUsername(String username) {
		return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("username", username))
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getAllUsers() {
		return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).list();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getUsersInRole(Role role) {
		return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("roles").add(Restrictions.eq("id", role.getId())).list();

	}

	public void saveQualification(Qualification qual) {
		sessionFactory.getCurrentSession().saveOrUpdate(qual);
		
	}

	public Role getRoleById(int id) {
		return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", id))
				.uniqueResult();
	}

}
