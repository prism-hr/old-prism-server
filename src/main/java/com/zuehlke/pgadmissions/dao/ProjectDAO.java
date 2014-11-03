package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.program.Program;

@Repository
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public void synchronizeProjectEndDates(Program program) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Project " //
                    + "set endDate = :baseline " //
                    + "where program = :program "
                    + "and endDate < :baseline") //
                .setParameter("program", program) //
                .setParameter("baseline", program.getEndDate()) //
                .executeUpdate();
    }
    
    public void synchronizeProjectDueDates(Program program) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Project " //
                    + "set dueDate = :baseline " //
                    + "where program = :program "
                    + "and dueDate < :baseline") //
                .setParameter("program", program) //
                .setParameter("baseline", program.getDueDate()) //
                .executeUpdate();
    }
    
}
