package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Repository
public class RoleDAO {

	private final SessionFactory sessionFactory;
	
	public RoleDAO() {
	    this(null);
	}

	@Autowired
	public RoleDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Role getRoleByAuthority(final Authority authority) {
		return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", authority)).uniqueResult();
	}
	
}
