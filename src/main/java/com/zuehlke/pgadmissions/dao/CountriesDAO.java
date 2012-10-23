package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Country;

@Repository
public class CountriesDAO {

	private final SessionFactory sessionFactory;

	CountriesDAO() {
		this(null);
	}

	@Autowired
	public CountriesDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<Country> getAllCountries() {
		return sessionFactory.getCurrentSession().createCriteria(Country.class).addOrder(Order.asc("name")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public Country getCountryById(Integer id) {
		return (Country) sessionFactory.getCurrentSession().get(Country.class, id);
	}
	
	public void save(Country country) {
		sessionFactory.getCurrentSession().saveOrUpdate(country);
	}


}
