package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Address;

@Repository
public class AddressDAO {

	private final SessionFactory sessionFactory;
	
	public AddressDAO(){
		this(null);
	}
	
	@Autowired
	public AddressDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}
	public void delete(Address address) {
		sessionFactory.getCurrentSession().delete(address);
	}
}
