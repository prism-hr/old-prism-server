package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;

@Repository
public class ApplicationTransferErrorDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ApplicationTransferErrorDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationTransferError transferError) {
        sessionFactory.getCurrentSession().saveOrUpdate(transferError);
    }

}
