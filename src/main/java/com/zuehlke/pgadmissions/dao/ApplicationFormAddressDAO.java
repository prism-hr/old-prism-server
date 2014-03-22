package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationFormAddress;

@Repository
public class ApplicationFormAddressDAO {

    @Autowired
	private SessionFactory sessionFactory;
	
    public ApplicationFormAddress getById(Integer id) {
        return (ApplicationFormAddress) sessionFactory.getCurrentSession().get(ApplicationFormAddress.class, id);
    }
    
    public void save(ApplicationFormAddress applicationFormAddress) {
        sessionFactory.getCurrentSession().saveOrUpdate(applicationFormAddress);
    }
	
}
