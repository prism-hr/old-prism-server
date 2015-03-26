package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProcessing;
import com.zuehlke.pgadmissions.domain.application.ApplicationProcessingSummary;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.ApplicationProcessingRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation.ApplicationProcessingSummaryRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationSummaryDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public ApplicationProcessing getProcessing(Application application, StateGroup stateGroup) {
        return (ApplicationProcessing) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }

    public ApplicationProcessingSummary getProcessingSummary(ResourceParent resource, StateGroup stateGroup) {
        return (ApplicationProcessingSummary) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessingSummary.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }

    public List<ApplicationProcessingRepresentation> getProcessings(Application application) {
        return (List<ApplicationProcessingRepresentation>) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class, "processing") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("stateGroup.id"), "stateGroup") //
                        .add(Projections.property("instanceCount"), "instanceTotal") //
                        .add(Projections.property("dayDurationAverage"), "instanceDurationAverage")) //
                .createAlias("stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.asc("stateGroup.sequenceOrder")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingRepresentation.class)) //
                .list();
    }

    public List<ApplicationProcessingSummaryRepresentation> getProcessingSummaries(ResourceParent resource) {
        return (List<ApplicationProcessingSummaryRepresentation>) sessionFactory.getCurrentSession()
                .createCriteria(ApplicationProcessingSummary.class, "processingSummary") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("stateGroup.id"), "stateGroup") //
                        .add(Projections.property("instanceCount"), "instanceTotal") //
                        .add(Projections.property("instanceCountLive"), "instanceTotalLive") //
                        .add(Projections.property("instanceCountAverageNonZero"), "instanceOccurrenceAverage") //
                        .add(Projections.property("dayDurationAverage"), "instanceDurationAverage")) //
                .createAlias("stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.asc("stateGroup.sequenceOrder")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryRepresentation.class)) //
                .list();
    }

}
