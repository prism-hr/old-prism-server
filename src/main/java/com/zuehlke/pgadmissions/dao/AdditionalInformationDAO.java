package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;

@Repository
public class AdditionalInformationDAO {

    @Autowired
	private SessionFactory sessionFactory;

	public void save(AdditionalInformation additionalInformation) {
		sessionFactory.getCurrentSession().saveOrUpdate(additionalInformation);
	}
	
}
