package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Countries;

@Repository
public class CountriesDAO {

	private final SessionFactory sessionFactory;
	
	CountriesDAO(){
		this(null);
	}
	
	@Autowired
	public CountriesDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public List<Countries> getAllCountries() {
		return  (List<Countries>)sessionFactory.getCurrentSession()
				.createCriteria(Countries.class).list();
	}

	public Countries getCountryWithName(String countryName) {
		return (Countries) sessionFactory.getCurrentSession()
		.createCriteria(Countries.class)
		.add(Restrictions.eq("name", countryName)).uniqueResult();
	}
	
}
