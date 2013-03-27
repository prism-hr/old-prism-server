package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

@Repository
public class ApplicationFormTransferDAO {

    private final SessionFactory sessionFactory;

    public ApplicationFormTransferDAO() {
        this(null);
    }
    
    @Autowired
    public ApplicationFormTransferDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationFormTransfer transfer) {
        sessionFactory.getCurrentSession().saveOrUpdate(transfer);
    }

    public ApplicationFormTransfer getById(Long id) {
        return (ApplicationFormTransfer) sessionFactory.getCurrentSession().get(ApplicationFormTransfer.class, id);
    }
    
    public ApplicationFormTransfer getByReceivedBookingReferenceNumber(String bookingReferenceNumber) {
        return (ApplicationFormTransfer) sessionFactory.getCurrentSession()
                .createCriteria(ApplicationFormTransfer.class)
                .add(Restrictions.eq("uclBookingReferenceReceived", bookingReferenceNumber)).uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationFormTransfer> getAllTransfersWaitingForWebserviceCall() {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormTransfer.class).
            add(Restrictions.eq("status", ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL)).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationFormTransfer> getAllTransfersWaitingToBeSentToPorticoOldestFirst() {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormTransfer.class)
                .add(Restrictions.or(
                                Restrictions.eq("status", ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING), 
                                Restrictions.eq("status", ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL)))
                .addOrder(Order.asc("transferStartTimepoint")).list();
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationFormTransfer> getAllTransfersWaitingForAttachmentsSending() {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormTransfer.class).
            add(Restrictions.eq("status", ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING)).list();
    }

    public ApplicationFormTransfer getByApplicationForm(final ApplicationForm form) {
        return (ApplicationFormTransfer) sessionFactory.getCurrentSession().createCriteria(ApplicationFormTransfer.class).
                add(Restrictions.eq("applicationForm", form)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationFormTransfer> getAllTransfers() {
        return (List<ApplicationFormTransfer>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormTransfer.class).list();
    }
}
