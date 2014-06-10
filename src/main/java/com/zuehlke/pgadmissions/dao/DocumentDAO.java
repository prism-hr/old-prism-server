package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void deleteOrphanDocuments() {
        // TODO rewrite
//        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_ORPHAN_DOCUMENTS();").executeUpdate();
    }

}
