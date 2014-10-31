package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public void synchronizeProjectDueDates(LocalDate baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Project " //
                    + "set dueDate = :baseline " //
                    + "where dueDate < :baseline") //
                .setParameter("baseline", baseline) //
                .executeUpdate();
    }
    
}
