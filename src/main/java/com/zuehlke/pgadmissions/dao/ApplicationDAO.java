package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.dto.ApplicationPurgeDTO;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Application getPreviousSubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.ne("id", application.getId())) //
                .addOrder(Order.desc("submittedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Application getPreviousUnsubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("user", application.getUser())) //
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
    
    public ApplicationReferee getRefereeByUser(Application application, User user) {
        return (ApplicationReferee) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }
    
    public List<ApplicationPurgeDTO> getApplicationsToPurge() {
        return (List<ApplicationPurgeDTO>) sessionFactory.getCurrentSession().createCriteria(Application.class, "application") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id")
                        .add(Projections.property("retain"), "retain")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED)) //
                        .add(Restrictions.conjunction()
                                .add(Restrictions.in("state.id",
                                        Arrays.asList(PrismState.APPLICATION_APPROVED_COMPLETED, PrismState.APPLICATION_REJECTED_COMPLETED, //
                                                PrismState.APPLICATION_WITHDRAWN_COMPLETED)))
                                .add(Restrictions.eq("retain", false)))) //
                .list();
    }

}
