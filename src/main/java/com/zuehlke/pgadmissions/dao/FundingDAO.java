package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Funding;

@Repository
public class FundingDAO {

    @Autowired
	private SessionFactory sessionFactory;

	public Funding getById(Integer id) {
        return (Funding) sessionFactory.getCurrentSession().get(Funding.class, id);
    }
	
	public void save(Funding funding) {
        sessionFactory.getCurrentSession().saveOrUpdate(funding);
    }

	public void delete(Funding funding) {
		sessionFactory.getCurrentSession().delete(funding);
	}
	
}
