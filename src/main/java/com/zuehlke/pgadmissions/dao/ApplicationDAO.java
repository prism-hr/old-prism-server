package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.OtherApplicationSummaryRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Application getPreviousSubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.eq("institution.locale", application.getLocale())) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.not( //
                        Restrictions.in("state.id", Arrays.asList( //
                                PrismState.APPLICATION_APPROVED_COMPLETED_PURGED, //
                                PrismState.APPLICATION_REJECTED_COMPLETED_PURGED, //
                                PrismState.APPLICATION_WITHDRAWN_COMPLETED_PURGED)))) //
                .addOrder(Order.desc("submittedTimestamp")) //
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

    public List<ApplicationReferee> getApplicationRefereesResponded(Application application) {
        return (List<ApplicationReferee>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("comment.applicationRating")) //
                .addOrder(Order.asc("comment.createdTimestamp")) //
                .addOrder(Order.asc("comment.id")) //
                .list();
    }

    public List<ApplicationReferee> getApplicationRefereesNotResponded(Application application) {
        return (List<ApplicationReferee>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNull("comment")) //
                .addOrder(Order.asc("id")) //
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

    public List<Integer> getApplicationsForExport() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.EXPORT_RESOURCE)) //
                .list();
    }

    public ApplicationQualification getLatestApplicationQualification(Application application) {
        return (ApplicationQualification) sessionFactory.getCurrentSession().createCriteria(ApplicationQualification.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("completed", true)) //
                .addOrder(Order.desc("awardDate")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public ApplicationEmploymentPosition getLatestApplicationEmploymentPosition(Application application) {
        return (ApplicationEmploymentPosition) sessionFactory.getCurrentSession().createCriteria(ApplicationEmploymentPosition.class) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("current")) //
                .addOrder(Order.desc("endDate")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Long getProvidedReferenceCount(Application application) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.rowCount()) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_PROVIDE_REFERENCE)) //
                .add(Restrictions.eq("declinedResponse", false)) //
                .uniqueResult();
    }

    public Long getDeclinedReferenceCount(Application application) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.rowCount()) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_PROVIDE_REFERENCE)) //
                .add(Restrictions.eq("declinedResponse", true)) //
                .uniqueResult();
    }

    public List<OtherApplicationSummaryRepresentation> getOtherLiveApplications(Application application) {
        return (List<OtherApplicationSummaryRepresentation>) sessionFactory.getCurrentSession().createCriteria(Application.class, "application") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("program.title"), "program") //
                        .add(Projections.property("project.title"), "project") //
                        .add(Projections.property("applicationRatingCount"), "ratingCount") //
                        .add(Projections.property("applicationRatingAverage"), "ratingAverage") //
                        .add(Projections.property("stateGroup.id"), "stateGroup")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("institution", application.getInstitution())) //
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.ne("id", application.getId())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.between("stateGroup.sequenceOrder", 2, 7)) //
                        .add(Restrictions.in("state.id", Arrays.asList(PrismState.APPLICATION_APPROVAL, PrismState.APPLICATION_REJECTED)))) //
                .addOrder(Order.desc("sequenceIdentifier")) //
                .setResultTransformer(Transformers.aliasToBean(OtherApplicationSummaryRepresentation.class)) //
                .list();
    }

    public List<ApplicationReportListRowDTO> getApplicationReport(Set<Integer> assignedApplications) {
        return (List<ApplicationReportListRowDTO>) sessionFactory
                .getCurrentSession()
                .createQuery( //
                        "select application.id as id, user.fullName as name, user.email as email, nationality.name as nationality, " //
                                + "domicile.name as residence, personalDetail.dateOfBirth as dateOfBirth, gender.name as gender, " //
                                + "institution.title as institution, program.title as program, project.title as project, " //
                                + "studyOption.code as studyOption, referralSource.name as referralSource, application.referrer as referrer, " //
                                + "application.createdTimestamp as createdDate, application.closingDate as closingDate, " //
                                + "application.submittedTimestamp as submittedDate, application.updatedTimestamp as updatedDate, " //
                                + "application.applicationRatingCount as ratingCount, application.applicationRatingAverage as ratingAverage, "
                                + "state.stateGroup.id as state, count(provideReferenceComment.id) as providedReferences, " //
                                + "count(declineReferenceComment.id) as declinedReferences, " //
                                + "verificationProcessing.instanceCount as verificationInstanceCount, " //
                                + "verificationProcessing.dayDurationAverage as verificationInstanceDurationAverage, " //
                                + "referenceProcessing.instanceCount as referenceInstanceCount, " //
                                + "referenceProcessing.dayDurationAverage as referenceInstanceDurationAverage, " //
                                + "reviewProcessing.instanceCount as reviewInstanceCount, " //
                                + "reviewProcessing.dayDurationAverage as reviewInstanceDurationAverage, " //
                                + "interviewProcessing.instanceCount as interviewInstanceCount, " //
                                + "interviewProcessing.dayDurationAverage as interviewInstanceDurationAverage, " //
                                + "approvalProcessing.instanceCount as approvalInstanceCount, " //
                                + "approvalProcessing.dayDurationAverage as approvalInstanceDurationAverage, "
                                + "application.confirmedStartDate as confirmedStartDate, application.confirmedOfferType as confirmedOfferType " //
                                + "from Application as application " + "join application.user as user " //
                                + "left join application.personalDetail as personalDetail " //
                                + "left join personalDetail.firstNationality as nationality " //
                                + "left join personalDetail.domicile as domicile " //
                                + "left join personalDetail.country as country " //
                                + "left join personalDetail.gender as gender " //
                                + "join application.institution as institution " //
                                + "join application.program as program " //
                                + "left join application.project as project " //
                                + "left join application.programDetail as programDetail " //
                                + "left join programDetail.studyOption as studyOption " //
                                + "left join programDetail.referralSource as referralSource " //
                                + "join application.state as state " //
                                + "left join application.comments as provideReferenceComment " //
                                + "with provideReferenceComment.action.id = :provideReferenceAction " //
                                + "and provideReferenceComment.declinedResponse is false " //
                                + "left join application.comments as declineReferenceComment " //
                                + "with declineReferenceComment.action.id = :provideReferenceAction " //
                                + "and declineReferenceComment.declinedResponse is true " //
                                + "left join application.processings as verificationProcessing " //
                                + "with verificationProcessing.stateGroup.id = :verificationStateGroup " //
                                + "left join application.processings as referenceProcessing " //
                                + "with referenceProcessing.stateGroup.id = :referenceStateGroup " //
                                + "left join application.processings as reviewProcessing " //
                                + "with reviewProcessing.stateGroup.id = :reviewStateGroup " //
                                + "left join application.processings as interviewProcessing " //
                                + "with interviewProcessing.stateGroup.id = :interviewStateGroup " //
                                + "left join application.processings as approvalProcessing " //
                                + "with approvalProcessing.stateGroup.id = :approvalStateGroup " //
                                + "where application.id in :assignedApplications " //
                                + "group by application.id " //
                                + "order by application.sequenceIdentifier desc") //
                .setParameterList("assignedApplications", assignedApplications) //
                .setParameter("provideReferenceAction", PrismAction.APPLICATION_PROVIDE_REFERENCE) //
                .setParameter("verificationStateGroup", PrismStateGroup.APPLICATION_VERIFICATION) //
                .setParameter("referenceStateGroup", PrismStateGroup.APPLICATION_REFERENCE) //
                .setParameter("reviewStateGroup", PrismStateGroup.APPLICATION_REVIEW) //
                .setParameter("interviewStateGroup", PrismStateGroup.APPLICATION_INTERVIEW) //
                .setParameter("approvalStateGroup", PrismStateGroup.APPLICATION_APPROVAL) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationReportListRowDTO.class)) //
                .list();
    }

}
