package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;

@SuppressWarnings("unchecked")
@Repository
public class OpportunityRequestDAO {

    private final SessionFactory sessionFactory;

    public OpportunityRequestDAO() {
        this(null);
    }

    @Autowired
    public OpportunityRequestDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(OpportunityRequest opportunityRequest) {
        sessionFactory.getCurrentSession().saveOrUpdate(opportunityRequest);
    }

    public List<OpportunityRequest> getInitialOpportunityRequests() {
        DetachedCriteria initialRequestsWithProgramsCriteria = DetachedCriteria.forClass(OpportunityRequest.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("sourceProgram")) //
                        .add(Projections.max("createdDate")) //
                        .add(Projections.max("id"))) //
                .add(Restrictions.isNotNull("sourceProgram"));

        return sessionFactory
                .getCurrentSession()
                .createCriteria(OpportunityRequest.class)
                .add(Restrictions.disjunction()
                        .add(Subqueries.propertiesIn(new String[] { "sourceProgram", "createdDate", "id" }, initialRequestsWithProgramsCriteria))
                        .add(Restrictions.isNull("sourceProgram"))) //
                .addOrder(Order.asc("status")).list();
    }

    public OpportunityRequest findById(Integer requestId) {
        return (OpportunityRequest) sessionFactory.getCurrentSession().get(OpportunityRequest.class, requestId);
    }

    public List<OpportunityRequest> getOpportunityRequests(Program program) {
        return sessionFactory.getCurrentSession() //
                .createCriteria(OpportunityRequest.class) //
                .add(Restrictions.eq("sourceProgram", program)) //
                .addOrder(Order.desc("createdDate")).list();
    }

    public List<OpportunityRequest> findByProgramAndStatus(Program program, OpportunityRequestStatus status) {
        return sessionFactory.getCurrentSession() //
                .createCriteria(OpportunityRequest.class) //
                .add(Restrictions.eq("sourceProgram", program)) //
                .add(Restrictions.eq("status", status)) //
                .list();
    }

    public List<OpportunityRequest> findByStatus(OpportunityRequestStatus status) {
        return sessionFactory.getCurrentSession() //
                .createCriteria(OpportunityRequest.class) //
                .add(Restrictions.eq("status", status)) //
                .list();
    }

    public List<OpportunityRequest> getOpportunityRequestsForAuthor(RegisteredUser author) {
        return sessionFactory.getCurrentSession() //
                .createCriteria(OpportunityRequest.class)
                .add(Restrictions.eq("author", author))
                .list();
    }

}
