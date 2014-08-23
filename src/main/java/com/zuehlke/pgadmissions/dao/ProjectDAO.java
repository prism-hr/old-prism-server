package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    // TODO project specific queries
    
}
