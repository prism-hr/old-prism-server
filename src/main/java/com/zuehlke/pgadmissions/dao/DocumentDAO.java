package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Document;

@Repository
public class DocumentDAO {

    private final SessionFactory sessionFactory;

    public DocumentDAO() {
        this(null);
    }

    @Autowired
    public DocumentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Document document) {
        sessionFactory.getCurrentSession().saveOrUpdate(document);
    }

    public Document getDocumentbyId(Integer id) {
        return (Document) sessionFactory.getCurrentSession().get(Document.class, id);
    }

    public void deleteOrphanDocuments() {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_ORPHAN_DOCUMENTS();").executeUpdate();
    }

}
