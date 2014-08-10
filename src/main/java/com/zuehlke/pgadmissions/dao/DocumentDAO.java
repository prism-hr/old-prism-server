package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void deleteOrphanDocuments() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete Document " //
                    + "where comment is null "
                        + "and applicationlanguageQualification is null "
                        + "and applicationQualification is null "
                        + "and applicationFunding is null "
                        + "and applicationCv is null "
                        + "and applicationPersonalStatement is null " //
                        + "and createdTimestamp <= :createdTimestamp") //
                .setParameter("createdTimestamp", new DateTime().minusDays(1)) //
                .executeUpdate();
    }

}
