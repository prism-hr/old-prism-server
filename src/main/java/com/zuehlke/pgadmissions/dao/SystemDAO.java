package com.zuehlke.pgadmissions.dao;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SystemDAO {

    @Inject
    private SessionFactory sessionFactory;

    public void clearSchema() {
        sessionFactory.getCurrentSession().createSQLQuery( //
                "call procedure_clear_schema()") //
                .executeUpdate();
    }

    public void resetAmazon() {
        sessionFactory.getCurrentSession().createSQLQuery( //
                "call procedure_reset_amazon()") //
                .executeUpdate();
    }

}
