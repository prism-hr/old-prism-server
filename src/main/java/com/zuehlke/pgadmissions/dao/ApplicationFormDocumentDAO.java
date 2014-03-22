package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationFormDocument;

@Repository
public class ApplicationFormDocumentDAO {

    @Autowired
	private SessionFactory sessionFactory;
	
    public ApplicationFormDocument getById(Integer id) {
        return (ApplicationFormDocument) sessionFactory.getCurrentSession().get(ApplicationFormDocument.class, id);
    }
    
    public void save(ApplicationFormDocument applicationFormDocument) {
        sessionFactory.getCurrentSession().saveOrUpdate(applicationFormDocument);
    }
	
}
