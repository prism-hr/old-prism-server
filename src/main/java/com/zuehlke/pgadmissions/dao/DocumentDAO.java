package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void deleteOrphanDocuments() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete Document " //
                        + "where referenced = :referenced " //
                        + "and createdTimestamp <= :createdTimestamp") //
                .setParameter("referenced", false) //
                .setParameter("createdTimestamp", new LocalDate().minusDays(1)) //
                .executeUpdate();
    }

}
