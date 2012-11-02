package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationFormTransferDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ApplicationFormTransferDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationFormTransfer transfer) {
        sessionFactory.getCurrentSession().saveOrUpdate(transfer);
    }

}
