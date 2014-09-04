package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationProcessing;
import com.zuehlke.pgadmissions.domain.ApplicationProcessingSummary;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ParentResource;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.dto.ApplicationRatingDTO;
import com.zuehlke.pgadmissions.dto.ApplicationRatingSummaryDTO;

@Repository
public class ApplicationSummaryDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public ApplicationRatingDTO getApplicationRatingSummary(Application application) {
        return (ApplicationRatingDTO) sessionFactory.getCurrentSession().createCriteria(Comment.class, "comment") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.count("id"), "ratingCount") //
                        .add(Projections.avg("rating"), "ratingAverage")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("rating")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationRatingDTO.class)) //
                .uniqueResult();
    }
    
    public ApplicationRatingSummaryDTO getApplicationRatingSummary(ParentResource parentResource) {
        return (ApplicationRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(Comment.class, "comment") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.avg("id"), "ratingCountAverage") //
                        .add(Projections.avg("rating"), "ratingAverage")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(parentResource.getResourceScope().getLowerCaseName(), parentResource)) //
                .add(Restrictions.isNotNull("rating")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationRatingDTO.class)) //
                .uniqueResult();
    }

    public Object getPercentileValue(ParentResource summaryResource, String property, Integer percentile) {
        return (Object) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.property(property)) //
                .add(Restrictions.eq(summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.isNotNull(property)) //
                .addOrder(Order.asc(property)) //
                .setFirstResult(percentile - 1) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public ApplicationProcessing getProcessing(Application application, StateGroup stateGroup) {
        return (ApplicationProcessing) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }
    
    public ApplicationProcessingSummary getProcessingSummary(ParentResource summaryResource, StateGroup stateGroup) {
        return (ApplicationProcessingSummary) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessingSummary.class) //
                .add(Restrictions.eq(summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }

    public Long getNotNullProcessingCount(ParentResource summaryResource, StateGroup stateGroup) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class) //
                .setProjection(Projections.count("application")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }

    public Object getProcessingPercentileValue(ParentResource summaryResource, StateGroup stateGroup, String property, Integer percentile) {
        return (Object) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class) //
                .setProjection(Projections.property(property)) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .addOrder(Order.asc(property)) //
                .setFirstResult(percentile - 1) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Double getInstanceCountAverage(ParentResource summaryResource, StateGroup stateGroup) {
        return (Double) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class) //
                .setProjection(Projections.avg("instanceCount")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }
    
    public Double getDayDurationSumAverage(ParentResource summaryResource, StateGroup stateGroup) {
        return (Double) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class) //
                .setProjection(Projections.avg("dayDurationSum")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }
    
}
