package com.zuehlke.pgadmissions.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferState;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationTransferDAO {

    private final SessionFactory sessionFactory;

    public ApplicationTransferDAO() {
        this(null);
    }

    @Autowired
    public ApplicationTransferDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationTransfer transfer) {
        sessionFactory.getCurrentSession().saveOrUpdate(transfer);
    }

    public ApplicationTransfer getById(Long id) {
        return (ApplicationTransfer) sessionFactory.getCurrentSession().get(ApplicationTransfer.class, id);
    }

    public ApplicationTransfer getByExternalTransferReference(String bookingReferenceNumber) {
        return (ApplicationTransfer) sessionFactory.getCurrentSession().createCriteria(ApplicationTransfer.class)
                .add(Restrictions.eq("externalTransferReference", bookingReferenceNumber)).uniqueResult();
    }

    public List<ApplicationTransfer> getAllTransfersWaitingToBeSentToPorticoOldestFirst() {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(ApplicationTransfer.class)
                .add(Restrictions.or(Restrictions.eq("status", ApplicationTransferState.QUEUED_FOR_ATTACHMENTS_SENDING),
                        Restrictions.eq("status", ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL))).addOrder(Order.asc("transferStartTimepoint")).list();
    }

    public List<Long> getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds() {
        Date weekAgo = new DateTime().minusWeeks(1).toDate();
        return sessionFactory
                .getCurrentSession()
                .createCriteria(ApplicationTransfer.class)
                .setProjection(Projections.id())
                .add(Restrictions.or(
                        Restrictions.eq("status", ApplicationTransferState.QUEUED_FOR_ATTACHMENTS_SENDING),
                        Restrictions.eq("status", ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL),
                        Restrictions.and(Restrictions.eq("status", ApplicationTransferState.REJECTED_BY_WEBSERVICE),
                                Restrictions.gt("createdTimestamp", weekAgo)))).addOrder(Order.asc("transferStartTimepoint")).list();
    }

    public ApplicationTransferError getErrorById(Long id) {
        return (ApplicationTransferError) sessionFactory.getCurrentSession().get(ApplicationTransferError.class, id);
    }
    
    public void requeueApplicationTransfer(final ApplicationForm application) {
        ApplicationTransfer transfer = application.getTransfer();
        if (transfer != null) {
            transfer.setBeganTimestamp(new Date());
            transfer.setState(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL);
            save(transfer);
        }
    }

}
