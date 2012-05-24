package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RegistryUser;

@Repository
public class RegistryUserDAO {

private final SessionFactory sessionFactory;
	
	RegistryUserDAO(){
		this(null);
	}
	
	@Autowired
	public RegistryUserDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public RegistryUser getRegistryUserWithId(Integer id) {
		return (RegistryUser) sessionFactory.getCurrentSession().get(
				RegistryUser.class, id);
	}
	
	public void save(RegistryUser registryUser) {
		sessionFactory.getCurrentSession().saveOrUpdate(registryUser);
	}

}
