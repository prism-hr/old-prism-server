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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.ParentResource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ApplicationRatingDTO;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Application getPreviousSubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class)
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.ne("id", application.getId())) //
                .addOrder(Order.desc("submittedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }
    
    public Application getPreviousUnsubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class)
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.isNull("submittedTimestamp")) //
                .add(Restrictions.ne("id", application.getId())) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }
    
    public String getApplicationExportReference(Application application) {
        return (String) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("exportReference")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("exportReference")) //
                .uniqueResult();
    }

    public String getApplicationCreatorIpAddress(Application application) {
        return (String) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("creatorIpAddress")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("creatorIpAddress")) //
                .uniqueResult();
    }
    
    public User getPrimarySupervisor(Comment offerRecommendationComment) {
        return (User) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("comment", offerRecommendationComment)) //
                .add(Restrictions.eq("role.id", PrismRole.APPLICATION_PRIMARY_SUPERVISOR)) //
                .uniqueResult();
    }
    
    public List<ApplicationReferee> getApplicationExportReferees(Application application) {
        return (List<ApplicationReferee>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("comment", "comment", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("comment.rating")) //
                .addOrder(Order.asc("comment.createdTimestamp")) //
                .list();
    }
    
    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return (List<ApplicationQualification>) sessionFactory.getCurrentSession().createCriteria(ApplicationQualification.class) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("awardDate")) //
                .addOrder(Order.desc("startDate")) //
                .list();
    }
    
    public List<Application> getUclApplicationsForExport() {
        return (List<Application>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
               .createAlias("state", "state", JoinType.INNER_JOIN) //
               .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
               .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
               .createAlias("institution", "institution", JoinType.INNER_JOIN) //
               .add(Restrictions.eq("institution.uclInstitution", true)) //
               .add(Restrictions.eq("action.actionCategory", PrismActionCategory.EXPORT_RESOURCE)) //
               .list();
    }
    
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

    public Object getPercentileValue(ParentResource parentResource, String property, Integer percentile) {
        return (Object) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.property(property)) //
                .add(Restrictions.eq(parentResource.getResourceScope().getLowerCaseName(), parentResource)) //
                .add(Restrictions.isNotNull(property)) //
                .addOrder(Order.asc(property)) //
                .setFirstResult(percentile) //
                .setMaxResults(1) //
                .uniqueResult();
    }
    
    public ApplicationReferee getRefereeByUser(Application application, User user) {
        return (ApplicationReferee) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }
    
}
