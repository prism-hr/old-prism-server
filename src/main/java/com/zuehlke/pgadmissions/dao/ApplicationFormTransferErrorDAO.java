package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationFormTransferErrorDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ApplicationFormTransferErrorDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationFormTransferError transferError) {
        sessionFactory.getCurrentSession().saveOrUpdate(transferError);
    }

    public ApplicationFormTransferError getById(Long id) {
        return (ApplicationFormTransferError) sessionFactory.getCurrentSession().get(ApplicationFormTransferError.class, id);
    }

}
