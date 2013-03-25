package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

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
    
    @SuppressWarnings("unchecked")
    public List<ApplicationFormTransferError> getByTransfer(final ApplicationFormTransfer transfer) {
        return (List<ApplicationFormTransferError>) sessionFactory.getCurrentSession()
                .createCriteria(ApplicationFormTransferError.class).add(Restrictions.eq("transfer", transfer)).list();
    }

}
