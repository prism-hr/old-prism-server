package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PersonalDetails;

@Repository
public class PersonalDetailDAO {

    private final SessionFactory sessionFactory;

    public PersonalDetailDAO() {
        this(null);
    }

    @Autowired
    public PersonalDetailDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(PersonalDetails personalDetails) {
        if (personalDetails.getId() == null) {
            sessionFactory.getCurrentSession().saveOrUpdate(personalDetails);
        } else {
            sessionFactory.getCurrentSession().merge(personalDetails);
        }
    }
    
    public PersonalDetails getPersonalDetailsById(Integer id) {
        return (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, id);
    }
}
